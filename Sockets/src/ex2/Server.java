package ex2;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Random;

public class Server {
  private static String[][] grid = new String[3][3];
  private static boolean eog = false;
  private static DatagramSocket serverSocket;
  private static int serverPort = 3000;
  private static int p1Port;
  private static int p2Port;
  private static InetAddress p1Address;
  private static InetAddress p2Address;
  private static InetAddress serverAddress;
  private static int role = 1;

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        grid[i][j] = "";
    try {
      URL url = new URL("https://checkip.amazonaws.com");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String ipAddress = in.readLine();
      System.out.println("Your public IP address is: " + ipAddress);
      serverAddress = InetAddress.getByName(ipAddress);
    } catch (IOException e) {
      e.printStackTrace();
    }
    serverSocket = new DatagramSocket(serverPort);
    socketReceiveAdresse();
    socketReceiveAdresse();

    socketSend("A vous de jouer");
  }

  private static void socketSend(String message) throws IOException, ClassNotFoundException {
    if(role == 1){
      String serverMessage = "\n\n" + message + "\n"  + displayGrid();
      byte[] sendData = serverMessage.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, p1Address, p1Port);
      serverSocket.send(sendPacket);

      serverMessage = "\n\nC'est le role de l'autre joueur";
      sendData = serverMessage.getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, p2Address, p2Port);
      serverSocket.send(sendPacket);
    }
    if(role == 2){
      String serverMessage = "\n\n" + message + "\n"  + displayGrid();
      byte[] sendData = serverMessage.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, p2Address, p2Port);
      serverSocket.send(sendPacket);

      serverMessage = "\n\nC'est le role de l'autre joueur";
      sendData = serverMessage.getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, p1Address, p1Port);
      serverSocket.send(sendPacket);
    }

    if(eog)
      System.exit(0);
    finPartie();
    if(!eog)
      socketReceive();
  }

  private static void socketReceive() throws IOException, ClassNotFoundException {
    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    serverSocket.receive(receivePacket);

    ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
    ObjectInputStream ois = new ObjectInputStream(bais);
    MorpionData data = (MorpionData) ois.readObject();

    int x = data.getPosX();
    int y = data.getPosY();

    if (!Objects.equals(grid[x][y], ""))
      socketSend("Il faut choisir une case vide");
    else {
      if(Objects.equals(receivePacket.getAddress().getHostAddress(), p1Address.getHostAddress()) && receivePacket.getPort() == p1Port)
        grid[x][y] = "X";
      else if(Objects.equals(receivePacket.getAddress().getHostAddress(), p2Address.getHostAddress()) && receivePacket.getPort() == p2Port)
        grid[x][y] = "O";
      else
        System.exit(0);
      if(role == 1)
        role = 2;
      else if(role == 2)
        role = 1;
      else
        System.exit(0);

      finPartie();
      socketSend("A vous de jouer");
    }
  }

  private static void finPartie() throws IOException {
    boolean b = false;
    for(int i = 0; i < 3; i++){
      if(Objects.equals(grid[i][0], ""))
        continue;
      if(grid[i][0] == grid[i][1] && grid[i][0] == grid[i][2]){
        eog = true;
        if(grid[i][0] == "X") {
          socketSendP("Fin de la partie\nVous avez gagne !\n", 1);
          socketSendP("Fin de la partie\nVous avez perdu !\n", 2);
        }
        else {
          socketSendP("Fin de la partie\nVous avez perdu !\n", 1);
          socketSendP("Fin de la partie\nVous avez gagne !\n", 2);
        }
      }
    }

    for(int i = 0; i < 3; i++){
      if(Objects.equals(grid[0][i], ""))
        continue;
      if(grid[0][i] == grid[1][i] && grid[0][i] == grid[2][i]){
        eog = true;
        if(grid[0][i] == "X") {
          socketSendP("Fin de la partie\nVous avez gagne !\n", 1);
          socketSendP("Fin de la partie\nVous avez perdu !\n", 2);
        }
        else {
          socketSendP("Fin de la partie\nVous avez perdu !\n", 1);
          socketSendP("Fin de la partie\nVous avez gagne !\n", 2);
        }
      }
    }

    if(!(grid[1][1] == "")) {
      if (grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
        eog = true;
        if (grid[0][0] == "X"){
          socketSendP("Fin de la partie\nVous avez gagne !\n", 1);
          socketSendP("Fin de la partie\nVous avez perdu !\n", 2);
        }
        else{
          socketSendP("Fin de la partie\nVous avez perdu !\n", 1);
          socketSendP("Fin de la partie\nVous avez gagne !\n", 2);
        }
      }
      if (grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
        eog = true;
        if (grid[1][1] == "X"){
          socketSendP("Fin de la partie\nVous avez gagne !\n", 1);
          socketSendP("Fin de la partie\nVous avez perdu !\n", 2);
        }
        else{
          socketSendP("Fin de la partie\nVous avez perdu !\n", 1);
          socketSendP("Fin de la partie\nVous avez gagne !\n", 2);
        }
      }
    }

    b = eog;
    eog = true;
    boolean a = true;

    if(!b) {
      for (int i = 0; i < 3; i++)
        for (int j = 0; j < 3; j++)
          if (Objects.equals(grid[i][j], ""))
            eog = false;
    }

    if(eog){
      socketSendP("Fin de la partie\nMatch nul !\n", 1);
      socketSendP("Fin de la partie\nMatch nul !\n", 2);
    }
  }

  private static void socketSendP(String message, int i) throws IOException {
    InetAddress adress = InetAddress.getLocalHost();
    int port = 0;
    if(i == 1){
      adress = p1Address;
      port = p1Port;
    }
    else if(i == 2){
      adress = p2Address;
      port = p2Port;
    }
    else
      System.exit(0);
    String serverMessage = "\n\n" + message + "\n"  + displayGrid();
    byte[] sendData = serverMessage.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, adress, port);
    serverSocket.send(sendPacket);
  }
  private static String displayGrid() {
    StringBuilder s = new StringBuilder();
    s.append(" x\\y |  0  |  1  |  2  |\n");
    s.append(" ----|-----|-----|-----|\n");
    for (int i = 0; i < 3; i++) {
      s.append("  ").append(i).append("  |");
      for (int j = 0; j < 3; j++) {
        s.append("  ").append(Objects.equals(grid[i][j], "") ? " " : grid[i][j]).append("  |");
      }
      s.append("\n");
      s.append(" ----|-----|-----|-----|\n");
    }
    return s.toString();
  }

  private static void socketReceiveAdresse() throws IOException {
    byte[] receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    serverSocket.receive(receivePacket);
    String serverMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
    System.out.println(serverMessage + " " + receivePacket.getSocketAddress());
    if(p1Address == null) {
      p1Address = receivePacket.getAddress();
      p1Port = receivePacket.getPort();
    }
    else {
      p2Address = receivePacket.getAddress();
      p2Port = receivePacket.getPort();
    }
    System.out.println(p1Address);
    System.out.println(p2Address);
  }
}
