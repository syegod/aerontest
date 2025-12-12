package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

public class Server {
  public static void main(String[] args) throws IOException {
    final var serverSocket = new ServerSocket();
    serverSocket.bind(new InetSocketAddress("192.168.0.15", 8080));
    System.out.println(
        "Server started on " + serverSocket.getLocalSocketAddress().toString().substring(1));

    while (true) {
      final var socket = serverSocket.accept();

      final var in = socket.getInputStream();
      final var out = socket.getOutputStream();
      final var bytes = new byte[128];
      int rcvd;
      while ((rcvd = in.read(bytes)) != -1) {
        System.out.println(
            socket.getRemoteSocketAddress() + ": " + new String(bytes, StandardCharsets.UTF_8));
        out.write(bytes);
      }
      socket.close();
    }
  }
}
