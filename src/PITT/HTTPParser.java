package PITT;

import java.util.*;
import java.io.*;

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

//  String[] supported_headers = {
//          "Connection",
//          "Range", //for streaming?
//          "If-Modified-Since", //for cacheing?
//          //TODO : determine which headers are to be implemented
//  };

  private boolean is_supported_method(String m){
    for(String s : supported_methods){
      if(m.equals(s)){
        return true;
      }
    }
    return false;
  }

  //this is just a parser...
  public HTTPEvent parse(String request){
    final String space = " ";

    String method, uri, http_version;
    TreeMap<String,String> header_map = new TreeMap<String,String>();
    StringBuffer body= new StringBuffer();

    try{
      BufferedReader reader = new BufferedReader(new StringReader(request));

      /** 1. parse first line : method ,uri, http_version */
      String first_line = reader.readLine();
      String[] parsed_first_line = first_line.split(space);
      if(parsed_first_line.length != 3){
        throw new Exception("HTTP request parse failed : first line segment not size 3");
      }
      method = parsed_first_line[0];
      uri = parsed_first_line[1];
      http_version = parsed_first_line[2];

      if(!is_supported_method(method)){//unsupported method : 501
        return new HTTPEvent(501);
      }
      if(http_version != "HTTP/1.1"){
        return new HTTPEvent(505);
      }

      /** 2. headers : general, request, entity */
      while(true){
        String header_line = reader.readLine();
        if(header_line.length() == 0){
          break;
        }

        //header line is parsed by colon
        int colon_index = header_line.indexOf(':');
        if(colon_index == -1){
          throw new Exception("HTTP request parse failed : header line doesn't contain colon");
        }

        String header_name = header_line.substring(0,colon_index);
        String header_content = header_line.substring(colon_index+1,header_line.length());
        header_map.put(header_name,header_content);
      }
      //deal with unsupported headers...?


      /** 3. body : now, the rest part is all body  */
      while(true){
        String body_line = reader.readLine();
        if(body_line == null){
          break;//end of request
        }

        body.append(body_line).append(Event.crlf);
      }

      return new HTTPEvent(method,uri,http_version,header_map,body, 200);
    }
    catch(Exception ex){
      ex.printStackTrace();

      return new HTTPEvent(400);//bad request
    }
  }
}
