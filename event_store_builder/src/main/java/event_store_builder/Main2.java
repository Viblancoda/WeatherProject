package event_store_builder;

import org.apache.activemq.ActiveMQConnection;

public class Main2 {
    public static void main(String[] args) {
        try {
            WeatherMessageReceiver messageReceiver = new WeatherMessageReceiver();
            messageReceiver.start();

            Thread.sleep(Thread.MAX_PRIORITY);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error in the main process: " + e.getMessage());
        }
    }
}
