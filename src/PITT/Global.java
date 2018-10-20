package PITT;

public class Global {
  public static final int PORT = 1111;

  public static final Map<Integer, String> http_status_map  = new TreeMap<Integer,String>(){
    {
      //TODO
      put(200,"OK");
      put(400,"Bad Request");
      put(404,"Not Found");
      put(501,"Not Implemented");
    }
  };
}
