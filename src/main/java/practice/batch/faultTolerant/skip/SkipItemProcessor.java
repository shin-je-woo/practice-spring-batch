package practice.batch.faultTolerant.skip;

import org.springframework.batch.item.ItemProcessor;

public class SkipItemProcessor implements ItemProcessor<Integer, String> {
    private int skipCount;

    @Override
    public String process(final Integer item) throws Exception {
        if (item == 6 || item == 7) {
            throw new CustomSkippableException("Process failed, skip count = " + ++skipCount);
        } else {
            System.out.println("ItemProcessor : " + item);
        }
        return String.valueOf(-item);
    }
}
