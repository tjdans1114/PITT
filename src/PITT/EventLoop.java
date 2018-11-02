package PITT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import java.util.ArrayList;

public class EventLoop implements Runnable {
  public EventQueue event_queue;
  public EventQueue io_event_queue; // to select I/O jobs. please only push/pop IOEvent
  //BlockingQueue<Event> IOQueue = new LinkedBlockingqueue<Event>();
  //LRUCache Cache = new LRUCache();
	//FileChannel errorChannel;
	//MappedByteBuffer buffer400, buffer404, buffer405;
  
  
  public EventLoop(){
    event_queue = new EventQueue();
    io_event_queue = new EventQueue();
  }







  //File jobs required

  public void run(){
    //TODO : implement Loop architecture

    //if io, look for the cache then do the io-job
  }

  //TODO : future works : Cacheing
}
