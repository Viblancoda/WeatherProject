package event_store_builder;

import javax.jms.JMSException;

public class CustomJMSException extends Exception {
    public CustomJMSException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class JMSConnectionException extends CustomJMSException {
        public JMSConnectionException(String message, JMSException cause) {
            super(message, cause);
        }
    }

    public static class JMSMessageReceivingException extends CustomJMSException {
        public JMSMessageReceivingException(String message, JMSException cause) {
            super(message, cause);
        }
    }

    public static class JMSMessageProcessingException extends CustomJMSException {
        public JMSMessageProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class FileEventStoreException extends CustomJMSException {
        public FileEventStoreException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}