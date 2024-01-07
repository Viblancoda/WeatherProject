package control;

public interface Listener {
    void consume(String message, String topic);
}