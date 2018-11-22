package PITT;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.text.*;

public class HTTPInterpreter{
  //TODO : interpret parsed Event
  public static Response create_response(Event http_request){
    //only for NON_IO, (IO?)
    SocketChannel client = http_request.client;
    SelectionKey key = http_request.key;

    String http_version = "HTTP/1.1";
    int status_code;
    TreeMap<String,String> header_map = new TreeMap<String,String>();
    ByteBuffer body = ByteBuffer.allocate(Global.BUFFER_SIZE);

    /** case : parse error occurred */
    if(http_request.error_code != 200){
      status_code = http_request.error_code;
      String status_str = Global.http_status_map.get(status_code);

      //TODO : study how to use ByteBuffer!!!!!!!
      //first line + header
      header_map.put("a","b");

      return new Response(client,key,http_version,status_code,header_map);
    }

    /** parser error code 200 */
    status_code = 200; //TODO

    //append headers???
    header_map.put("Connection","keep-alive");

    /*process body*/
    //1. check cache
    if(Cache.has(http_request.uri)){
      body.put(
              Cache.get(http_request.uri)
      );
    }
    else {
      //involves file
      File file = new File(http_request.uri);

      //1. 404
      if(!file.exists()){
        //TODO : not found 404
        return new Response(client, key, http_version, 404, header_map);
      }

      //2. 304
      if(try304(http_request,file)){//debug needed
        //TODO : 304
        return new Response(client, key, http_version, 304, header_map);
      }

      //other headers?
      //206
      if(http_request.header_map.containsKey("Range")){
        //TODO : note that there are If-Range, Content-Range, Range headers
        // read https://svn.apache.org/repos/asf/labs/webarch/trunk/http/draft-fielding-http/p5-range.html for detail
        status_code = 206;
      }


      ///////////////////////////////////////////////////////////////
      body.put(
              ("Not Implemented yet sorry...").getBytes()
      );
    }

    return new Response(client,key,http_version,status_code,header_map,body);
  }

  public static Event respond(Event http_request, EventQueue EVENT_QUEUE){
    Event.Type type = http_request.type;
    SocketChannel client = http_request.client;
    SelectionKey key = http_request.key;

    ByteBuffer buffer = ByteBuffer.allocate(0);
    try{
      if(type == Event.Type.NON_IO){
        Response response = create_response(http_request);
        buffer = response.get_message();
      }
      else if(type == Event.Type.CONTINUATION){
        //no need to execute create_response! just write the body into the socket!
        buffer = http_request.resp_body;
      }
      //io
      else if(type == Event.Type.IO){
        //cacheing
        if(Cache.has(http_request.uri)){
          buffer = http_request.resp_body;
        }
        else{
          //TODO : cache maintenance
          FileThread f = new FileThread(http_request, EVENT_QUEUE);
          f.start();
          return null;
        }
      }

      int x = client.write(buffer);
      //TODO : create continuation?
      String connection = http_request.connection;
      if(buffer.hasRemaining()){
        return new Event(client,key,buffer,connection);
      }
      else{
        handle_connection(http_request);
      }

    }
    catch(Exception ex){
      //TODO
    }

    return null;
  }

  public static boolean try304(Event http_request, File file){//debug needed
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
      catch(ParseException pe){// or other exception
        // date format invalid.
        // ignore if-modified-since header?
        // or make this 400

      }
    }

    //304 failed
    return false;
  }
  private static void handle_connection(Event http_request){
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
    }

  }
}

class FileThread extends Thread{
  private static final int THREAD_MAX = 4;
  private static int THREAD_COUNT = 0;

  Event event;
  EventQueue event_queue;
  FileChannel errorChannel;
  File file;
 // MappedByteBuffer buffer_file; //buffer file of error 400, 404, 405

  public FileThread(Event event, EventQueue event_queue){
    this.event = event;
    this.event_queue = event_queue;
  //  /* errorChannel = new FileInputStream("400.html").getChannel();
	// 	buffer400 = errorChannel.map(FileChannel.MapMode.READ_ONLY, 0, errorChannel.size());
  //   */
  //   errorChannel.close();
  }

//additional test conducted : see for change
  public void run(){
    //TODO
    //1. open file from event
    File file = null;
    //2. create response message buffer
    ByteBuffer buffer = null;
    //3.
    SocketChannel client = event.client;
    SelectionKey key = event.key;
    try {
      int x = client.write(buffer);

      if (buffer.hasRemaining()) {
        event_queue.push(new Event(client, key, buffer, event.connection));
      }
      // ProcessEvent(event);
    }
    catch(Exception e){//IOException | InterruptedException e
      e.printStackTrace();

    }
  }
  // public void IOProcess(Event event)throws IOException, InterruptedException {
  //   /*Need to fill IO process, 
  //   get file name through uri,
  //   connect through
  //   FileChannel input_channel = new FileInputStream(file).getChannel;
    
  //   */
  // }

  // public void ProcessEvent(Event event) throws IOException, InterruptedException {
  //   ByteBuffer[] buffer;
  //   if (!event.error_code){ //If the problem does not have error code
  //     if (try304(http_request,file)){ //Not Modified
  //       //data = response 304 
  //     }
  //     else { //Good Respond
  //       //data = respond 200
  //     }
  //   }
  //   else {
  //     //data = respond according to error_code
  //   }
  // }
}