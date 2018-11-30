package PITT;

import java.nio.*;
import java.util.*;

public class Cache{
  //TODO : uri -> {last-modified, bytebuffer}


  //TODO : Don't reference. you should copy it
  //define fields. e.g. Map<String, ByteBuffer> ? priorityqueue? idon'know
  private static final int capacity = 1024*1024*1; // max byte size of cell . currently 1MB
  private static final int cache_size = 10; // total # of cells in cache

  private static final float load_factor = 0.75f;
	private static LinkedHashMap<String,ByteBuffer> cache = new LinkedHashMap<String,ByteBuffer>();
  int current;
  int limit;

  //with static, we don't need constructors

//  public Cache(){
//    //default size
//    this.capacity = 100;
//    this.cache_size = 100*1024*1024;
//    this.current = 0;
////    private int lru_size;
//  }

//  public Cache(int size) {
//    this.cache_size = size;
//    this.capacity = (int)Math.ceil(this.cache_size/this.load_factor)+1;
//    this.cache = new LinkedHashMap<String,ByteBuffer>(this.capacity, this.load_factor, true){
//      private static final long serialVersionUID =1;
////      @Override protected boolean remove_eldest (Map.Entry<K,V) eldest){
////        return size() > Cache.this.cache_size;
////      }
//    };

  /** Main Functions */
  public static boolean has(String uri){
    return cache.keySet().contains(uri);
  }

  public static ByteBuffer get(String uri){
    //TODO
    if(has(uri)){
      return null;
    }
    return cache.get(uri);
  }

  /** Auxiliary functions */
  public synchronized void clear(){
    this.cache.clear();
  }
  public synchronized int used_size(){
    return this.cache.size();
  }

  public synchronized Collection<Map.Entry<String,ByteBuffer>> getAll(){
    return new ArrayList<Map.Entry<String,ByteBuffer>>(cache.entrySet());

  }

  public static void set(String str, ByteBuffer new_input){
//    //TODO
//    ByteBuffer value = cache.get(str);
//
//    //if
//    if (cache.get(str) == null){
//      System.out.println("new data : adding to cache");
//      if (value.capacity() > limit){
//        System.out.print("Buffer Too Big");
//        return;
//      //when cache is already full, delete the element
//      if (Cache.size()>= capacity){
//        removefirst();
//      }
//      //when cache is already full with data, remove element
//      while (current_size + value.capacity() > size) {
//				removefirst();
//			}
//      Cache.put(str, new_input);
//      current_size += value.capacity();
//      System.out.println("Adjustment complete");
//    }
//    else{
//      System.out.println("Already holds data");
//    }
  }
  public void removefirst() {
    //Key at the first location
//    String first_key = (String)Cache.keySet().toArrary()[Cache.size()-1];
//    ByteBuffer first_value = Cache.get(first_key);
//    Cache.remove(first_key);
//    current_size -= first_value.capacity();
  }
  /*
  private void removeEldestEntry(){
    boolean isRemove = size() > maxsize;
    if(isRemove) {
      Object obj = this.get(eldest.)
    }
  }
  */

  }
}
