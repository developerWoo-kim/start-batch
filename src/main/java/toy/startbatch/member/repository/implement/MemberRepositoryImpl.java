package toy.startbatch.member.repository.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.startbatch.member.domain.Member;
import toy.startbatch.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final EntityManager em;

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
    public List<Member> findTargetOfDormantSmsRecepMember() {
        return null;
    }

    @Override
    public List<Member> findTargetOfConvertForDormantMember() {
        return null;
    }

    @Override
    public void convertMemberState() {

    }
}
