package PITT;

public class EventLoop {
  public EventQueue event_queue;
  public EventQueue io_event_queue; // to select I/O jobs. please only push/pop IOEvent

  public EventLoop(){
    event_queue = new EventQueue();
    io_event_queue = new EventQueue();
  }





  //File jobs required

  public void run(){
    //TODO : implement Loop architecture
  }

  //TODO : future works : Cacheing
}
