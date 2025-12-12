import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import java.nio.ByteBuffer;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

public class Program {
  public static void main(String[] args) {
    final var channel = "aeron:ipc";
    final var message = "Hello World!";
    final var idle = new SleepingIdleStrategy();
    final var unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(256));
    try (final var driver = MediaDriver.launch();
        final var aeron = Aeron.connect();
        final var sub = aeron.addSubscription(channel, 10);
        final var pub = aeron.addPublication(channel, 10)) {
      while (!pub.isConnected()) {
        idle.idle();
      }

      unsafeBuffer.putStringUtf8(0, message);
      System.out.println("sending: " + message);
      while (pub.offer(unsafeBuffer) < 0) {
        idle.idle();
      }
      FragmentHandler handler =
          (buffer, offset, length, header) ->
              System.out.println("Received: " + buffer.getStringUtf8(offset));

      while (sub.poll(handler, 1) <= 0) {
        idle.idle();
      }
    }
  }
}
