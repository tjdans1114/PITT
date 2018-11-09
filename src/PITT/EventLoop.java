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
  
  public EventLoop(EventQueue event_queue){
    this.event_queue = event_queue;
  }

  //File jobs required
  public void run(){
    system.out.print("EventLoop Thread running \n");
    //TODO : implement Loop architecture
    while(true){
      

    }
  }

  //TODO : future works : Cacheing
}

  public void GoLoop() throws IOException, InterruptedException {
  //System.out.println("Event Dequeueing....");
    Event event = eventQueue.pop();
    if (eent.)

  }