package toy.startbatch.member.batch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.startbatch.batch.reader.QuerydslPagingItemReader;
import toy.startbatch.member.domain.Member;
import toy.startbatch.member.domain.MemberConnectHist;
import toy.startbatch.member.domain.QMember;
import toy.startbatch.member.domain.QMemberConnectHist;
import toy.startbatch.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static toy.startbatch.member.domain.QMemberConnectHist.memberConnectHist;

@SpringBootTest
@Transactional
public class MemberBatchTest {
    @Autowired
    private EntityManagerFactory emf;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("의존성 주입 테스트")
    public void diTest() {
        List<Member> member = memberRepository.findByName("닉네임1");
        assertThat(member.size()).isEqualTo(1);
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
                        .selectFrom(memberConnectHist)
                        .groupBy(memberConnectHist.memberId)
                        .having(memberConnectHist.connectDt.max().goe(beforeDate.atStartOfDay())));
        reader.open(new ExecutionContext());

        MemberConnectHist memberConnectHist1 = reader.read();

        System.out.println(memberConnectHist1.getMemberId());
    }
}
