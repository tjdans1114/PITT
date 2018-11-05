package PITT;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MainServer {
  public static EventQueue event_queue = new EventQueue();
  public static EventLoop event_loop = new EventLoop();


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
          //run(); // read input
//          ByteBuffer buffer = ByteBuffer.allocate(256);
//          client.read(buffer); //read message from client
//
//          String result = new String(buffer.array()).trim();//trim : removes whitespace
//          if(!result.equals("")){
//            System.out.println("message received : " + result);
//          }
//
//          if (result.equals("FINISH")) {//exit code
//            client.close();
//            System.out.println("Closing this client... but server keeps running! Try running client again to establish new connection");
//          }
        }
        else if(key.isWritable()){
          //retrieve response from the key
          Response response = (Response) key.attachment();
          //SocketChannel client = (SocketChannel) key.channel();//???

          //write response
          SocketChannel client = response.client;
          ByteBuffer response_data = response.get_message();
          int data_size = client.write(response_data); // debugging required
            // retrieve response


        }
        key_iterator.remove();//remove current key
      }
    }
  }
}
