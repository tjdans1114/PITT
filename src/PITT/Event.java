package PITT;

import java.util.*;

public class Event {
  /*
  * Event :
  *
  * 1. HTTP Request : https://developer.mozilla.org/en-US/docs/Web/HTTP for detail
  *
  * 2. IO ?
  *
  */
  static final String crlf = "\r\n";
  /*
    \r = CR (Carriage Return) // Used as a new line character in Mac OS before X
    \n = LF (Line Feed) // Used as a new line character in Unix/Mac OS X
    \r\n = CR + LF // Used as a new line character in Windows
  */

  String method, uri, http_version;//first line
  //String mime; //principal headers
  Map<String,String> header_map;
  StringBuffer body;

  int error_code;

  public Event(String method, String uri, String http_version, Map<String,String> header_map, StringBuffer body, int error_code){
    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.header_map = header_map;
    this.body = body;

    this.error_code = error_code;
  }

  public Event(int error_code){
    this.error_code = error_code;
  }
  //400, 501, ...


}
