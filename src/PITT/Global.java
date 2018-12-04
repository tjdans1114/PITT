package PITT;

import java.nio.channels.FileChannel;
import java.util.*;
import java.nio.file.*;

public class Global {
  public static final String IP = "127.0.0.1"; //localhost
  public static final int PORT = 1111;

  public static final String INDEX = "index.html";

  public static final int BUFFER_SIZE = 2*1024*1024;//2MB. temporarily 500kb
  public static final int LARGE_BUFFER_SIZE = 20*1024*1024; //20 MB


  public static final int HEADER_SIZE = 1024; // 1024 characters

  public static final int TIMEOUT = 1 * 1000; //miliseconds

  public static final Map<Integer, String> http_status_map  = new TreeMap<Integer,String>(){
    {
      /** Complete codes */
      put(200,"OK");
      put(304,"Not Modified");
      put(400,"Bad Request");
      put(404,"Not Found");
      put(408,"Request Timeout");
      put(413,"Payload Too Large");
      put(431,"Request Header Fields Too Large");
      put(500,"Internal Server Error");
      put(501,"Not Implemented");
      put(505,"HTTP Version Not Supported");

      //TODO
      put(403,"Forbidden");

      /** Other codes*/
      //1xx Informational response
      put(100,"Continue");
      put(101,"Switching Protocols");
      put(102,"Processing");
      put(103,"Early Hints");

      //2xx Success
      put(201,"Created");
      put(202,"Accepted");
      put(203,"Non-Authoritative Information");
      put(204,"No Content");
      put(205,"Reset Content");
      put(206,"Partial Content"); //TODO : streaming?
      put(207,"Multi-Status");
      put(208,"Already Reported");
      put(226,"IM Used");

      //3xx Redirection
      put(300,"Multiple Choices");
      put(301,"Moved Permanently");
      put(302,"Found");
      put(303,"See Other");
      put(305,"Use Proxy");
      put(306,"Switch Proxy");
      put(307,"Temporary Redirect");
      put(308,"Permanent Redirect");

      //4xx Client errors
      put(401,"Unauthorized");
      put(402,"Payment Required");
      put(405,"Method Not Allowed");
      put(406,"Not Acceptable");
      put(407,"Proxy Authentication Required");
      put(409,"Conflict");
      put(410,"Gone");
      put(411,"Length Required");
      put(412,"Precondition Failed");
      put(414,"URI Too Long");
      put(415,"Unsupported Media Type");
      put(416,"Range Not Satisfiable");
      put(417,"Expectation Failed");
      put(418,"I'm a teapot");
      put(421,"Misdirected Request");
      put(422,"Unprocessable Entity");
      put(423,"Locked");
      put(424,"Failed Dependency");
      put(426,"Upgrade Required");
      put(428,"Precondition Required");
      put(429,"Too Many Requests");
      put(451,"Unavailable For Legal Reasons");

      //5xx Server errors
      put(502,"Bad Gateway");
      put(503,"Service Unavailable");
      put(504,"Gateway Timeout");
      put(506,"Variant Also Negotiates");
      put(507,"Insufficient Storage");
      put(508,"Loop Detected");
      put(510,"Not Extended");
      put(511,"Network Authentication Required");
    }
  };

  public static final Map<Integer,String> ERROR_HTML_MAP = new TreeMap<Integer,String>(){
    {
      try{
        put(400, new String(Files.readAllBytes(Paths.get("html/400.html"))));
        put(404, new String(Files.readAllBytes(Paths.get("html/404.html"))));
        put(408, new String(Files.readAllBytes(Paths.get("html/408.html"))));

        put(500, new String(Files.readAllBytes(Paths.get("html/500.html"))));
        put(501, new String(Files.readAllBytes(Paths.get("html/501.html"))));
        put(505, new String(Files.readAllBytes(Paths.get("html/505.html"))));

      }
      catch(Exception ex){
        ex.printStackTrace();
      }

    }
  };
}
