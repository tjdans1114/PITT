package PITT;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

import java.util.*;

public class Client {
  public static void main(String[] args) throws IOException,InterruptedException {
    InetSocketAddress address = new InetSocketAddress("localhost",1111);
    SocketChannel client = SocketChannel.open(address);

    System.out.println("connecting to port : " + 1111);

    //Test codes
    ArrayList<String> string_list = new ArrayList<String>();
    string_list.add("GET /Logo.png HTTP/1.1\n");
//    string_list.add("test2");
//    string_list.add("test3");
//    string_list.add("test4");
//    string_list.add("FINISH");

    for(String string : string_list){
      byte[] message = new String(string).getBytes();
      ByteBuffer buffer = ByteBuffer.wrap(message);

      client.write(buffer);//write message into server (i.e. buffer -> channel ?)

      System.out.println("sending : " + string);
      buffer.clear();

      // wait for 2 seconds before sending next message
      //Thread.sleep(1000);
    }

    //Test codes end
  }
}
