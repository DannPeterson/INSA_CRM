package ee.insa.crmapp.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "policy")
public class Policy {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Client client;
    @OneToOne
    private Insurer insurer;
    @OneToOne
    private PolicyType policyType;
    @OneToOne
    private PaymentType paymentType;
    @OneToOne
    private Agent agent;
    @OneToOne
    private User user;
    private LocalDate conclusionDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate stopDate;
    private LocalDate reminderDate;
    private Boolean reminder;
    private String object;
    private String folder;
    private String policyNumber;
    private Double sum;
    private BigDecimal percent;
    private Double provision;
    @Column(length = 1000 )
    private String comment;
    private Integer parts;

    public Policy(Client client, Insurer insurer, PolicyType policyType, PaymentType paymentType, Agent agent, User user, LocalDate conclusionDate, LocalDate startDate, LocalDate endDate, LocalDate stopDate, LocalDate reminderDate, Boolean reminder, String object, String folder, String policyNumber, Double sum, BigDecimal percent, Double provision, String comment, Integer parts) {
        this.client = client;
        this.insurer = insurer;
        this.policyType = policyType;
        this.paymentType = paymentType;
        this.agent = agent;
        this.user = user;
        this.conclusionDate = conclusionDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stopDate = stopDate;
        this.reminderDate = reminderDate;
        this.reminder = reminder;
        this.object = object;
        this.folder = folder;
        this.policyNumber = policyNumber;
        this.sum = sum;
        this.percent = percent;
        this.provision = provision;
        this.comment = comment;
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", client=" + client +
                ", insurer=" + insurer +
                ", policyType=" + policyType +
                ", paymentType=" + paymentType +
                ", agent=" + agent +
                ", user=" + user +
                ", conclusionDate=" + conclusionDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", stopDate=" + stopDate +
                ", reminderDate=" + reminderDate +
                ", reminder=" + reminder +
                ", object='" + object + '\'' +
                ", folder='" + folder + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                ", sum=" + sum +
                ", percent=" + percent +
                ", provision=" + provision +
                ", comment='" + comment + '\'' +
                ", parts=" + parts +
                '}';
    }
}
