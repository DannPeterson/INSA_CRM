package ee.insa.crmapp.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "insurer_type_profit_percent")
public class InsurerTypeProfitPercent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    private Insurer insurer;
    @OneToOne
    private PolicyType policyType;
    @Column(name = "percent", nullable = false, precision=10, scale=2)
    private BigDecimal percent;
}