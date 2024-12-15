package practice.batch.faultTolerant.retry;

public class CustomRetryableException extends RuntimeException {
    public CustomRetryableException(final String message) {
        super(message);
    }
}
