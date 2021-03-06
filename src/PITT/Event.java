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


  /** Processed Headers */
  String connection;//TODO

  FileChannel file_channel; //entailed open file channel : for continuation
  int start; //file channel start position

  /**************************************************************************************/
  static final String crlf = "\r\n";

  /** Constructors ************************************/
  // 1. constructor for NON_IO : i.e. errors
  public Event(SocketChannel client, SelectionKey key,
               int error_code){
    this.client = client;
    this.key = key;
    this.type = Event.Type.NON_IO;
    
    this.error_code = error_code;

    this.connection = null;
  }

  // 2. constructor for IO
  public Event(SocketChannel client, SelectionKey key,
               String method, String uri, String http_version,
               Map<String,String> header_map, StringBuffer req_body,
               int error_code,
               String connection){
    this.client = client;
    this.key = key;
    this.type = Event.Type.IO;

    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.header_map = header_map;
    this.req_body = req_body;

    this.error_code = error_code;

    this.connection = connection;
  }

  // 3. continuation
  public Event(SocketChannel client, SelectionKey key,
               FileChannel file_channel, int start,
               String connection){
    this.client = client;
    this.key = key;
    this.type = Type.CONTINUATION;

    this.file_channel = file_channel;//remaining data to write

    this.connection = connection;

    this.start = start;
  }

  // 4. finished
  public Event(SocketChannel client, SelectionKey key){
    this.client = client;
    this.key = key;
    this.type = Type.FINISHED;
  }
}
