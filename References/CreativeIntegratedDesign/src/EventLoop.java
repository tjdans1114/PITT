import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventLoop extends Thread {
	EventQueue eventQueue;
	BlockingQueue<Event> IOQueue = new LinkedBlockingQueue<Event>();
	LRUCache Cache = new LRUCache();
	FileChannel errorChannel;
	MappedByteBuffer buffer400, buffer404, buffer405;
	
	@SuppressWarnings("resource")
	public EventLoop(EventQueue eventQueue) throws IOException {
		this.eventQueue = eventQueue;
		errorChannel = new FileInputStream("400.html").getChannel();
		buffer400 = errorChannel.map(FileChannel.MapMode.READ_ONLY, 0, errorChannel.size());
		errorChannel = new FileInputStream("404.html").getChannel();
		buffer404 = errorChannel.map(FileChannel.MapMode.READ_ONLY, 0, errorChannel.size());
		errorChannel = new FileInputStream("405.html").getChannel();
		buffer405 = errorChannel.map(FileChannel.MapMode.READ_ONLY, 0, errorChannel.size());
		errorChannel.close();
		Thread IOthread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("EventLoop IOThread Running...");
					read();
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		IOthread.start();
	}
	
	public void run() {
		System.out.println("EventLoop Main thread Running...");
		while (true) {
			try {
				GoLoop();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void GoLoop() throws IOException, InterruptedException {
		//System.out.println("Event Dequeueing....");
		Event event = eventQueue.dequeue();
		//System.out.println("Event Dequeued!");
		if (event.blocking) {
			try {
				// 304 NOT MODIFIED should be implemented
				ByteBuffer cache_response = Cache.get(event.uri);
				if (cache_response == null) {
					IOQueue.put(event);	
				} else {
					cache_response.rewind();
					event.response_data[1] = cache_response;
					event.errCode = 0;
					event.blocking = false;
					ProcessEvent(event);
					connection(event);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No IO event implementing");
			ProcessEvent(event);
			connection(event);
		}
	}
	
	public void ProcessEvent(Event event) throws IOException, InterruptedException {
		ByteBuffer[] data;
		if (event.errCode == 0) {
			if (event.Range[0] != 0) {
				data = HTTPResponse.respond(206, event);
			} else if (Response304(event)) {
				data = HTTPResponse.respond(304, event);
			} else {
				data = HTTPResponse.respond(200, event);
			}			
		} else {
			data = HTTPResponse.respond(event.errCode, event);
		}
		
//		event.socket.blockingLock();
//		event.socket.configureBlocking(true);
		long size = data[0].limit() + data[1].limit();
		//data[1] = (ByteBuffer) data[1].position(event.Range[0]);
		//System.out.println(data[0].position()+data[1].position());
		long data_size = event.socket.write(data);
		//System.out.println("DS:" + data_size);
		if (data_size < size) {
			event.response_data = data;
			event.key.attach(event);
			try {
				event.key.interestOps(SelectionKey.OP_WRITE);
				event.key.selector().wakeup();
			} catch (Exception e) {
				event.key.attach(null);
				event.key.cancel();
				event.key.channel().close();
				//e.printStackTrace();
			}
		}
	}
	
	public void connection(Event event) throws IOException, InterruptedException {
		if (!event.connection.equals("keep-alive") || event.errCode > 0) {
			event.socket.close();
		}
	}
	
	public void read() throws InterruptedException, IOException {
		while (true) {
			read_helper();
		}
	}
	
	public void read_helper() throws InterruptedException, IOException {
		Event event = IOQueue.take();
		event = DoIO(event);
		ProcessEvent(event);
	}
	
	public Event DoIO(Event event) throws IOException, InterruptedException {
		String fileName = event.uri.substring(1);
		// File file = new File(fileName);
		//System.out.println("Process IO....");
		if (event.errCode > 0) {
			event.response_data[1] = buffer400;
			event.Range[0] = 0; event.Range[1] = buffer400.capacity();
			event.errCode = 400;
		}
		else if (!event.method.equals("GET")) {
			event.response_data[1] = buffer405;
			event.Range[0] = 0; event.Range[1] = buffer405.capacity();
			event.errCode = 405;
		}
		else if (ModifiedResponse(event)) {
			@SuppressWarnings("resource")
			FileChannel inChannel = new FileInputStream(fileName).getChannel();
			event.size = (int) inChannel.size();
			MappedByteBuffer buffer;
			if (event.Range[0] == 0) {
				buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
				Cache.set(event.uri, buffer);
			} else {
				if (event.Range[1] == 0) {
					buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, event.Range[0], inChannel.size() - event.Range[0]);
					//System.out.println(event.Range[0]);
					//System.out.println(inChannel.size());
				} else {
					buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, event.Range[0], event.Range[1] - event.Range[0]);
				}
			}
			event.Range[1] = event.Range[0] + buffer.capacity();
			//System.out.printf("buffer size: %d\n", buffer.limit());
			//buffer.rewind();
			event.response_data[1] = buffer;
			inChannel.close();
			
			event.errCode = 0;
		} else {
			event.response_data[1] = buffer404;
			event.Range[0] = 0; event.Range[1] = buffer404.capacity();
			event.errCode = 404;
		}
		event.blocking = false;
		return event;
	}
	
	public boolean ModifiedResponse(Event event) throws IOException, InterruptedException {
		File file = new File(event.uri.substring(1));
		if (file.exists()) {
			String d = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(file.lastModified()));
			//System.out.println(d);
			event.LastModified = d;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean Response304(Event event) throws IOException, InterruptedException {
		File file = new File(event.uri.substring(1));
		String d = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(file.lastModified()));
		event.LastModified = d;
		if (event.ModifedSince == null || event.ModifedSince.compareTo(d) < 0) {
			return false;
		} else {
			return true;
		}
	}
}
