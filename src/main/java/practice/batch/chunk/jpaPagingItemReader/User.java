package practice.batch.chunk.jpaPagingItemReader;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private int age;
    @OneToOne(mappedBy = "user")
    private Address address;
}
