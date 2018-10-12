package PITT;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventQueue {
  private BlockingQueue<Event> Q;
  // BlockingQueue : A Queue that additionally supports operations
  // that wait for the queue to become non-empty when retrieving an element,
  // and wait for space to become available in the queue when storing an element.

  public EventQueue(){
    Q = new LinkedBlockingQueue<Event>();
  }

  //method name follows c++ STL style
  //push, pop, top

  //read https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/LinkedBlockingQueue.html thoroughly
  //then do the implementation
  void push(Event e){
    //TODO
  }
  void pop(){
    //TODO
  }
  Event top(){
    //TODO
    return null;
  }

}
