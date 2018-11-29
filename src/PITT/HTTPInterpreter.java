package PITT;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.text.*;

public class HTTPInterpreter{
  //TODO : interpret parsed Event
  private static final boolean THREAD_POOL[] = new boolean[FileThread.THREAD_MAX];

//  public static 1 create_response(Event http_request){
//    //only for NON_IO, (IO?)
//    System.out.println("creating response");
//    SocketChannel client = http_request.client;
//    SelectionKey key = http_request.key;
//    public int limit;
//    public int counter;
//    private int thread_array [limit] = [0];
//    String http_version = "HTTP/1.1";
//    int status_code;
//    TreeMap<String,String> header_map = new TreeMap<String,String>();
//    ByteBuffer body = ByteBuffer.allocate(Global.BUFFER_SIZE);
//
//    /** case : parse error occurred */
//    if(http_request.error_code != 200){
//      status_code = http_request.error_code;
//      String status_str = Global.http_status_map.get(status_code);
//
//      //TODO : study how to use ByteBuffer!!!!!!!
//      //first line + header
//      header_map.put("a","b");
//
//      return new Response(client,key,http_version,status_code,header_map);
//    }
//
//    /** parser error code 200 */
//    status_code = 200; //TODO
//
//    //append headers???
//    header_map.put("Connection","keep-alive");
//
//    /*process body*/
//    //1. check cache
//    if(Cache.has(http_request.uri)){
//      body.put(
//              Cache.get(http_request.uri)
//      );
//    }
//    else {
//      //involves file
//      String filename = http_request.uri.substring(1);
//      File file = new File(filename);
//
//      //1. 404 //? does it not require finding? thread burden?
//      if(!file.exists()){
//        status_code = 404;
//        return new Response(client, key, http_version, 404, header_map);
//      }
//
//      //2. 304
//      if(try304(http_request,file)){//debug needed
//        //TODO : 304
//        status_code = 304;
//        return new Response(client, key, http_version, 304, header_map);
//      }
//
//      //other headers?
//      //206
//      if(http_request.header_map.containsKey("Range")){
//        //TODO : note that there are If-Range, Content-Range, Range headers
//        // read https://svn.apache.org/repos/asf/labs/webarch/trunk/http/draft-fielding-http/p5-range.html for detail
//        /*
//        under the assumption
//
//        String range = header_map.get("Range");
//        int i = range.indexOf("=");
//        int j = range.indexOf("-");
//
//        long start = Long.parseLong(range.substring(i + 1, j));
//        long end = 0;
//        if (j < range.length() - 1) {
//          end = Long.parseLong(range.substring(j + 1));
//        }
//        if (end == 0) {
//          end = start + 2 * 1024 * 1024 - 1;
//        }
//        if (end > file.length() - 1) {
//          end = file.length() - 1;
//        }
//        */
//        status_code = 206;
//      }
//
//      /*else {
//        data_code = 200
//      }
//      */
//
//      ///////////////////////////////////////////////////////////////
//      body.put(
//              ("Not Implemented yet sorry...").getBytes()
//      );
//    }
//
//    return new Response(client,key,http_version,status_code,header_map,body);
//  }

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
    Event new_event = null;
    try{
      if (type == Event.Type.FINISHED){
        //System.out.println("Finished IO Job");
        http_request.header_map.put("Connection", "Close");
        handle_connection(http_request);
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
        handle_connection(http_request);
        //////
        new_event = Event(client, key); //finished
        return new_event;
      }
      else if(type == Event.Type.CONTINUATION){
        //System.out.println("CONTINUATION TYPE");
        //System.out.println("THREADING START");
        //TODO : cache maintenance

        int thread_num = get_free_thread();
        if(thread_num != -1){
          THREAD_POOL[thread_num] = true;
          FileThread f = new FileThread(thread_num, http_request, EVENT_QUEUE);
          new_event = f.start();
          THREAD_POOL[thread_num] = false;
          return new_event;
        }
        else{
          System.out.println("Thread full, re-enqueueing to the queue");
          return http_request;
        }
        System.out.println("Error in Continuation : Threading");
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
        else {

        }
      }
    }
    catch(Exception ex){
      ex.printStackTrace();
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
      HTTPInterpreter.handle_connection(event);
      new_event = Event(client, key); //Finished
      return new_event;

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
    //TODO : twisted logic... i don't know
    Event.Type type = http_request.type;
    if(!(type == Event.Type.IO || type == Event.Type.NON_IO)){
      return;
    }

    SocketChannel client = http_request.client;
    //SelectionKey key = http_request.key;
    if(http_request.header_map.containsKey("Connection") &&
            http_request.header_map.get("Connection").equals("keep-alive")){
      //TODO
      return; //keep-alive!
    }

    //close
    try {
      client.close();
    }
    catch(IOException ex){
      //TODO
      ex.printStackTrace();
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
      HTTPInterpreter.handle_connection(event);
      new_event = Event(client, key); //Finished
      return new_event;
    }

  }
}

class FileThread extends Thread{
  public static final int THREAD_MAX = 4;
  private static int THREAD_COUNT = 0;
  public int thread_number;

