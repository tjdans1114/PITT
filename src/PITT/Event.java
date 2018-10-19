package PITT;

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

  String method, uri, http_version;//first line
  //String mime; //principal headers
  String[] headers;
  String[] body;

  boolean parse_error;//error code?

  public Event(String method, String uri, String http_version, String[] headers, String[] body){
    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.headers = headers;
    this.body = body;

    parse_error = false;
  }

  public Event(){
    parse_error = true;
  }


}
