package tw.eeits.unhappy.eee.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_level")
@Getter
@Setter
@ToString
public class UserLevelBean {
    @Id
    @Column(name = "id")
    private Integer levelId;  
    
    @Column(name = "level_name")
    private String levelName;  
    
    @Column(name = "min_spent")
    private Integer minSpent;  
    
    @Column(name = "discount_rate", precision = 5, scale = 2) 
    private BigDecimal discountRate; 
    
    @Column(name = "updated_at")
    private java.util.Date updatedAt; 
}
