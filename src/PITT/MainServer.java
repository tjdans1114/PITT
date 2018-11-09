package PITT;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MainServer {
  public static EventQueue EVENT_QUEUE = new EventQueue();
  public static EventLoop event_loop = new EventLoop(EVENT_QUEUE);//share EVENT_QUEUE. between MainSerer & EventLoop


  public static void main(String[] args) throws IOException{
    /* Basic Server Configuration */

    Selector selector = Selector.open();  //selector : selects SelectableChannel Objects
    ServerSocketChannel socket = ServerSocketChannel.open();  //socket : selectable channel for stream-oriented listening sockets
    InetSocketAddress address = new InetSocketAddress(Global.IP,Global.PORT);//localhost
    socket.bind(address); // Binds the channel's socket to a local address and configures the socket to listen for connections
    socket.configureBlocking(false);//configure : Non-Blocking

      //ops : operation set
    int ops = socket.validOps(); // ops == SelectionKey.OP_ACCEPT
    SelectionKey selection_key = socket.register(selector,ops,null);

    /* Running MainServer */ //Infinite loop : MainServer keeps running
    System.out.println("server running ... ");
    int count = 0; //# of clients
    event_loop.run();//run event loop TODO

    while(true) {//TODO : try-catch
      selector.select();  // Selects a set of keys whose corresponding channels are ready for I/O operations
      Set<SelectionKey> keys = selector.selectedKeys(); // token representing the registration of a SelectableChannel with a Selector
      Iterator<SelectionKey> key_iterator = keys.iterator();
      while (key_iterator.hasNext()) {//don't use range-for
        SelectionKey key = key_iterator.next();
        //System.out.println(key);

        if (key.isAcceptable()) {//key can accept client
          SocketChannel client = socket.accept(); // get client socket
          if(client == null){
            continue;
          }
          client.configureBlocking(false);//non-blocking
          client.register(selector, SelectionKey.OP_READ); //convert it to readable state

          /* do something... */
          count++;
          System.out.println("Connection Accepted : " + client.getLocalAddress());
        }
        else if (key.isReadable()) {//key is ready for reading
          SocketChannel client = (SocketChannel) key.channel();
          //TODO : read request
          String req_str = read(client);
          Event ev = HTTPParser.parse(client,key,req_str);

          EVENT_QUEUE.push(ev);
        }
        else if(key.isWritable()){
          //TODO : NOTHING!!!
        }

        key_iterator.remove();//remove current key
      }
    }
  }

  static String read(SocketChannel client){
    try{
      ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
      //timeout?

      while(true){
        int bytes_read = client.read(buffer);

        if(bytes_read == -1){
          break;
        }
      }

      return buffer.toString();
    }
    catch(Exception ex){
      return null;
    }
  }
}