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
  * Note that each thread(including main) has its own HTTP Interpreter (HTTPResponse)
  */

  public SocketChannel client;

  public enum Type{
    UNDEFINED, NON_IO, IO;
  }
  public Type type;

  public String method, uri, http_version;//first line
  public Map<String,String> header_map;
  public StringBuffer body;

  int error_code;

  static final String crlf = "\r\n";
  /*
    \r = CR (Carriage Return) // Used as a new line character in Mac OS before X
    \n = LF (Line Feed) // Used as a new line character in Unix/Mac OS X
    \r\n = CR + LF // Used as a new line character in Windows
  */

  public Event(SocketChannel client, Type type, String method, String uri, String http_version, Map<String,String> header_map, StringBuffer body, int error_code){
    this.client = client;
    this.type = type;

    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.header_map = header_map;
    this.body = body;

    this.error_code = error_code;
  }

  public Event(int error_code){
    this.type = Event.Type.NON_IO;

    this.error_code = error_code;
  }
  public Event(){
    this(400);
  } // unnecessary?
}
