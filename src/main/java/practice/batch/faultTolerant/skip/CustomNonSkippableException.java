package practice.batch.faultTolerant.skip;

public class CustomNonSkippableException extends RuntimeException {
    public CustomNonSkippableException(final String s) {
        super(s);
    }
}
