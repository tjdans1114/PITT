package PITT;

import java.nio.*;
import java.nio.channels.*;
import java.util.TreeMap;

public class HTTPInterpreter {
  //TODO : interpret parsed Event
  public Response create_response(Event http_request){
    SocketChannel client = http_request.client;
    SelectionKey key = http_request.key;

    String http_version = "HTTP/1.1";
    int status_code;
    TreeMap<String,String> header_map = new TreeMap<String,String>();
    ByteBuffer body = ByteBuffer.allocate(0);

    /* case : parse error occurred */
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

    /* parser error code 200 */
    status_code = 404; //TODO

    //append headers
    header_map.put("Connection","keep-alive");

    //process body
    if(Cache.has(http_request.uri)){
      status_code = 206;
      body.put(
              Cache.get(http_request.uri)
      );
    }
    else{
      body.put(
              ("Not Implemented yet sorry...").getBytes()
      );
    }

    return new Response(client,key,http_version,status_code,header_map,body);
  }

  public Object respond(Response resp){
    return null;
  }
  //SocketChannel.write shoudld happen here!!!!


}
