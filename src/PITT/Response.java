package PITT;

import java.nio.*;
import java.nio.channels.*;
import java.util.TreeMap;

public class Response {
  SocketChannel client;
  SelectionKey key;

  String http_version;
  int status_code;
  String status_message;

  TreeMap<String,String> header_map;
  ByteBuffer body;

  Event continuation;

  //constructor
  public Response(SocketChannel client, SelectionKey key,
                  String http_version, int status_code,
                  TreeMap<String,String> header_map, ByteBuffer body){
    this.client = client;
    this.key = key;

    this.http_version = http_version;
    this.status_code = status_code;
    this.status_message = Global.http_status_map.get(status_code);

    this.header_map = header_map;
    this.body = body;
  }

  //constructor for without-body
  public Response(SocketChannel client, SelectionKey key,
                  String http_version, int status_code,
                  TreeMap<String,String> header_map){
    this.client = client;
    this.key = key;

    this.http_version = http_version;
    this.status_code = status_code;
    this.status_message = Global.http_status_map.get(status_code);

    this.header_map = header_map;
  }


  ByteBuffer get_message(){
    ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);

    buffer.put(http_version.getBytes());
    buffer.put((byte) status_code);
    buffer.put(status_message.getBytes());

    for(String s : header_map.keySet()){
      buffer.put((s+": "+header_map.get(s)).getBytes());
    }

    if(body == null){//status code not 200, 206, 304?
      buffer.put(
              Global.ERROR_HTML_MAP.get(status_code).getBytes()
      );
    }
    else{
      buffer.put(body);
    }

    return buffer;
  }
}
