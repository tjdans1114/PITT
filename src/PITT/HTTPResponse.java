package PITT;

import java.nio.*;

public class HTTPResponse {
  //TODO : interpret parsed Event
  ByteBuffer[] respond(Event http_request){
    ByteBuffer header_buffer = ByteBuffer.allocate(4096);//capacity
    ByteBuffer body_buffer = ByteBuffer.allocate(4096);//capacity

    /* case : parse error occurred */
    if(http_request.error_code != 200){
      //error handling
      String http_version = "HTTP/1.1";
      int status_code = http_request.error_code;
      String status_str = Global.http_status_map.get(status_code);

      //TODO : study how to use ByteBuffer!!!!!!!
      //first line + header
      header_buffer.put((
              http_version +" "+ status_code + " "  + status_str + Event.crlf //first line
                      + "" // headers
      ).getBytes());//.getBytes(charset)?

      String body =
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
              ;
      body_buffer.put(body.getBytes());

      return new ByteBuffer[]{header_buffer,body_buffer};
    }

    /* parser error code 200 */
    String http_version = "HTTP/1.1";
    int status_code = 404; //TODO

    //TODO : study how to use ByteBuffer!!!!!!!
    header_buffer.put((
            http_version +" "+ status_code + " "  + Global.http_status_map.get("Not Found") + Event.crlf //first line
            + "" // headers
    ).getBytes());//.getBytes(charset)?


    body_buffer.put(("").getBytes());

    return new ByteBuffer[]{header_buffer,body_buffer};
  }
}
