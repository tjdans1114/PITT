package PITT;

import java.nio.*;

public class HTTPResponse {
  //TODO : interpret parsed Event
  ByteBuffer respond(Event http_request){
    String http_version = "HTTP/1.1";
    int status_code = 404;

    //TODO : study how to use ByteBuffer!!!!!!!
    ByteBuffer response = ByteBuffer.allocate(987654321);//capacity

    response.put((
            http_version +" "+ status_code + " "  + Global.http_status_map.get("Not Found")
    ).getBytes());//.getBytes(charset)?
    //add headers & body

    return response;//ByteBuffer[]?
  }
}
