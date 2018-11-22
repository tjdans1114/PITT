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

    event_loop.start();//run event loop TODO : not working well

    while(true) {//TODO : try-catch for buffer overflow exceptions...
      //System.out.println("Main inner loop running");

      selector.select();
      Set<SelectionKey> keys = selector.selectedKeys();
      Iterator<SelectionKey> key_iterator = keys.iterator();
      while (key_iterator.hasNext()) {
        SelectionKey key = key_iterator.next();
        //System.out.println(key);

        if (key.isAcceptable()) {//key can accept client
          SocketChannel client = socket.accept(); // get client socket
          //System.out.println(client);
          if(client == null){
            continue;
          }
          client.configureBlocking(false);//non-blocking
          client.register(selector, SelectionKey.OP_READ); //convert it to readable state

          /* do something... */
          count++;
          System.out.println("Connection Accepted : " + client.getRemoteAddress() + " -> " + client.getLocalAddress());
        }
        else if (key.isReadable()) {//key is ready for reading
          SocketChannel client = (SocketChannel) key.channel();
          //System.out.println(client);
          //TODO : read request
          //System.out.println("reading from client...");

          String client_remote_address = client.getRemoteAddress().toString();
          String request_string = read(client);

          System.out.println("Request : " + request_string + " from " + client_remote_address);
          //System.out.println("reading done");
          if(request_string == null || request_string.length() == 0){
            continue;
          }
          Event ev = HTTPParser.parse(client,key,request_string);
          System.out.println("Parse Complete");
          EVENT_QUEUE.push(ev);
          System.out.println("Parsed event enqueued");
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
      buffer.clear();
      //TODO : timeout?

      while(true){
        int bytes_read = client.read(buffer);
        //System.out.println("buffer reading..." + bytes_read);

        //exit conditions
        if(bytes_read == 0){
          break;
        }
        if(bytes_read == -1){//client finished sending
          //System.out.println("closing the channel...");
          client.close();
          break;
        }
      }



      /** produce string from buffer */
      byte[] bytes = new byte[buffer.position()];
      buffer.flip();
      buffer.get(bytes);
      return new String(bytes);
      //System.out.println(buffer);

      //buffer.rewind();
      //return StandardCharsets.UTF_8.decode(buffer).toString();
    }
    catch(Exception ex){
      return null;
    }
  }
}