package net;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
  public static void main(String[] args) throws IOException {
    final var socket = new Socket("localhost", 8080);
    final var message = "Hello World!".getBytes(StandardCharsets.UTF_8);
    final var in = socket.getInputStream();
    final var out = socket.getOutputStream();
    out.write(message);
    socket.shutdownOutput();
    final var bytes = new byte[message.length];
    var rcvd = in.read(bytes);
    while (rcvd > 0) {
      rcvd = in.read(bytes, rcvd, message.length);
    }
    System.out.println(new String(bytes));
    socket.close();
  }
}
