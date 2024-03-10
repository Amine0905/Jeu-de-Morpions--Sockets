package ex2;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class Client {
  private static DatagramSocket clientSocket;
  private static int serverPort = 3000;
  private static int clientPort;
  private static InetAddress serverAddress;
  private static Scanner sc = new Scanner(System.in);

  public static void main(String[] args) throws Exception {
    if (args.length < 1)
      System.exit(0);

    if(Objects.equals(args[0], "local"))
      serverAddress = InetAddress.getLocalHost();
    else
      serverAddress = InetAddress.getByName(args[0]);

    clientPort = Integer.parseInt(args[1]);

    clientSocket = new DatagramSocket(clientPort);

    String serverMessage = "adress";
    byte[] sendData = serverMessage.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
    clientSocket.send(sendPacket);

    socketReceive();
  }

  private static void socketSend() throws IOException {
    int x = -1;
    int y = -1;
    do {
      System.out.println("Entrer la position x (entre 0 et 2): ");
      x = sc.nextInt();
      System.out.println("Entrer la position y (entre 0 et 2): ");
      y = sc.nextInt();
    } while(x > 2 || x < 0 || y > 2 || y < 0);
    MorpionData data = new MorpionData(x, y);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(data);
    byte[] dataBytes = baos.toByteArray();
    DatagramPacket sendPacket = new DatagramPacket(dataBytes, dataBytes.length, serverAddress, serverPort);
    clientSocket.send(sendPacket);
    socketReceive();
  }

  private static void socketReceive() throws IOException {
    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String serverMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
    System.out.println(serverMessage);
    if (!serverMessage.startsWith("\n\nFin")){
      if(serverMessage.startsWith("\n\nC'est"))
        socketReceive();
      socketSend();
    }
    else
      System.exit(0);
  }
}
