package practice.batch.faultTolerant.skip;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class SkipItemWriter implements ItemWriter<String> {

    @Override
    public void write(final Chunk<? extends String> chunk) throws Exception {
        chunk.forEach(item -> {
            if (item.equals("-12")) {
                System.out.println("ItemWriter : " + item);
                throw new CustomSkippableException("writeFailed item = " + item);
            } else {
                System.out.println("ItemWriter : " + item);
            }
        });
    }
}
