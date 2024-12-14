package practice.batch.faultTolerant.skip;

public class CustomSkippableException extends RuntimeException {
    public CustomSkippableException(final String s) {
        super(s);
    }
}
