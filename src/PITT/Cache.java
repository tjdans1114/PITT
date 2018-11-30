package PITT;

import java.nio.*;
import java.util.*;

public class Cache<K,V> {
  //TODO
  //define fields. e.g. Map<String, ByteBuffer> ? priorityqueue? idon'know
  private int capacity;
  private static final float load_factor = 0.75f;
	private static LinkedHashMap<String,ByteBuffer> cache;
	private int cache_size;
  int current;
  int limit;

  public Cache(){
    //default size
    this.capacity = 100;
    this.cache_size = 100*1024*1024;
    this.current = 0;
//    private int lru_size;
  }

  public Cache(int size) {
    this.cache_size = size;
    this.capacity = (int)Math.ceil(this.cache_size/this.load_factor)+1;
    this.cache = new LinkedHashMap<String,ByteBuffer>(this.capacity, this.load_factor, true){
      private static final long serialVersionUID =1;
//      @Override protected boolean remove_eldest (Map.Entry<K,V) eldest){
//        return size() > Cache.this.cache_size;
//      }
    };
  }
  public static boolean has(String uri){
    //TODO
//    ByteBuffer value = Cache.get(uri);
//
//    if (value ==null){
//      System.out.println("Cache Miss");
//      return false;
//    } else {
//      System.out.println("Cache Hit");
//      return true;
//    }
    return false;
  }

  public static ByteBuffer get(String uri){
    //TODO
    boolean check = has(uri);
    ByteBuffer value = null;
    if (check){
      value = Cache.get(uri);
    }
    return value;
  }
  public synchronized void clear(){
    this.cache.clear();
  }
  public synchronized int used_size(){
    return this.cache.size();
  }
  public synchronized Collection<Map.Entry<K,V>> getAll(){
    //return new ArrayList<Map.Entry<K,V>>(map.entrySet());
    return null;
  }

  public static void set(String str, ByteBuffer new_input){
    //TODO
//    V value = cache.get(str);
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
//  }
//  /*
//  private void removeEldestEntry(){
//    boolean isRemove = size() > maxsize;
//    if(isRemove) {
//      Object obj = this.get(eldest.)
//    }
//  }
//  */
//
  }
}
