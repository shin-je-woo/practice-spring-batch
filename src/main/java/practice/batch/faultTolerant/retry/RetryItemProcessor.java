package practice.batch.faultTolerant.retry;

import org.springframework.batch.item.ItemProcessor;

public class RetryItemProcessor implements ItemProcessor<Integer, String> {

    private int failedCount = 0;

    @Override
    public String process(final Integer item) throws Exception {
        if (item == 7 || item == 23) {
            failedCount++;
            throw new CustomRetryableException("Process failed count : " + failedCount);
        }
        return String.valueOf(item);
    }
}
