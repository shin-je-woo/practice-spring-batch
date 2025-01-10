package practice.batch.application.batch.writer;

import jakarta.annotation.Nonnull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import practice.batch.application.batch.dto.ApiRequestDto;

public class ApiItemWriter3 implements ItemWriter<ApiRequestDto> {

    @Override
    public void write(@Nonnull Chunk<? extends ApiRequestDto> chunk) throws Exception {
         
    }
}
