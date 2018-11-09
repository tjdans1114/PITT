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

      body.put((
        "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
        "<html>\n" +
        "\n" +
        "<head>\n" +
        "   <title>"+status_code+" "+status_str+"</title>\n" +
        "</head>\n" +
        "\n" +
        "<body>\n" +
        "   <h1>"+status_str+"</h1>\n" +
        "   <p>"+status_str/*The requested URL /t.html was not found on this server.*/+"</p>\n" +
        "</body>\n" +
        "\n" +
        "</html>"
      ).getBytes());

      return new Response(client,key,http_version,status_code,header_map,body);
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
      if(file == null){
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

  public static Event respond(Event http_request){
    Event.Type type = http_request.type;
    SocketChannel client = http_request.client;
    SelectionKey key = http_request.key;

    //non-io
    if(type == Event.Type.NON_IO){
      Response response = create_response(http_request);

      try{
        ByteBuffer buffer = response.get_message();
        int x = client.write(buffer);
        //TODO : create continuation?
        if(buffer.hasRemaining()){
          return new Event(client,key,buffer);
        }

        //client.close?
        return null;
      }
      catch(Exception ex){
        return null;
      }
    }
    //finished

    //cont
    else if(type == Event.Type.CONTINUATION){
      //no need to execute create_response! just write the body into the socket!
      try{
        ByteBuffer buffer = http_request.resp_body;
        int x = client.write(buffer);
        //TODO : create continuation?
        if(buffer.hasRemaining()){
          return new Event(client,key,buffer);
        }

        //client.close?
        return null;
      }
      catch(Exception ex){
        return null;
      }
    }
    //io
    else if(type == Event.Type.IO) {
      FileThread f = new FileThread(http_request);
      f.start();
      return null;
    }
    /*
    else if(type == Event.Type.FINISHED){
      //nothing to do! or 2 blank lines?
      return null;
    }
    */
    return null;
  }

  public static boolean try304(Event http_request, File file){//debug needed
    if(http_request.header_map.containsKey("If-Modified-Since")){//debug needed
      //TODO : https://developer.mozilla.org/ko/docs/Web/HTTP/Headers/If-Modified-Since
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

class FileThread extends Thread{
  Event event;
  EventQueue event_queue;

  public FileThread(Event event, EventQueue event_queue){
    this.event = event;
    this.event_queue = event_queue;
  }


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
        event_queue.push(new Event(client, key, buffer));
      }
    }
    catch(Exception ex){
      //TODO

    }
  }

}