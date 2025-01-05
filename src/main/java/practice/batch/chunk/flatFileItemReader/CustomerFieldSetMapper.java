package practice.batch.chunk.flatFileItemReader;

import jakarta.annotation.Nonnull;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    @Nonnull
    public Customer mapFieldSet(@Nonnull FieldSet fieldSet) throws BindException {
        return new Customer(
                fieldSet.readString(0),
                fieldSet.readInt(1),
                fieldSet.readString(2)
        );
    }
}
