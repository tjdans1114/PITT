package PITT;

import java.util.*;

public abstract class Event {
  /*
  * Event :
  *
  * 1. HTTPEvent : HTTP Request event : https://developer.mozilla.org/en-US/docs/Web/HTTP for detail
  * 2. IOEvent : Continuation of I/O
  */

  public enum Type{
    UNDEFINED, HTTP, IO;
  }
  protected Type type;

  protected String method, uri, http_version;//first line
  protected Map<String,String> header_map;
  protected StringBuffer body;

  int error_code;

  static final String crlf = "\r\n";
  /*
    \r = CR (Carriage Return) // Used as a new line character in Mac OS before X
    \n = LF (Line Feed) // Used as a new line character in Unix/Mac OS X
    \r\n = CR + LF // Used as a new line character in Windows
  */
  public Event(){
    this.type = Type.UNDEFINED;
  }
}
