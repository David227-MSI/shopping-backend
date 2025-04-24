package tw.eeits.unhappy.gy.domain;
// TODO: 這是 stub 暫時版本，等 teammate 上傳正式版本後刪除

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(precision = 10, scale = 2) // 小數位數取2
    private BigDecimal unitPrice;

}
