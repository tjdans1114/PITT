package PITT;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.text.*;

public class HTTPInterpreter{
  //TODO : interpret parsed Event
  private static final boolean THREAD_POOL[] = new boolean[FileThread.THREAD_MAX];

  public static String create_response_NON_IO(Event http_request){
    //only for NON_IO, (IO?)
    System.out.println("creating NON_IO response");
    SocketChannel client = http_request.client;
    SelectionKey key = http_request.key;

    TreeMap<String,String> header_map = new TreeMap<String,String>();
    //ByteBuffer body = ByteBuffer.allocate(Global.BUFFER_SIZE);

    int status_code = http_request.error_code;
    String status_str = Global.http_status_map.get(status_code);
    String http_version = "HTTP/1.1";

    //1. first line
    String first_line = http_version + " " + status_code + " " + status_str;

    //2. header
    String header = "";//TODO

    //3. body
    String body = Global.ERROR_HTML_MAP.get(status_code);

    //TODO header
    return first_line + "\n" +
            header + "\n" +
            body;
  }

  public static Event respond(Event http_request, EventQueue EVENT_QUEUE){
    //TODO : add last-modified header to the response
    //System.out.println("respond responding...");
    //System.out.println(http_request.error_code);
    Event.Type type = http_request.type;
    SocketChannel client = http_request.client;
    SelectionKey key = http_request.key;
    try{
      if (type == Event.Type.FINISHED){
//        System.out.println("FINISHED processingggg...");
        //http_request.header_map.put("connection", "Close");
        handle_connection(http_request);
//        System.out.println("finished dequeued");
      }

      else if(type == Event.Type.NON_IO){
        //System.out.println("NON IO TYPE");
        ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
        String response_str = create_response_NON_IO(http_request);
        System.out.println(response_str);
        buffer.put(response_str.getBytes());

        buffer.flip();
        while(buffer.hasRemaining()){ //TODO : temporarily, write to client with while loop
          int x = client.write(buffer);
        }

        return new Event(client, key); //finished
      }
      else if(type == Event.Type.CONTINUATION){
        int thread_num = get_free_thread();
        if(thread_num != -1){
          THREAD_POOL[thread_num] = true;
          FileThread f = new FileThread(thread_num, http_request, EVENT_QUEUE);
          f.start();
          THREAD_POOL[thread_num] = false;
        }
        else{
          System.out.println("Thread full, re-enqueueing to the queue");
          return http_request;
        }
        return null;
      }
      //io
      else if(type == Event.Type.IO){
          int thread_num = get_free_thread();
          if(thread_num != -1){
            THREAD_POOL[thread_num] = true;
            FileThread f = new FileThread(thread_num, http_request, EVENT_QUEUE);
            f.start();
            THREAD_POOL[thread_num] = false;
          }
          else{
            System.out.println("Thread full, reenqueueing to the queue");
            EVENT_QUEUE.push(http_request);
          }
          return null;
        }
    }
    catch(Exception ex){
      ex.printStackTrace();
    }

    return null;
  }

  private static int get_free_thread(){//if unavailable, return -1
    for(int i=0;i<THREAD_POOL.length;i++){
      if(THREAD_POOL[i] == false){
        return i;
      }
    }

    return -1;
  }

  public static boolean try304(Event http_request, File file){//TODO : confirm logic
    System.out.println("try 304......");
    if(http_request.header_map.containsKey("if-modified-since")){//debug needed
      System.out.println("try 304......222");

      String date_string = http_request.header_map.get("if-modified-since");
      try{
        //parse request modified date
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date req_date = format.parse(date_string);

        //check file
        Date file_date = new Date(file.lastModified());
        //String file_date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(file.lastModified()));

        System.out.println("try 304!");
        if(req_date.equals(file_date)){//does this work?
          System.out.println("304 success!");
          return true;
        }
      }
      catch(Exception ex){// or other exception
        ex.printStackTrace();
        // date format invalid.
        // ignore if-modified-since header?
        // or make this 400
      }
    }

    //304 failed
    return false;
  }

  public static void handle_connection(Event http_request){
    //TODO :
    Event.Type type = http_request.type;
    if(!(type == Event.Type.FINISHED)){
      return;
    }

    SocketChannel client = http_request.client;

    try {
      System.out.println("closing client : "+ client);
      client.close();
    }
    catch(IOException ex){
      ex.printStackTrace();
    }

  }
}

class FileThread extends Thread{
  public static final int THREAD_MAX = 4;
  private static int THREAD_COUNT = 0;
  public int thread_number;

  Event event;
  EventQueue event_queue;

  public FileThread(int thread_number, Event event, EventQueue event_queue){
    this.thread_number = thread_number;

    this.event = event;
    this.event_queue = event_queue;
  }

