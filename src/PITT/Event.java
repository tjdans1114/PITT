package PITT;

import java.util.*;
import java.nio.*;
import java.nio.channels.*;

public class Event {
  /*
  * Event : 
  *
  * Unprocessed : 
  *  1. NON_IO
  *  2. IO 
  * Processing
  *  3. CONTINUATION
  * Finished
  *  4. FINISHED
  * 
  * Note that each thread(including main) has its own HTTP Interpreter (HTTPInterpreter)
  * Read contructor code to understand the data members(fields) that each type exploits.
  * Specification may be updated, due to the use of Buffer & File
  */

  public enum Type{
    UNDEFINED, 
    NON_IO, // unprocessed event that does not need file IO
    IO, // unprocessed event that does need file IO
    CONTINUATION, // event that is being processed. case where request 'body' is too long
    FINISHED; //event that finished processing
  }

  public SocketChannel client;
  public SelectionKey key;

  public Type type;

  public String method, uri, http_version;//first line
  public Map<String,String> header_map;
  public StringBuffer req_body;

  int error_code;


  ByteBuffer resp_body; //entailed open bytebuffer : for continuation
  //for 206
  int start, end; // position of data

  static final String crlf = "\r\n";
  static final int BODY_LENGTH = 2 * 1024 * 1024;//2MB
  /*
    \r = CR (Carriage Return) // Used as a new line character in Mac OS before X
    \n = LF (Line Feed) // Used as a new line character in Unix/Mac OS X
    \r\n = CR + LF // Used as a new line character in Windows
  */

  /** Constructors ************************************/
  // 1. constructor for NON_IO : i.e. errors
  public Event(SocketChannel client, SelectionKey key,
               int error_code){
    this.client = client;
    this.key = key;
    this.type = Event.Type.NON_IO;

    this.error_code = error_code;
  }

  // 2. constructor for IO
  public Event(SocketChannel client, SelectionKey key,
               Type type, String method, String uri, String http_version,
               Map<String,String> header_map, StringBuffer req_body,
               int error_code){
    this.client = client;
    this.key = key;
    this.type = type;

    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.header_map = header_map;
    this.req_body = req_body;

    this.error_code = error_code;
  }

  // 3. continuation
  public Event(SocketChannel client, SelectionKey key,
               ByteBuffer continuation){
    this.client = client;
    this.key = key;
    this.type = Type.CONTINUATION;

    this.resp_body = continuation;//remaining data to write
  }

  // 4. finished
  public Event(SocketChannel client, SelectionKey key){
    this.client = client;
    this.key = key;
    this.type = Type.FINISHED;
  }
}
