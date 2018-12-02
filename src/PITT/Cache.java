package PITT;

import java.nio.*;
import java.util.*;

public class Cache {
  /** uri -> {last-modified, bytebuffer} */

  private static final int capacity = Global.BUFFER_SIZE; // max byte size of cell
  private static final int cache_size = 10; // total # of cells in cache
  private static final float load_factor = 0.75f;

  private static LinkedHashMap<String, CachePair> cache =
          new LinkedHashMap<String, CachePair>(cache_size, load_factor, true);

  private static int current_size = 0;

  /**
   * Main Functions
   */
  //date means last-modified date of the client
  public static boolean has(String uri, Date date) {
    return cache.containsKey(uri) &&
            cache.get(uri).date.equals(date);
  }

  public static ByteBuffer get(String uri,Date date) {
    //TODO
    if (!has(uri,date)) {
      return null;
    }
    return cache.get(uri).data.duplicate();
  }

  /**
   * Auxiliary functions
   */
  public synchronized void clear() {
    this.cache.clear();
  }

  public synchronized int used_size() {
    return this.cache.size();
  }
//
//  public synchronized Collection<Map.Entry<String, ByteBuffer>> getAll() {
//    return new ArrayList<Map.Entry<String, ByteBuffer>>(cache.entrySet());
//
//  }

  public static void set(String uri, ByteBuffer data, Date date) {
    if(data == null) {//1. file is removed in server!
      cache.remove(uri);
      return;
    }

    //2. check if the bytebuffer size is small enough
    if(data.capacity() > capacity){
      return;
    }

    if(!cache.containsKey(uri)){//cache has no file matching uri
      if(current_size < cache_size){
        cache.put(uri,new CachePair(date,data.duplicate()));
        current_size++;
      }
      else{
        removefirst();//evict
        cache.put(uri,new CachePair(date,data.duplicate()));
      }
    }
    else {
      Date cache_date = cache.get(uri).date;
      if(!cache_date.equals(date)){//different date
        //System.out.println("diff date...");
        //replace
        cache.remove(uri);
        cache.put(uri,new CachePair(date,data.duplicate()));
      }
    }
  }

  public static void removefirst() {
    //TODO : improve performance?
    //Key at the first location
    String first_key = (String)cache.keySet().toArray()[cache.size()-1]; // O(N)

//    ByteBuffer first_value = Cache.get(first_key);
    cache.remove(first_key);
  }
//
//  private void removeEldestEntry() {
//    boolean isRemove = size() > maxsize;
//    if (isRemove) {
//      Object obj = this.get(eldest.)
//    }
//  }
}

class CachePair{
  public Date date;
  public ByteBuffer data;

  public CachePair(Date date, ByteBuffer data){
    this.date = date;
    this.data = data;
  }
}
