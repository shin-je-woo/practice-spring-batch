package practice.batch.multiThread.multiThreadedStep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class CustomItemProcessListener implements ItemProcessListener<Customer, JdbcConvertedCustomer> {

    @Override
    public void afterProcess(final Customer item, final JdbcConvertedCustomer result) {
        System.out.println("Thread : " + Thread.currentThread().getName() + ", process item : " + item.getId());
    }
}
