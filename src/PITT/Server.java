package PITT;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server{
  public static void main(String[] args) throws IOException{
    /* PITT.Server Config ... */
    //selector : selects SelectableChannel Objects
    Selector selector = Selector.open();

    //socket : selectable channel for stream-oriented listening sockets
    ServerSocketChannel socket = ServerSocketChannel.open();
    InetSocketAddress address = new InetSocketAddress("localhost",1111);
    // Binds the channel's socket to a local address and configures the socket to listen for connections
    socket.bind(address);
    socket.configureBlocking(false);//configure : Non-Blocking

      //ops : operation set
    int ops = socket.validOps(); // ops == SelectionKey.OP_ACCEPT
    SelectionKey selection_key = socket.register(selector,ops,null);

    /* Running PITT.Server ... */
    //Infinite loop : PITT.Server keeps running
    System.out.println("server running ... ");
    while(true) {
      // Selects a set of keys whose corresponding channels are ready for I/O operations
      selector.select();

      // token representing the registration of a SelectableChannel with a Selector
      Set<SelectionKey> keys = selector.selectedKeys();
      Iterator<SelectionKey> key_iterator = keys.iterator();
      while (key_iterator.hasNext()) {//don't use range-for
        SelectionKey key = key_iterator.next();
        //System.out.println(key);
        // Tests whether this key's channel is ready to accept a new socket connection
        if (key.isAcceptable()) {//key can accept client
          SocketChannel client = socket.accept(); // get? client
          client.configureBlocking(false);//non-blocking
          client.register(selector, SelectionKey.OP_READ);//why read?

          System.out.println("Connection Accepted : " + client.getLocalAddress());
        }
        else if (key.isReadable()) {//key is ready for reading
          SocketChannel client = (SocketChannel) key.channel();

          ByteBuffer buffer = ByteBuffer.allocate(256);
          client.read(buffer); //read message from client

          String result = new String(buffer.array()).trim();//trim : removes whitespace
          if(!result.equals("")){
            System.out.println("message received : " + result);
          }

          if (result.equals("FINISH")) {//exit code
            client.close();
            System.out.println("Closing this client... but server keeps running! Try running client again to establish new connection");
          }
        }
        key_iterator.remove();//remove current key
      }
    }
  }
}
