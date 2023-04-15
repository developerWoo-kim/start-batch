package toy.startbatch.member.repository;

import toy.startbatch.member.domain.Member;
import toy.startbatch.member.domain.MemberConnectHist;

import java.util.List;

public interface MemberRepository {


    /**
     * 단일 회원 조회
     * @param id
     * @return
     */
    public Member findOne(Long id);

    /**
     * 회원 이름으로 조회
     * @param name
     * @return
     */
    public List<Member> findByName(String name);

    /**
     * 회원 가입
     * @param member
     * @return
     */
    public void save(Member member);


    /**
     * 휴면전환 문자발송 대상 조회
     * @return
     */
    public List<MemberConnectHist> findTargetOfDormantSmsRecepMember();

    /**
     * 휴면전환 대상 조회
     * @return
     */
    public List<Member> findTargetOfConvertForDormantMember();

    /**
     * 휴면 전환 처리
     */
    public void convertMemberState();

    /**
     * 접속 이력 데이터 조회
     */
    public MemberConnectHist findOneMemberConnectHist(Long connectId);

}
