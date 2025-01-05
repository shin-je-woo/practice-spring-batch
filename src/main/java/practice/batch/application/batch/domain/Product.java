package practice.batch.application.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    private Long id;
    private String name;
    private int price;
    private String type;

    public static Product of(Long id, String name, int price, String type) {
        return Product.builder()
                .id(Objects.requireNonNull(id))
                .name(name)
                .price(price)
                .type(type)
                .build();
    }
}
