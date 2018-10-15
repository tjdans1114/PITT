package PITT;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventQueue {
  private BlockingQueue<Event> Q;
  // BlockingQueue : A Queue that additionally supports operations
  // that wait for the queue to become non-empty when retrieving an element,
  // and wait for space to become available in the queue when storing an element.
  // put, take are the blocking operations

  public EventQueue(){
    Q = new LinkedBlockingQueue<Event>();
  }

  //method name follows c++ STL style
  //push, pop, top

  //read https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/LinkedBlockingQueue.html thoroughly
  //then do the implementation
  // try-catch vs throw exception
  void push(Event e){
    try{
      Q.put(e);
    }
    catch(InterruptedException ex){
      //TODO
      ex.printStackTrace();
    }
  }

  //
  Event pop(){
    try {
      return Q.take();
    }
    catch(InterruptedException ex){
      //TODO
      ex.printStackTrace();
      return null;
    }
  }

  //DEPRECATED
  Event top(){
    return Q.peek();
  }

}