  public void run(){
    //TODO :
    THREAD_COUNT++; //manage counter

//    System.out.println("IO Thread : " + thread_number + " Start");
    SocketChannel client = event.client;
    SelectionKey key = event.key;

    try {
      //Continuation

      int read_start, read_end =0;

      if (event.type == Event.Type.IO) {

        String filename = event.uri.substring(1);
        File file = new File(filename);
        //1. 404
        if (!file.exists()) {
          write_error(event,event_queue,404);

          Cache.set(event.uri,null, null); // erase from the cache
        }
        //2. 304
        else if (HTTPInterpreter.try304(event, file)){
          write_error(event,event_queue,304);
        }
        //3. 200 + cache try
        else {
          Date date = new Date(file.lastModified());

          if (Cache.has(event.uri, date)) {//cache hit
            System.out.println("cache hit!");
            //1. write first line, headers
            String first_line = "HTTP/1.1 200 OK";
            String date_string = get_time_string(new Date(file.lastModified()));
            String headers = "Last-Modified: " + date_string;

            ByteBuffer firstline_header_buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
            firstline_header_buffer.put((first_line + "\n" +  headers + "\n\n").getBytes());
            firstline_header_buffer.flip();
            client.write(firstline_header_buffer);

            ByteBuffer body_buffer = Cache.get(event.uri,date); //copy not aliasing

            write(client,body_buffer);
            event_queue.push(new Event(client, key));//Finished
          }
          //4. 200 + cache fail
          else {//cache miss
            System.out.println("cache miss!");
            String first_line = "HTTP/1.1" + " " + 200 + " " + "OK";

            //2. header
            //String headers = "Content-Type: text/plain";//TODO
            String date_string = get_time_string(new Date(file.lastModified()));
            String headers = "Last-Modified: " + date_string;

            //TODO header
            String preprocess = first_line + "\n" +
                    headers + "\n\n";
            ByteBuffer firstline_header_buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
            firstline_header_buffer.put(preprocess.getBytes());
            firstline_header_buffer.flip();
            while (firstline_header_buffer.hasRemaining()) {
              client.write(firstline_header_buffer);
            }

            MappedByteBuffer MBbuffer;

            FileChannel input_channel = new FileInputStream(filename).getChannel();

            if (file.length() > Global.BUFFER_SIZE) { // large file
              System.out.println("case 1");
              MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, 0, Global.BUFFER_SIZE);

              write(client, MBbuffer);
              event_queue.push(new Event(client, key, input_channel, Global.BUFFER_SIZE, "Keep Alive"));//Continue
            }
            else {//small file
              System.out.println("case 2");
              MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, 0, input_channel.size());

              Cache.set(event.uri,MBbuffer,date);//maintain cache

              write(client, MBbuffer);
              event_queue.push(new Event(client, key));//Finished

            }
          }
        }
      }
      else if (event.type == Event.Type.CONTINUATION) {
        System.out.println("case CONT");
        MappedByteBuffer MBbuffer;

        read_start = event.start;
//        System.out.println("read start at : " + read_start);
        FileChannel input_channel = event.file_channel;
        if (input_channel.size() - read_start > Global.BUFFER_SIZE){ // goes in from start IO
          MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, Global.BUFFER_SIZE);
          read_start += Global.BUFFER_SIZE;

          write(client,MBbuffer);
          event_queue.push(new Event(client, key, input_channel, read_start, "Keep Alive"));
        }
        else{
          MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, input_channel.size()-read_start);

          System.out.println(client.isOpen()?"open":"not open");
          System.out.println(client.isConnected()?"connected":"not connected");
          System.out.println(client.toString());
          write(client,MBbuffer);
          event_queue.push(new Event(client, key)); //Finished
        }
      }
      else {//Error Case
        //let's discard this
        //System.out.println("500 Service Unavailable");
        ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
        event.error_code = 500;
        String response_str = HTTPInterpreter.create_response_NON_IO(event);
//        System.out.println(response_str);
        buffer.put(response_str.getBytes());

        buffer.flip();

        write(client,buffer);
        event_queue.push(new Event(client, key)); //Finished
      }
    }
    catch(IOException ex){
      ex.printStackTrace();
    }

    //System.out.println("IO Thread " + thread_number+ " End");

    /************************************************************************/
    THREAD_COUNT--; //manage counter
  }

  private static void write(SocketChannel client, ByteBuffer buffer) throws IOException {
    while(buffer.hasRemaining()){
      int x = client.write(buffer);
    }
  }

  private static void write_error(Event event, EventQueue event_queue, int error_code) throws IOException{
    SocketChannel client = event.client;
    SelectionKey key = event.key;
    ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
    event.error_code = error_code;
    String response_str = HTTPInterpreter.create_response_NON_IO(event);
//    System.out.println(response_str);
    buffer.put(response_str.getBytes());

    buffer.flip();

    write(client,buffer);
//          String connection = http_request.connection;
    //HTTPInterpreter.handle_connection(event);
    event_queue.push(new Event(client, key)); //Finished
  }

  private static String get_time_string(Date date){
    SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    return sdf.format(date);
  }
}