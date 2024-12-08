package practice.batch.chunk.jpaCursorItemReader;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;
}
