package control;

public class MainHotelSend {
    public static void main(String[] args) {

        JMSHotelStore jmsHotelStore = new JMSHotelStore(args[0]);
        HotelController hotelController = new HotelController(jmsHotelStore);

        hotelController.execute();
    }
}