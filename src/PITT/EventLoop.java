package PITT;

public class EventLoop implements Runnable {
  public EventQueue event_queue;
  
  public EventLoop(EventQueue event_queue){
    this.event_queue = event_queue;
  }

  //File jobs required
  public void run(){
    while(true){
      Event event = event_queue.pop();

      //TODO
      Event cont = HTTPInterpreter.respond(event,event_queue);
      if(cont != null){
        event_queue.push(cont);
      }
    }
  }
}