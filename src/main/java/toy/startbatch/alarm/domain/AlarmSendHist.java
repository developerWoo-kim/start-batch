package toy.startbatch.alarm.domain;


import lombok.*;
import toy.startbatch.member.domain.MemberConnectHist;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;


@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AlarmSendHist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sendType;
    private LocalDate sendDt;
    private String successAt;
    private Long memberId;

    @Builder
    public AlarmSendHist(String sendType, LocalDate sendDt, String successAt, Long memberId){
        this.sendType = sendType;
        this.sendDt = sendDt;
        this.successAt = successAt;
        this.memberId = memberId;
    }
}
