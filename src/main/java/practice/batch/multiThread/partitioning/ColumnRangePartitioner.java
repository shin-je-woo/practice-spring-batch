package practice.batch.multiThread.partitioning;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple minded partitioner for a range of values of a column in a database table. Works
 * best if the values are uniformly distributed (e.g. auto-generated primary key values).
 *
 * @author Dave Syer
 *
 */
public class ColumnRangePartitioner implements Partitioner {
    private JdbcOperations jdbcTemplate;
    private String table;
    private String column;

    public void setTable(String table) {
        this.table = table;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * Partition a database table assuming that the data in the column specified are
     * uniformly distributed. The execution context values will have keys
     * <code>minValue</code> and <code>maxValue</code> specifying the range of values to
     * consider in each partition.
     *
     * @see Partitioner#partition(int)
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int min = jdbcTemplate.queryForObject("SELECT MIN(" + column + ") from " + table, Integer.class); // 1
        int max = jdbcTemplate.queryForObject("SELECT MAX(" + column + ") from " + table, Integer.class); // 1000
        int targetSize = (max - min) / gridSize + 1; // (1000 - 1) / 4 + 1 = 250

        Map<String, ExecutionContext> result = new HashMap<>();
        int number = 0;
        int start = min; // 1
        int end = start + targetSize - 1; // 250

        while (start <= max) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);

            if (end >= max) {
                end = max;
            }
            value.putInt("minValue", start);
            value.putInt("maxValue", end);
            start += targetSize; // 251, 501, 751, 1001
            end += targetSize; // 500, 750, 1000, 1250
            number++;
        }

        return result;
    }

}