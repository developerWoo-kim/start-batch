package toy.startbatch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toy.startbatch.domain.Market;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;


    /**
     * exampleJob 생성
     * @return
     * @throws Exception
     */
    @Bean
    @Qualifier("exampleJob")
    public Job exampleJob() throws Exception {
        return jobBuilderFactory.get("exampleJob")
                .start(exampleStep())
                .build();
    }

    /**
     * exampleStep 생성
     * @return
     */
    @Bean
    @JobScope
    public Step exampleStep() {
        return stepBuilderFactory.get("exampleStep")
                .<Market, Market> chunk(10) // Chuck 사이즈는 한번에 처리될 트랜잭선 단위
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Market> reader(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.info("==> reader value : " + requestDate);

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("price", 1000);

        return new JpaPagingItemReaderBuilder<Market>()
                .pageSize(10) // 한번에 조회할 item의 양
                .parameterValues(parameterValues)
                .queryString("SELECT m FROM Market m WHERE m.price >= : price")
                .entityManagerFactory(entityManagerFactory)
                .name("JpaPagingItemReader")
                .build();
    };

    @Bean
    @StepScope
    public ItemProcessor<Market, Market> processor(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new ItemProcessor<Market, Market>() {
            @Override
            public Market process(Market market) throws Exception {
                log.info("==> processor Market : " + market);
                log.info("==> processor value : " + requestDate);

                market.setPrice(market.getPrice() + 100);
                return market;
            }
        };
    };

    @Bean
    @StepScope
    public ItemWriter<Market> writer(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.info("==> writer value : " + requestDate);

        return new JpaItemWriterBuilder<Market>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    };

}
