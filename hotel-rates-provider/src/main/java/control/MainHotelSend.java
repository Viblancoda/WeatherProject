package control;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainHotelSend {
    public static void main(String[] args) {

        JMSHotelStore jmsHotelStore = new JMSHotelStore(args[0]);
        HotelController hotelController = new HotelController(jmsHotelStore);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                hotelController.execute();
            }
        }, 0, TimeUnit.HOURS.toMillis(6));
    }
}
