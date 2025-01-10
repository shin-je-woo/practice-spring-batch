package practice.batch.application.batch.rowmapper;

import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;
import practice.batch.application.batch.dto.ProductDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDtoRowMapper implements RowMapper<ProductDto> {

    @Override
    public ProductDto mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return ProductDto.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .price(rs.getInt("price"))
                .type(rs.getString("type") )
                .build();
    }
}
