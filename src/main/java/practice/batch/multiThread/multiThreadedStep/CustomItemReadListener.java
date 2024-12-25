package practice.batch.multiThread.multiThreadedStep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemReadListener implements ItemReadListener<Customer> {

    @Override
    public void afterRead(final Customer item) {
        System.out.println("Thread : " + Thread.currentThread().getName() + ", read item : " + item.getId());
    }
}
