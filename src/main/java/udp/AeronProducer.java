package udp;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import java.nio.ByteBuffer;
import org.agrona.concurrent.BackoffIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

public class AeronProducer {

  public static void main(String[] args) throws InterruptedException {
    final var channel = "aeron:udp?endpoint=localhost:8080";
    final var idle = new BackoffIdleStrategy();
    final var unsafe = new UnsafeBuffer(ByteBuffer.allocate(1024 * 5));

    final var sb = new StringBuilder();
    while (sb.length() < 1500) {
      sb.append("Hello World!\n");
    }

    try (final var driver = MediaDriver.launch();
        final var aeron = Aeron.connect();
        final var pub = aeron.addPublication(channel, 1)) {
      while (!pub.isConnected()) {
        idle.idle();
      }
      for (int i = 0; i < 5; i++) {
        System.out.println("Sending: " + sb.substring(0, 10) + "...");
        unsafe.putStringUtf8(0, sb.toString());
        while (pub.offer(unsafe) < 0) {
          idle.idle();
        }

        Thread.sleep(1000);
      }
    }
  }
}
