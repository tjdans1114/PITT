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
        System.out.println("FINISHED processingggg...");
        //http_request.header_map.put("connection", "Close");
        handle_connection(http_request);
        System.out.println("finished dequeued");
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

        //String connection = http_request.connection;
        //handle_connection(http_request);
        //////
        return new Event(client, key); //finished
      }
      else if(type == Event.Type.CONTINUATION){
        //System.out.println("CONTINUATION TYPE");
        //System.out.println("THREADING START");
        //TODO : cache maintenance

        int thread_num = get_free_thread();
        if(thread_num != -1){
          THREAD_POOL[thread_num] = true;
          FileThread f = new FileThread(thread_num, http_request, EVENT_QUEUE);
          f.start();
          THREAD_POOL[thread_num] = false;
        }
        else{
          System.out.println("Thread full, re-enqueueing to the queue");
          System.out.println("Error in Continuation : Threading");
          return http_request;
        }
        return null;
      }
      //io
      else if(type == Event.Type.IO){
        //System.out.println("IO TYPE");
        //cacheing
        if(Cache.has(http_request.uri)){
          //TODO : cache maintenance & verification of code behavior

          //1. write first line, headers
          String first_line = "HTTP/1.1 200 OK";//TODO
          String headers = "";//TODO

          ByteBuffer firstline_header_buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          firstline_header_buffer.put((first_line + "\n" + headers + "\n\n").getBytes());
          firstline_header_buffer.flip();

          client.write(firstline_header_buffer);

          ByteBuffer body_buffer = Cache.get(http_request.uri); //TODO : copy not aliasing
          body_buffer.flip();
          client.write(body_buffer);
        }
        else{
          //System.out.println("THREADING START");
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
    }
    catch(Exception ex){
      ex.printStackTrace();
//      ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
//      http_request.error_code = 500;
//      String response_str = HTTPInterpreter.create_response_NON_IO(http_request);
//      System.out.println(response_str);
//      buffer.put(response_str.getBytes());
//
//      buffer.flip();
//
//      try {
//        while (buffer.hasRemaining()) { //TODO : temporarily, write to client with while loop
//          int x = client.write(buffer);
//        }
//      }
//      catch(Exception exx){
//        exx.printStackTrace();
//      }
//
//      //String connection = http_request.connection;
//      HTTPInterpreter.handle_connection(http_request);
//      return new Event(client, key); //Finished
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
    if(http_request.header_map.containsKey("If-Modified-Since")){//debug needed
      String date_string = http_request.header_map.get("If-Modified-Since");
      try{
        //parse request modified date
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date req_date = format.parse(date_string);

        //check file
        Date file_date = new Date(file.lastModified());
        //String file_date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(file.lastModified()));

        if(req_date.equals(file_date)){//does this work?
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
    System.out.println("evaluating handle_connection");
    System.out.println(http_request.type.toString());
    //TODO : twisted logic... i don't know
    Event.Type type = http_request.type;
    if(!(type == Event.Type.FINISHED)){
//      System.out.println("1111111");
      return;
    }
//    System.out.println("151515");
    SocketChannel client = http_request.client;
    //SelectionKey key = http_request.key;
//    if(http_request.header_map.containsKey("connection") &&
//            http_request.header_map.get("connection").equals("keep-alive")){
//      System.out.println("222222");
//      //TODO
//      return; //keep-alive!
//    }
//    System.out.println("333333");
    //close
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

    System.out.println("IO Thread : " + thread_number + " Start");
    SocketChannel client = event.client;
    SelectionKey key = event.key;

    try {
      //Continuation

      int read_start, read_end =0;

      if (event.type == Event.Type.IO) {
        System.out.println("IO");
        //TODO : IO case
        /** 1. open file from event */
        
        String filename = event.uri.substring(1);
        File file = new File(filename);
        //1. 404
        if (!file.exists()) {
          write_error(event,event_queue,404);
        }

        //2. 304
        else if (HTTPInterpreter.try304(event, file)){
          write_error(event,event_queue,304);
        }
        //3. 200
        else{
          String first_line = "HTTP/1.1"+ " " + 200 + " " + "OK";

          //2. header
          String header = "";//TODO

          //TODO header
          String preprocess =  first_line + "\n" +
                  header + "\n";
          ByteBuffer firstline_header_buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          firstline_header_buffer.put(preprocess.getBytes());
          firstline_header_buffer.flip();
          while(firstline_header_buffer.hasRemaining()){
            client.write(firstline_header_buffer);
          }

          System.out.println("200 SENDING....");
          System.out.println(file.getName() + " : file length!");

          MappedByteBuffer MBbuffer;

          FileChannel input_channel = new FileInputStream(filename).getChannel();

          if (file.length() > Global.BUFFER_SIZE){ // large file
            System.out.println("case 1");
            MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, 0, Global.BUFFER_SIZE);

            write(client,MBbuffer);
            event_queue.push(new Event(client, key, input_channel, Global.BUFFER_SIZE, "Keep Alive"));//Continue
          }
          else{//small file
            System.out.println("case 2");
            MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, 0, input_channel.size());
            System.out.println("mapped buffer");

            write(client,MBbuffer);
            event_queue.push(new Event(client, key));//Finished
          }

//          //Don't flip here!
//          while(MBbuffer.hasRemaining()){
//            int x = client.write(MBbuffer);
//          }

//          Cache.set(event.uri, buffer);
//          //String connection = http_request.connection;
//          HTTPInterpreter.handle_connection(event);
        }
      }
      else if (event.type == Event.Type.CONTINUATION) {
        MappedByteBuffer MBbuffer;
        System.out.println("CONTINUATION");
        read_start = event.start;
        System.out.println("read start at : " + read_start);
        FileChannel input_channel = event.file_channel;
        if (input_channel.size() - read_start > Global.BUFFER_SIZE){ // goes in from start IO
          System.out.println("case 1");
          MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, Global.BUFFER_SIZE);
          read_start += Global.BUFFER_SIZE;

          write(client,MBbuffer);
          event_queue.push(new Event(client, key, input_channel, read_start, "Keep Alive"));
        }
        else{
          System.out.println("case 2");
          MBbuffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, input_channel.size()-read_start);

          write(client,MBbuffer);
          event_queue.push(new Event(client, key)); //Finished
        }

////        MBbuffer.flip();
//        System.out.println(client.toString() + " in continuation writing");
//        while(MBbuffer.hasRemaining()){
//          int x = client.write(MBbuffer);
//        }

        Cache.set(event.uri, MBbuffer);
        //String connection = http_request.connection;
        //HTTPInterpreter.handle_connection(event);
      }
      else {//Error Case
        //let's discard this
        //System.out.println("500 Service Unavailable");
        ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
        event.error_code = 500;
        String response_str = HTTPInterpreter.create_response_NON_IO(event);
        System.out.println(response_str);
        buffer.put(response_str.getBytes());

        buffer.flip();

        while (buffer.hasRemaining()) { //TODO : temporarily, write to client with while loop
          int x = client.write(buffer);
        }

        //String connection = http_request.connection;
        //HTTPInterpreter.handle_connection(event);
        event_queue.push(new Event(client, key)); //Finished
      }
    }
    catch(IOException ex){
      ex.printStackTrace();
    }

    System.out.println("IO Thread " + thread_number+ " End");

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
    //TODO : this part & 304 part with NON_IO may be reduced to some 'write_error_to_client' function...
    ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
    event.error_code = error_code;
    String response_str = HTTPInterpreter.create_response_NON_IO(event);
    System.out.println(response_str);
    buffer.put(response_str.getBytes());

    buffer.flip();

    while (buffer.hasRemaining()) { //TODO : temporarily, write to client with while loop
      int x = client.write(buffer);
    }

//          String connection = http_request.connection;
    //HTTPInterpreter.handle_connection(event);
    event_queue.push(new Event(client, key)); //Finished
  }
}