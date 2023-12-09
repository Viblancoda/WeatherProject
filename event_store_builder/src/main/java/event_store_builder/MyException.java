package event_store_builder;

import javax.jms.JMSException;


public class MyException extends Exception {
    public MyException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class JMSConnectionException extends MyException {
        public JMSConnectionException(String message, JMSException cause) {
            super(message, cause);
        }
    }

    public static class JMSMessageReceivingException extends MyException {
        public JMSMessageReceivingException(String message, JMSException cause) {
            super(message, cause);
        }
    }

    public static class JMSMessageProcessingException extends MyException {
        public JMSMessageProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class FileEventStoreException extends MyException {
        public FileEventStoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

