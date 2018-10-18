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
    //TODO
    return null;
  }

}
