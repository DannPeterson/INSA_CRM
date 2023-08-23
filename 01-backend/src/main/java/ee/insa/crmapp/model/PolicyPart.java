package ee.insa.crmapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "policy_part")
public class PolicyPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    private Policy policy;
    @OneToOne
    private Invoice invoice;
    private Integer part;
    private Double sum;
    private LocalDate date;
    private LocalDate datePaid;
    private LocalDate dateConfirmed;
    private Double sumReal;
    private boolean reminder;
}