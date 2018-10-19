package PITT;

public class HTTPParser {
  /*
  * https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages for detail
  * also refer to https://www.tutorialspoint.com/http/http_requests.htm
  * HTTP 1.1 maybe?
  */

  /*
  * [Start Line] : METHOD + request target(URL) + HTTP Version
  *   definition of 'supported methods' are required
  * [Headers] (General Headers, Request Headers, Entity Headers) : semi-colon is a delimiter
  *   for thorough headers, refer to https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
  *   for better guide, refer to https://developer.mozilla.org/ko/docs/Web/HTTP/Headers
  * [Body] : not for GET, HEAD, DELETE, or OPTIONS
  */
  // HOW to handle body?????????

  /* example
  * POST /background.png HTTP/1.0   [Start Line]
  * Host: ...                 [Headers]
  * User-Agent: ...
  *       (blank line : delimiter between Headers / Body]
  * Data...? [Body]
  */
  String[] supported_methods = {"GET","POST"};
  String[] supported_headers = {
          "Connection",
          "Range", //for streaming?
          "If-Modified-Since", //for cacheing?
          //TODO : determine which headers are to be implemented
  };

  private boolean is_supported_method(String m){
    for(String s : supported_methods){
      if(m.equals(s)){
        return true;
      }
    }
    return false;
  }


  public Event parse(String request){
    final String space = " ";

    String method, uri, http_version;
    try{
      String[] tokens = request.split(Event.crlf);

      /* 1. parse first line : method ,uri, http_version */
      String first_line = tokens[0];
      String[] parsed_first_line = first_line.split(space);
      if(parsed_first_line.length != 3){
        throw new Exception("HTTP request parse failed : first line segment not size 3");
      }
      method = parsed_first_line[0];
      uri = parsed_first_line[1];
      http_version = parsed_first_line[2];

      if(!is_supported_method(method)){//unsupported method : 501
        return new Event(501);
      }

      /* 2. headers : general, request, entity */
      //???
      for(int i=1;i<tokens.length;i++){
        String header = tokens[i];

        //TODO
        if(header.startsWith("Connection")){
          //TODO
          continue;
        }
        if(true){
          continue;
        }
      }

      /* 3. body ?  */
      //???




      return new Event(method,uri,http_version,new String[1],new String[1], 200);
    }
    catch(Exception ex){
      ex.printStackTrace();

      return new Event(400);//bad request
    }
  }
}
