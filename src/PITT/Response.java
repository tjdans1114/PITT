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

  TreeMap<String,String> header_map = new TreeMap<String,String>();
  StringBuffer body= new StringBuffer();

  //constructor
  public Response(SocketChannel client, SelectionKey key,
                  String http_version, int status_code,
                  TreeMap<String,String> header_map, StringBuffer body){
    this.client = client;
    this.key = key;

    this.http_version = http_version;
    this.status_code = status_code;
    this.status_message = Global.http_status_map.get(status_code);

    this.header_map = header_map;
    this.body = body;
  }
}
