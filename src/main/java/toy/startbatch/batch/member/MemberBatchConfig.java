package toy.startbatch.batch.member;

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
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toy.startbatch.batch.reader.QuerydslPagingItemReader;
import toy.startbatch.member.domain.MemberConnectHist;
import toy.startbatch.member.domain.QMemberConnectHist;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;

import static toy.startbatch.member.domain.QMemberConnectHist.memberConnectHist;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MemberBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private int chunkSize;

    @Value("${chunkSize:100}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * exampleJob 생성
     * @return
     * @throws Exception
     */
    @Bean
    @Qualifier("memberBatchJob")
    public Job memberBatchJob() throws Exception {
        return jobBuilderFactory.get("memberBatchJob")
                .start(memberBatchStep())
                .build();
    }

    /**
     * exampleStep 생성
     * @return
     */
    @Bean
    @JobScope
    public Step memberBatchStep() {
        return stepBuilderFactory.get("memberBatchStep")
                .<MemberConnectHist, MemberConnectHist> chunk(10) // Chuck 사이즈는 한번에 처리될 트랜잭선 단위
                .reader(memberBatchReader(null))
                .processor(memberBatchProcessor(null))
                .writer(memberBatchWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public QuerydslPagingItemReader<MemberConnectHist> memberBatchReader(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.info("==> reader value : " + requestDate);
        LocalDate nowDate = LocalDate.now();
        LocalDate beforeDate = nowDate.minusMonths(11);

        return new QuerydslPagingItemReader<>(entityManagerFactory, chunkSize, jpaQueryFactory -> jpaQueryFactory
                .selectFrom(memberConnectHist)
                .groupBy(memberConnectHist.memberId)
                .having(memberConnectHist.connectDt.max().goe(beforeDate.atStartOfDay())));
//        return new JpaPagingItemReaderBuilder<Market>()
//                .pageSize(10) // 한번에 조회할 item의 양
//                .parameterValues(parameterValues)
//                .queryString("SELECT m FROM Market m WHERE m.price >= : price")
//                .entityManagerFactory(entityManagerFactory)
//                .name("JpaPagingItemReader")
//                .build();
    };

    @Bean
    @StepScope
    public ItemProcessor<MemberConnectHist, MemberConnectHist> memberBatchProcessor(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new ItemProcessor<MemberConnectHist, MemberConnectHist>() {
            @Override
            public MemberConnectHist process(MemberConnectHist memberConnectHist) throws Exception {
                log.info("==> processor value : " + requestDate);


                return new MemberConnectHist();
            }
        };
    };

    @Bean
    @StepScope
    public ItemWriter<MemberConnectHist> memberBatchWriter(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.info("==> writer value : " + requestDate);

        return new JpaItemWriterBuilder<MemberConnectHist>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    };

}
