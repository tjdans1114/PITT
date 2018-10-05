import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Iterator;
import java.net.HttpURLConnection;

public class Main {

	final static String CRLF = "\r\n";
	static EventQueue eventQueue = new EventQueue();
	static EventLoop eventLoop;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerSocketChannel serverSocketChannel = null;
		SocketChannel client = null;
		ServerSocket serverSocket = null;
		Selector selector = null;
		
		eventLoop = new EventLoop(eventQueue);
		
		int port = 4000;
		String ip = "127.0.0.1";
		
		
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			
			serverSocketChannel.configureBlocking(false);
			
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(ip, port));
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("Server Socket opening....");
			
			eventLoop.start();
			int i = 1;
			
			while (true) {
				try {
					selector.select();
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					
					while (iterator.hasNext()) {
						SelectionKey key = (SelectionKey) iterator.next();
						try {
							if (key.isAcceptable()) {
								client = ((ServerSocketChannel)key.channel()).accept();
								if (client != null) {
									client.configureBlocking(false);
									client.register(selector, SelectionKey.OP_READ);
									//System.out.printf("%d: Client %s connected\n", i, client.toString());
									i++;
									//run(client);
								}
							}
							else if (key.isReadable()) {
								//System.out.println("READ");
								client = (SocketChannel) key.channel();
								run(client, key);
							}
							else if (key.isWritable()) {
								Event event = (Event) key.attachment();
								ByteBuffer[] data = event.response_data;
								long position = data[0].position() + data[1].position();
								long size = data[0].limit() + data[1].limit();
								long data_size = event.socket.write(data);
								//System.out.println(position);
								//System.out.println(size);
								//System.out.println(data_size);
								if (position == size) {
									//System.out.println("WRITINGaa");
									key.interestOps(SelectionKey.OP_READ);
									key.attach(null);
									eventLoop.connection(event);	
								} else {
									//System.out.println("WRITE");
									//System.out.println(data[1].position());
								}
							}
						} catch (Exception e) {
							//e.printStackTrace();
							//System.out.println("Key Cancelled");
							//System.out.println(e.toString());
							key.cancel();
							key.channel().close();
						}	
					}
					iterator.remove();					
				} catch (Exception e) {
					//e.printStackTrace();
					//System.out.println("Cannot remove iter");
				}
			}
		
	}
	
	public static void run(SocketChannel client, SelectionKey key) throws IOException {
		//System.out.println("Run stage");
		ByteBuffer mBuf = ByteBuffer.allocate(10240);
		//mBuf.rewind();
		int data_size;
		try {
			data_size = client.read(mBuf);
		} catch (IOException e) {
			client.close();	
			//System.out.println("Read Exception?");
			return;
		}
		mBuf.flip();
		String read_data = StandardCharsets.UTF_8.decode(mBuf).toString();
		//System.out.println(read_data);
		if (data_size == -1) { // error handling
			//System.out.println("Read Error?");
			client.close();
			return;
		}
		if (data_size == 0) {
			//System.out.println("CLOSED");
			//System.out.println(read_data);
			//client.close();
		} else {
			//System.out.println("Success Request");
			HttpParser parser = new HttpParser();
			Event event = parser.Parse(read_data);
			event.socket = client;
			event.key = key;
			eventQueue.enqueue(event);
		}
	}
}