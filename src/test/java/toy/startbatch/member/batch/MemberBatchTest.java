package toy.startbatch.member.batch;

import com.querydsl.core.types.Projections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import toy.startbatch.alarm.repository.AlarmRepository;
import toy.startbatch.batch.reader.QuerydslPagingItemReader;
import toy.startbatch.member.domain.Member;
import toy.startbatch.member.domain.MemberConnectHist;
import toy.startbatch.member.domain.QMember;
import toy.startbatch.member.repository.MemberRepository;

import javax.batch.runtime.BatchStatus;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static toy.startbatch.member.domain.QMemberConnectHist.memberConnectHist;

@SpringBootTest
@TestPropertySource(properties = {"spring.batch.job.names= memberBatchJob"})
public class MemberBatchTest{
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private EntityManagerFactory emf;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AlarmRepository alarmRepository;

    @Test
    @DisplayName("의존성 주입 테스트")
    public void diTest() {
        List<Member> member = memberRepository.findByName("닉네임1");
        assertThat(member.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("접속 이력 조회")
    public void findHist() {
        MemberConnectHist memberConnectHist = memberRepository.findOneMemberConnectHist(100001L);

        String str = "문자";
        str.equals("문자");

        int number = 123;

        List list = new ArrayList<>();

        Member member = new Member();
        member.equals(memberConnectHist);

        Long memberId = memberConnectHist.getMemberId();
        Long memberId2 = 100001L;

        System.out.println("connectId = " + memberConnectHist.getConnectId());

    }

    @Test
    @DisplayName("reader가 정상적으로 값을 반환한다.")
    public void readerTest() throws Exception {
        LocalDate txDate = LocalDate.of(2021, 4, 9);

        int pageSize = 1;

        QuerydslPagingItemReader<Member> reader = new QuerydslPagingItemReader<>(emf, pageSize,
                queryFactory -> queryFactory
                        .selectFrom(QMember.member)
                        .where(QMember.member.name.eq("닉네임1"))
        );

        reader.open(new ExecutionContext());

        Member read1 = reader.read();
        Member read2 = reader.read();
        System.out.println(read1.getName());
        assertThat(read1.getName()).isEqualTo("닉네임1");
        assertThat(read2).isNull();
    }

    @Test
    @DisplayName("빈값일경우 null 반환된다.")
    public void nullReturnTest() throws Exception {
        String name = "김건우";

        int pageSize = 1;

        QuerydslPagingItemReader<Member> reader = new QuerydslPagingItemReader<>(emf, pageSize, queryFactory -> queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.name.eq(name)));

        reader.open(new ExecutionContext());

        Member read1 = reader.read();

        assertThat(read1).isNull();
    }

    @Test
    @DisplayName("마지막 접속 일자가 11개월전인 사용자들 Read 테스트")
    public void 마지막_접속_일자가_11개월_전인_사용자_read_테스트() throws Exception {
        LocalDate nowDate = LocalDate.now();
        LocalDate beforeDate = nowDate.minusMonths(11);

        int pageSize = 100;

        QuerydslPagingItemReader<MemberConnectHist> reader = new QuerydslPagingItemReader<>(emf, pageSize,
                jpaQueryFactory -> jpaQueryFactory
                        .select(
                                Projections.fields(MemberConnectHist.class,
                                        memberConnectHist.memberId,
                                        memberConnectHist.connectDt.max().as("connectDt")
                                )
                        )
                        .from(memberConnectHist)
                        .groupBy(memberConnectHist.memberId)
                        .having(memberConnectHist.connectDt.max().loe(beforeDate.atStartOfDay())));
        reader.open(new ExecutionContext());

        MemberConnectHist memberConnectHist1 = memberConnectHist1 = reader.read();
//        for(int i=0; i < 100; i++) {
//            if(i == 99) {
//                memberConnectHist1 = reader.read();
//            } else {
//                reader.read();
//            }
//        }
        System.out.println(memberConnectHist1.getMemberId());
    }

    @Test
    @DisplayName("휴면전환 대상 회원(11개월 미접속자)을 조회하여 알림톡을 발송하고 이력을 쌓는 배치 테스트")
    public void 알림톡을_발송하고_이력을_쌓는다() throws Exception{
        LocalDate txDate = LocalDate.of(2023,04,12);

        JobParameters jobParameters = new JobParametersBuilder(jobLauncherTestUtils.getUniqueJobParameters())
                .addString("txDate", txDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getStatus()).isEqualTo(org.springframework.batch.core.BatchStatus.COMPLETED);


        jobExecution.getStatus().equals(BatchStatus.COMPLETED);


//        if() {
//            System.out.println("true");
//        } else {
//            System.out.println("false");
//        }
//        List<AlarmSendHist> alarmSendHistList = alarmRepository.findAll();
//        assertThat(alarmSendHistList.size()).isEqualTo(999);
    }

}
