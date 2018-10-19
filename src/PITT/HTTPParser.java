package PITT;

public class HTTPParser {
  /*
  * https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages for detail
  * also refer to https://www.tutorialspoint.com/http/http_requests.htm
  * HTTP 1.1 maybe?
  */

  /*
  * [Start Line] : METHOD + request target(URL) + HTTP Version
  * [Headers] (General Headers, Request Headers, Entity Headers) : semi-colon is a delimiter
  * [Body] : not for GET, HEAD, DELETE, or OPTIONS
  */

  /* example
  * POST /background.png HTTP/1.0   [Start Line]
  * Host: ...                 [Headers]
  * User-Agent: ...
  *       (blank line : delimiter between Headers / Body]
  * Data...? [Body]
  */

  public Event parse(String request){
    final String space = " ";
    try{
      String[] tokens = request.split(Event.crlf);

      /* 1. parse first line : method ,uri, http_version */
      String first_line = tokens[0];
      String[] parsed_first_line = first_line.split(space);
      if(parsed_first_line.length != 3){
        throw new Exception("HTTP request parse failed : first line segment not size 3")
      }








      return new Event("method","uri","http_version",new String[1],new String[1]);
    }
    catch(Exception ex){
      ex.printStackTrace();

      return new Event();
    }
  }
}
