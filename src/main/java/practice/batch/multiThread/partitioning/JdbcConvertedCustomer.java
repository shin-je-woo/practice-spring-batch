package practice.batch.multiThread.partitioning;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JdbcConvertedCustomer {
    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;
}
