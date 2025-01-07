package practice.batch.application.batch.partition;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import practice.batch.application.batch.utils.QueryGenerator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ProductPartitioner implements Partitioner {
    private final DataSource dataSource;

    @Override
    @Nonnull
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<String> productTypeList = QueryGenerator.getProductTypeList(dataSource);
        Map<String, ExecutionContext> result = new HashMap<>();

        AtomicInteger number = new AtomicInteger();
        productTypeList.forEach(productType -> {
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.put("productType", productType);
            result.put("partition" + number.getAndIncrement(), executionContext);
        });

        return result;
    }
}
