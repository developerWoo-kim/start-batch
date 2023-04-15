package toy.startbatch.alarm.repository;

import toy.startbatch.alarm.domain.AlarmSendHist;

import java.util.List;

public interface AlarmRepository {

    public List<AlarmSendHist> findAll();
}
