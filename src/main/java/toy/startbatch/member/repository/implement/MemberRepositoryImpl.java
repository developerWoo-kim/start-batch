package toy.startbatch.member.repository.implement;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.startbatch.member.domain.Member;
import toy.startbatch.member.domain.MemberConnectHist;
import toy.startbatch.member.domain.QMember;
import toy.startbatch.member.domain.QMemberConnectHist;
import toy.startbatch.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    @Override
    public List<Member> findByName(String name) {
        return em.createQuery(
                "select m " +
                        "from Member m " +
                        "where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

    }

    @Override
    public void save(Member member) {
        em.persist(member);
    }

    @Override
    public List<MemberConnectHist> findTargetOfDormantSmsRecepMember() {
        LocalDate nowDate = LocalDate.now();
        LocalDate beforeDate = nowDate.minusMonths(11);

        return jpaQueryFactory
                .select(Projections.fields(MemberConnectHist.class,
                                QMemberConnectHist.memberConnectHist.memberId,
                                QMemberConnectHist.memberConnectHist.connectDt.max().as("connectDt")
                        )
                )
                .from(QMemberConnectHist.memberConnectHist)
                .groupBy(QMemberConnectHist.memberConnectHist.memberId)
                .having(QMemberConnectHist.memberConnectHist.connectDt.max().loe(beforeDate.atStartOfDay()))
                .fetch();
    }

    @Override
    public List<Member> findTargetOfConvertForDormantMember() {
        return jpaQueryFactory
                .selectFrom(QMember.member)
                .fetch();
    }

    @Override
    public void convertMemberState() {

    }
    @Override
    public MemberConnectHist findOneMemberConnectHist(Long connectId) {
        return em.find(MemberConnectHist.class, connectId);
    }
}
