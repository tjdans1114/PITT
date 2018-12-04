package PITT;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class MainServer {
  public static EventQueue EVENT_QUEUE = new EventQueue();
  public static EventLoop event_loop = new EventLoop(EVENT_QUEUE);//share EVENT_QUEUE. between MainSerer & EventLoop


  public static void main(String[] args) throws IOException{
    /* Basic Server Configuration */

    Selector selector = Selector.open();
    ServerSocketChannel socket = ServerSocketChannel.open();
    InetSocketAddress address = new InetSocketAddress(Global.IP,Global.PORT);
    socket.bind(address);
    socket.configureBlocking(false);

      //ops : operation set
    int ops = socket.validOps();
    socket.register(selector,ops,null);

    /* Running MainServer */
    System.out.println("server running ... ");
    int count = 0; //# of clients

    event_loop.start();//run event loop TODO : not working well

    while(true) {//TODO : try-catch for buffer overflow exceptions... or other exceptions. server should keep running
      /**/
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

          /** do something... */
          count++;
          System.out.println("Connection Accepted : " + client.getRemoteAddress() + " -> " + client.getLocalAddress());
        }
        else if (key.isReadable()) {//key is ready for reading
          SocketChannel client = (SocketChannel) key.channel();
          //System.out.println(client);
          //TODO : read request
          //System.out.println("reading from client...");
          if(!client.isConnected()){
            continue;
          }

//          String client_remote_address = client.getRemoteAddress().toString();
          try{
            String request_string = read(client);

            //System.out.println("reading done");
            if(request_string == null || request_string.length() == 0){
              continue;
            }
//          System.out.println("Request : " + request_string + " from " + client_remote_address);

            System.out.println(request_string);
            Event ev = HTTPParser.parse(client,key,request_string);
//          System.out.println("Parse Complete");
            EVENT_QUEUE.push(ev);

          }
          catch(TimeoutException tex){
            EVENT_QUEUE.push(new Event(client,key,408));
          }
          catch(BufferOverflowException boex){
            EVENT_QUEUE.push(new Event(client,key,413));
          }

//          System.out.println("Parsed event enqueued");
        }

        key_iterator.remove();//remove current key
      }
    }
  }

  static String read(SocketChannel client) throws TimeoutException, BufferOverflowException{
    try{
      ByteBuffer buffer = ByteBuffer.allocate(Global.BUFFER_SIZE);
      buffer.clear();
      //TODO : timeout? read only once or multiple times?

      long start_time = System.currentTimeMillis();
      long total_bytes_read = 0;

      while(true){//verification required
        long bytes_read = client.read(buffer);

        //Timeout
        long current_time = System.currentTimeMillis();
        if(current_time - start_time > Global.TIMEOUT){
          throw new TimeoutException();
        }

        //Buffer Expansion
        total_bytes_read += bytes_read;
        if(total_bytes_read >= Global.LARGE_BUFFER_SIZE){
          //413
          System.out.println("case -1");
          throw new BufferOverflowException();
        }
        else if(total_bytes_read >= buffer.capacity()){
          System.out.println("case -2");
          ByteBuffer new_buffer = ByteBuffer.allocate(Global.LARGE_BUFFER_SIZE);
          buffer.flip();
          new_buffer.put(buffer);

          buffer = new_buffer;
        }


        //exit conditions
        if(bytes_read == 0){
          break;
        }
        if(bytes_read == -1){//client finished sending
//          System.out.println(client);
//          System.out.println("read finished : closing the channel... " + client);
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
    catch(TimeoutException tex){
      //for 408
      throw new TimeoutException("Read Timeout!");
    }
    catch(BufferOverflowException boex){
      //for 413
      throw new BufferOverflowException();
    }
    catch(Exception ex){
      return null;
    }
  }
}