package toy.startbatch.batch.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toy.startbatch.alarm.domain.AlarmSendHist;
import toy.startbatch.batch.reader.QuerydslPagingItemReader;
import toy.startbatch.batch.reader.QuerydslPagingItemReaderJobParameter;
import toy.startbatch.member.domain.MemberConnectHist;
import toy.startbatch.member.domain.QMemberConnectHist;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;

import static toy.startbatch.member.domain.QMemberConnectHist.memberConnectHist;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "memberBatchJob")
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
//    @JobScope
    public Step memberBatchStep() {
        return stepBuilderFactory.get("memberBatchStep")
                .<MemberConnectHist, AlarmSendHist> chunk(100) // Chuck 사이즈는 한번에 처리될 트랜잭선 단위
                .reader(memberBatchReader())
                .processor(memberBatchProcessor())
                .writer(memberBatchWriter())
                .build();
    }

    @Bean
//    @StepScope
    public QuerydslPagingItemReader<MemberConnectHist> memberBatchReader(/*@Value("#{jobParameters[requestDate]}") String requestDate*/) {
//        log.info("==> reader value : " + requestDate);
        LocalDate nowDate = LocalDate.now();
        LocalDate beforeDate = nowDate.minusMonths(11);

        int pageSize = 100;

        return new QuerydslPagingItemReader<>(entityManagerFactory, pageSize,
                jpaQueryFactory -> jpaQueryFactory
                        .select(
                                Projections.fields(MemberConnectHist.class,
                                        memberConnectHist.memberId,
                                        memberConnectHist.connectDt.max().as("connectDt")
                                )
                        )
                        .from(memberConnectHist)
                        .groupBy(memberConnectHist.memberId)
                        .having(memberConnectHist.connectDt.max().loe(beforeDate.atStartOfDay()))
        );
    };

    public ItemProcessor<MemberConnectHist, AlarmSendHist> memberBatchProcessor(/*@Value("#{jobParameters[requestDate]}") String requestDate*/) {
        log.info("==> processor value : " + memberConnectHist);
        LocalDate nowDate = LocalDate.now();
        return memberConnectHist -> {
            return AlarmSendHist.builder()
                    .sendType("kakao")
                    .successAt("Y")
                    .sendDt(nowDate)
                    .memberId(memberConnectHist.getMemberId())
                    .build();
        };
    };

    @Bean
//    @StepScope
    public ItemWriter<AlarmSendHist> memberBatchWriter(/*@Value("#{jobParameters[requestDate]}") String requestDate*/) {
//        log.info("==> writer value : " + requestDate);

        return new JpaItemWriterBuilder<AlarmSendHist>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    };

}
