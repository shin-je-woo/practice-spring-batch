package practice.batch.chunk.jdbcCursorItemReader;

import lombok.Data;

@Data
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;
}
