package toy.startbatch.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@Entity
public class MemberConnectHist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long connectId;
    private String connectType;
    private long memberId;
    private LocalDateTime connectDate;
}
