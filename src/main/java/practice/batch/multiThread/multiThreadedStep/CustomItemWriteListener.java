package practice.batch.multiThread.multiThreadedStep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class CustomItemWriteListener implements ItemWriteListener<JdbcConvertedCustomer> {

    @Override
    public void afterWrite(final Chunk<? extends JdbcConvertedCustomer> items) {
        System.out.println("Thread : " + Thread.currentThread().getName() + ", write item size : " + items.size());
    }
}
