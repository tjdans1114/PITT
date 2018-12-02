package PITT;

import java.util.*;
import java.io.*;
import java.nio.channels.*;

public class HTTPParser {
  private static String[] supported_methods = {"GET"}; //currently, only support GET

//  String[] supported_headers = {
//          "Connection",
//          "Range", //for streaming? TODO
//          "If-Modified-Since", //for cacheing?
//  };

  private static boolean is_supported_method(String m){
    for(String s : supported_methods){
      if(m.equals(s)){
        return true;
      }
    }
    return false;
  }

  //this is just a parser...
  public static Event parse(SocketChannel client, SelectionKey key, String request){
    final String space = " ";

    String method, uri, http_version;
    TreeMap<String,String> header_map = new TreeMap<String,String>();
    StringBuffer body= new StringBuffer();

    try{
      BufferedReader reader = new BufferedReader(new StringReader(request));

      /** 1. parse first line : method ,uri, http_version */
      String first_line = reader.readLine();
//      System.out.println("first line is : "+ first_line);

      String[] parsed_first_line = first_line.split(space);
      if(parsed_first_line.length != 3){
        return new Event(client, key, 400);
      }
      method = parsed_first_line[0];
      uri = parsed_first_line[1];
      http_version = parsed_first_line[2];

      if(!is_supported_method(method)){//unsupported method : 501
        return new Event(client, key, 501);
      }
      if(!http_version.equals("HTTP/1.1")){
        return new Event(client, key, 505);
      }

      /** 2. headers : general, request, entity */
      int header_length = 0;
      while(true){
        String header_line = reader.readLine();
        if(header_line == null || header_line.length() == 0){
          break;
        }

        header_length += header_line.length() + 1;//1 for '\n'
        if(header_length > Global.HEADER_SIZE){
          return new Event(client, key, 431);
        }

        //header line is parsed by colon
        int colon_index = header_line.indexOf(':');
        if(colon_index == -1 || colon_index + 1 == header_line.length()){
          return new Event(client, key, 400);
        }

        String header_name = header_line.substring(0,colon_index).toLowerCase();
        String header_content = header_line.substring(colon_index+2);
        // System.out.println(header_name);
        // System.out.println(header_content);

        //TODO : duplicate header? e.g. Range...
        header_map.put(header_name,header_content);
      }
      //deal with unsupported headers...?

      /** 3. body : now, the rest part is all body  */
      while(true){
        String body_line = reader.readLine();
        if(body_line == null){//??
          break;//end of request
        }

        body.append(body_line).append(Event.crlf);
      }

      /** 4. further preprocessing */
      String connection = header_map.get("connection");

      return new Event(
              client, key,
              method,uri,http_version,
              header_map,body, 200,
              connection);
    }
    catch(Exception ex){
      ex.printStackTrace();

      return new Event(client, key, 500);// internal server error
    }
  }
}
