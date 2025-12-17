package net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
  public static void main(String[] args) throws IOException {
    final var server = ServerSocketChannel.open();
    server.bind(new InetSocketAddress("192.168.0.15", 8080));
    System.out.println("Server started...");

    while (true) {
      final var client = server.accept();
      final var buf = ByteBuffer.allocate(64);
      final var sb = new StringBuilder();
      while (client.read(buf) > 0) {
        buf.flip();

        sb.append(new String(buf.array(), 0, buf.limit(), StandardCharsets.UTF_8));

        buf.clear();
      }

      System.out.println(client.getRemoteAddress().toString().substring(1) + ": " + sb);
      client.write(ByteBuffer.wrap(sb.toString().getBytes(StandardCharsets.UTF_8)));
      client.close();
    }
  }
}
