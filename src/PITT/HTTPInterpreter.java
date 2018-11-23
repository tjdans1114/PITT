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
    public int limit;
    public int counter;
    private int thread_array [limit] = [0];
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
      String filename = http_request.uri.substring(1);
      File file = new File(filename);

      //1. 404 //? does it not require finding? thread burden?
      if(!file.exists()){
        status_code = 404;
        return new Response(client, key, http_version, 404, header_map);
      }

      //2. 304
      if(try304(http_request,file)){//debug needed
        //TODO : 304
        status_code = 304;
        return new Response(client, key, http_version, 304, header_map);
      }

      //other headers?
      //206
      if(http_request.header_map.containsKey("Range")){
        //TODO : note that there are If-Range, Content-Range, Range headers
        // read https://svn.apache.org/repos/asf/labs/webarch/trunk/http/draft-fielding-http/p5-range.html for detail
        /*
        under the assumption 
      
        String range = header_map.get("Range");
        int i = range.indexOf("=");
        int j = range.indexOf("-");

        long start = Long.parseLong(range.substring(i + 1, j));
        long end = 0;
        if (j < range.length() - 1) {
          end = Long.parseLong(range.substring(j + 1));
        }
        if (end == 0) {
          end = start + 2 * 1024 * 1024 - 1; 
        }
        if (end > file.length() - 1) {
          end = file.length() - 1; 
        }
        */
        status_code = 206;
      }

      /*else {
        data_code = 200
      }
      */

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

    /*Need to fill IO process, 
  //   get file name through uri,
  //   connect through
  //   FileChannel input_channel = new FileInputStream(file).getChannel;
    // get /logo.png http1.1  new file("logo.png")
  //   */

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
          /*
          value = Cache.get(uri);
          */
        }
        else{
          //TODO : cache maintenance
          if (counter <= limit) {
            int k =0;
            while(thread_array[k]==1){
              k++;}
            thread_array[k]=1;
            FileThread f = new FileThread(k, http_request, EVENT_QUEUE);
            f.start();
          }
          else {
            event_queue.push(http_request);
            System.out.println("Thread full");
          }
          return null;
        }
      }

      int x = client.write(buffer);
      //TODO : create continuation?
      if(buffer.hasRemaining()){
        return new Event(client,key,buffer);
      }

      //client.close?
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
}

public class FileThread extends Thread{
  Event event;
  EventQueue event_queue;
  FileChannel errorChannel;
  File file;
 // MappedByteBuffer buffer_file; //buffer file of error 400, 404, 405

  public FileThread(int thread_number, Event event, EventQueue event_queue){
    this.event = event;
    this.event_queue = event_queue;
  }

//additional test conducted : see for change
  public void run(){
    //TODO
    counter++;
    System.out.println("IO Thread : " + thread_number + " Start");
    //1. open file from event
    String filename = event.uri.substring(1); 
    this.file = new File(filename);
    ByteBuffer buffer = null;
    int read_start, read_end; // read range
    if (file.exists()){//better to operate in here?
      FileChannel inChannel = new FileInputStream(fileName).getChannel();
      //event.size = (int) inChannel.size(); // size of event
      //Cache.set(event.uri, buffer);

      // first time io
      if (event.type==Event.Type.IO){
        if ((file.length() >= Global.BUFFER_SIZE){ // goes in from start IO
          buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, start, Global.BUFFER_SIZE);
          //Mark that it is only read to certain point : call this marker
          event_queue.push(event);
        } else{
          buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, start, end-start);
        }
      }
      else {
        if ((file.length()-marker)>=Global.BUFFER_SIZE){
          buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, marker, Global.BUFFER_SIZE);
          event_queue.push(event);
        }
        else {
          buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, marker, end-marker);
        }
      }
      Cache.set(event.uri, buffer);
      //Need to return the range of file; 
      body.put{
        buffer;
      }
      inChannel.close();
    
      /* Think 404 would be better if located here
    }else {
      buffer = 
    }
    */
    //2. create response message buffer
    
    //3.
    SocketChannel client = event.client;
    SelectionKey key = event.key;
    /*
    Consider Range : how much to return
    */
    try {
      
      if (buffer.hasRemaining()) {
        event_queue.push(new Event(client, key, buffer));
      }
      // ProcessEvent(event);

      /*
      has a data buffer : require return header
      header buffer.flip : get the last info;
      long size = header.flip.limit() + buffer.flip.limit();
      or
      long size = header.pos() + buffer.pos();

      long input_size = client.write(data)

      if (input_size < size){
        body.put(header);
        body.put(buffer);
        event.key.attach(event);
      }
      try {
				event.key.interestOps(SelectionKey.OP_WRITE);
				event.key.selector().wakeup();
      */

    }
    catch(Exception e){//IOException | InterruptedException e
      e.printStackTrace();
      event.key.attach(null);
			event.key.cancel();
			event.key.channel().close();
    }
    /*
    public boolean modified(Event event) throws IOException, InterruptedException {
      File file = new File(event.uri.substring(1));
      if (file.exists()) {
        System.out.println("File Exist");
        String date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(file.lastModified()));
        event.header_map.put("If-Modified-Since", date);
        return true;
      } else {
        return false;
      }*/
      System.out.println("IO Thread" + thread_number+ "End");
      thread_array[thread_number] = 0;
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