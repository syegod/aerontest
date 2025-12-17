package udp;

import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.driver.MediaDriver;
import org.agrona.concurrent.SleepingIdleStrategy;

public class AeronConsumer {

  public static void main(String[] args) {
    final var channel = "aeron:udp?endpoint=192.168.0.11:8080";
    final var idle = new SleepingIdleStrategy();

    try (final var driver = MediaDriver.launch();
        final var aeron = Aeron.connect();
        final var sub = aeron.addSubscription(channel, 1)) {
      while (true) {
        final var assembler =
            new FragmentAssembler(
                (buffer, offset, length, header) ->
                    System.out.println("Received: " + buffer.getStringUtf8(offset)));

        while (sub.poll(assembler, 1) < 0) {
          idle.idle();
        }
        idle.idle();
      }
    }
  }
}
