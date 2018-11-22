package PITT;

public class EventLoop extends Thread {
  public EventQueue event_queue;
  
  public EventLoop(EventQueue event_queue){
    this.event_queue = event_queue;
  }

  //File jobs required
  public void run(){
    System.out.println("Event Loop running...");


    while(true){
      //System.out.println("Event Loop inner running...");

//      if(event_queue.empty()){
//        continue;
//      }
      Event event = event_queue.pop();

      //TODO
      System.out.println("responding...");
      Event cont = HTTPInterpreter.respond(event,event_queue);
      System.out.println("respond complete");
      //System.out.println("run");

      if(cont != null){
        //System.out.println("run");

        event_queue.push(cont);
        //System.out.println("run");

      }
    }
  }
}