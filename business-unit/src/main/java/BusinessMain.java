import control.AMQSubscriber;
import control.DataMartBuilder;
import view.UserInterface;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BusinessMain {

    private static final String directory = "datamart" + File.separator + "eventstore";

    public static void main(String[] args) {
        try {
            String amqBrokerURL = args[0];

            AMQSubscriber amqSubscriber = new AMQSubscriber(amqBrokerURL);
            DataMartBuilder dataMartBuilder = new DataMartBuilder(directory);

            CountDownLatch latch = new CountDownLatch(1);

            amqSubscriber.start((message, topicName) -> {
                dataMartBuilder.consume(message, topicName);
                latch.countDown();
            });

            if (!latch.await(5, TimeUnit.SECONDS)) {
                System.out.println("No messages were received during the specified time.");
            }

            Thread.sleep(5000);

            amqSubscriber.closeConnection();

            UserInterface ui = new UserInterface();
            ui.selectLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}