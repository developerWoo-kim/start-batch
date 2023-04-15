package toy.startbatch.alarm.repository.implement;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toy.startbatch.alarm.domain.AlarmSendHist;
import toy.startbatch.alarm.domain.QAlarmSendHist;
import toy.startbatch.alarm.repository.AlarmRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepository {
    private final EntityManager em;

    @Override
    public List<AlarmSendHist> findAll() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        return queryFactory
                .selectFrom(QAlarmSendHist.alarmSendHist)
                .fetch();
    }
}
