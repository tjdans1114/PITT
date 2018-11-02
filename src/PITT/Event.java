package PITT;

import java.util.*;
import java.nio.channels.*;

public class Event {
  /*
  * Event :
  *
  * 1. NON_IO Event : HTTP Request that does not entail File I/O. should be processed in main thread
  * 2. IO Event : HTTP Request that entails File I/O. should be processed in Thread Pool.
  *
  * Note that each thread(including main) has its own HTTP Interpreter (HTTPInterpreter)
  */

  public SocketChannel client;
  public SelectionKey key;

  public enum Type{
    UNDEFINED, NON_IO, IO, CONTINUATION, FINISHED;
  }
  public Type type;

  public String method, uri, http_version;//first line
  public Map<String,String> header_map;
  public StringBuffer body;

  int error_code;

  Object data; //entailed open bytebuffer : for continuation (partial content)
  int start, end; // position of data

  static final String crlf = "\r\n";
  /*
    \r = CR (Carriage Return) // Used as a new line character in Mac OS before X
    \n = LF (Line Feed) // Used as a new line character in Unix/Mac OS X
    \r\n = CR + LF // Used as a new line character in Windows
  */

  public Event(SocketChannel client, SelectionKey key,
               Type type, String method, String uri, String http_version,
               Map<String,String> header_map, StringBuffer body,
               int error_code){
    this.client = client;
    this.key = key;
    this.type = type;

    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.header_map = header_map;
    this.body = body;

    this.error_code = error_code;
  }

    //for processing error messages
  public Event(SocketChannel client, SelectionKey key,
               int error_code){
    this.client = client;
    this.key = key;
    this.type = Event.Type.NON_IO;

    this.error_code = error_code;
  }
}
