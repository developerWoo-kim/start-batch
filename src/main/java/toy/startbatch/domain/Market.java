package toy.startbatch.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter @Setter
@Entity
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private int price;

    @Override
    public String toString() {
        return "Market{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", price=" + price +
                '}';
    }
}
