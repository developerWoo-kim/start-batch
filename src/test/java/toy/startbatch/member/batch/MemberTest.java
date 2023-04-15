package toy.startbatch.member.batch;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toy.startbatch.member.domain.Member;
import toy.startbatch.member.domain.MemberConnectHist;
import toy.startbatch.member.domain.QMemberConnectHist;
import toy.startbatch.member.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

import static toy.startbatch.member.domain.QMemberConnectHist.memberConnectHist;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("장기 미접속 회원 조회")
    public void 장기_미접속_회원_조회() {
        List<MemberConnectHist> memberConnectHistList = memberRepository.findTargetOfDormantSmsRecepMember();


        memberConnectHistList.forEach(s -> {
            System.out.println(s.getMemberId() + " : " + s.getConnectDt());
        });
        System.out.println(memberConnectHistList.size() + "<<<<<<<");
        Assertions.assertThat(memberConnectHistList.size()).isEqualTo(100000);
        LocalDate nowDate = LocalDate.now();
        LocalDate beforeDate = nowDate.minusMonths(11);
        System.out.println(beforeDate);
    }

    @Test
    @DisplayName("회원 조회")
    public void 회원_조회() {
        List<Member> members = memberRepository.findTargetOfConvertForDormantMember();
        System.out.println(members.size() + "<<<<<<<");
        Assertions.assertThat(members.size()).isEqualTo(200000);
    }


}