  Event event;
  EventQueue event_queue;
  FileChannel errorChannel;
  File file;

  public FileThread(int thread_number, Event event, EventQueue event_queue){
    this.thread_number = thread_number;

    this.event = event;
    this.event_queue = event_queue;
  }

  //additional test conducted : see for change
  public Event run(){
    //TODO :
    THREAD_COUNT++; //manage counter
    Event new_event;
    //System.out.println("IO Thread : " + thread_number + " Start");
    SocketChannel client = event.client;
    SelectionKey key = event.key;
    /** NAHYUNSOO : do it ! ********************************************************************/
    try {
      int read_start, read_end; // read range
      //Continuation
      if (event.type == Event.Type.CONTINUATION) {
        read_start = event.start;
        FileChannel input_channel = event.file_channel;
        if ((file.length()-read_start > Global.BUFFER_SIZE){ // goes in from start IO
          buffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, Global.BUFFER_SIZE);
          read_start += Global.BUFFER_SIZE;
          new_event = Event(client, key, input_channel, start, "Keep Alive");
        }
        else{
          buffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, read_end-read_start+1);
          new_event = Event(client, key); //Finished
        }
        
        buffer.flip();

        while(buffer.hasRemaining()){
          int x = client.write(buffer);
        }
        
        Cache.set(event.uri, buffer);
        //String connection = http_request.connection;
        HTTPInterpreter.handle_connection(event);
        return new_event;
      }
      else if (event.type == Event.Type.IO) {
        //TODO : IO case
        /** 1. open file from event */
        
        String filename = event.uri.substring(1);
        this.file = new File(filename);
        //1. 404
        if (!file.exists()) {
          //TODO : this part & 304 part with NON_IO may be reduced to some 'write_error_to_client' function...
          ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          event.error_code = 404;
          String response_str = HTTPInterpreter.create_response_NON_IO(event);
          System.out.println(response_str);
          buffer.put(response_str.getBytes());

          buffer.flip();
          

          while (buffer.hasRemaining()) { //TODO : temporarily, write to client with while loop
//            System.out.println(client);
//            System.out.println(client.isOpen()?"client is open" : "client is not open");
//            System.out.println(client.isConnected()?"client is connected" : "client is not connected");
            int x = client.write(buffer);
            System.out.println(x + " bytes");
          }

//          String connection = http_request.connection;
          HTTPInterpreter.handle_connection(event);
          new_event = Event(client, key); //Finished
        }

        //2. 304
        else if (HTTPInterpreter.try304(event, file)){
          ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          event.error_code = 304;
          String response_str = HTTPInterpreter.create_response_NON_IO(event);
          System.out.println(response_str);
          buffer.put(response_str.getBytes());

          buffer.flip();
          

          while (buffer.hasRemaining()) { //TODO : temporarily, write to client with while loop
            int x = client.write(buffer);
          }
          
          //String connection = http_request.connection;
          HTTPInterpreter.handle_connection(event);
          new_event = Event(client, key); //Finished
        }
        //3. 200
        else{
          read_start = 0;
          read_end = file.size() -1;
          ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          FileChannel input_channel = new FileInputStream(filename).getChannel();
          if ((file.length()-read_start > Global.BUFFER_SIZE){ // goes in from start IO
            buffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, Global.BUFFER_SIZE);
            read_start += Global.BUFFER_SIZE;
            new_event = Event(client, key, input_channel, start, "Keep Alive");//Continue
          }
          else{
            buffer = input_channel.map(FileChannel.MapMode.READ_ONLY, read_start, read_end-read_start+1);
            new_event = Event(client, key); //Finished
            event_queue.push(new_event);
          }

          //ByteBuffer header_buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          //event.error_code = 200;
          //String response_str = HTTPInterpreter.create_response_NON_IO(event);
          //System.out.println(response_str);
          //header_buffer.put(response_str.getBytes());

//         header_buffer.flip();
//         header_buffer.rewind();

          String first_line = "HTTP/1.1 200 OK";//TODO
          String headers = "";//TODO

          ByteBuffer firstline_header_buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
          firstline_header_buffer.put((first_line + "\n" + headers + "\n\n").getBytes());

          client.write(firstline_header_buffer);
          
          buffer.flip();
          

//          while (header_buffer.hasRemaining()) { //TODO : temporarily, write to client with while loop
//            int x = client.write(header_buffer);
//          }

          while(buffer.hasRemaining()){
            int x = client.write(buffer);
          }

          Cache.set(event.uri, buffer);
          //String connection = http_request.connection;
          HTTPInterpreter.handle_connection(event);
          return new_event;
        }
      }
      else {//Error Case
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
        HTTPInterpreter.handle_connection(event);
        new_event = Event(client, key); //Finished
        return new_event;
      }
    }
    catch(IOException ex){
      ex.printStackTrace();

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
      HTTPInterpreter.handle_connection(event);
      new_event = Event(client, key); //Finished
      return new_event;
    }

    System.out.println("IO Thread " + thread_number+ " End");

    /************************************************************************/
    THREAD_COUNT--; //manage counter
  }
}