package ex1;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Server {
  public static void main(String[] args) throws Exception {
    DatagramSocket ds = new DatagramSocket();
    String str = "Hello from server";
    InetAddress ip = InetAddress.getByName("127.0.0.1");
    int port = 3000;
    DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, port);
    ds.send(dp);
    ds.close();
  }
}
