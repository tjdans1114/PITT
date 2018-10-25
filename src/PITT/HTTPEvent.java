package PITT;

import java.util.Map;

public class HTTPEvent extends Event {
  public HTTPEvent(){
    this.type = Event.Type.HTTP;
  }

  public HTTPEvent(String method, String uri, String http_version, Map<String,String> header_map, StringBuffer body, int error_code){
    this.type = Event.Type.HTTP;

    this.method = method;
    this.uri = uri;
    this.http_version = http_version;
    this.header_map = header_map;
    this.body = body;

    this.error_code = error_code;
  }

  public HTTPEvent(int error_code){
    this.type = Event.Type.HTTP;

    this.error_code = error_code;
  }
}
