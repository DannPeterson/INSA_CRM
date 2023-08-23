package ee.insa.crmapp.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "firm")
public class Firm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private String fax;
    private String email;
    @OneToOne
    private Bank bank;
    private String bankAccount;
    private String comment;

    public Firm(String name, String code, String address, String phone, String fax, String email, Bank bank, String bankAccount, String comment) {
        this.name = name;
        this.code = code;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.bank = bank;
        this.bankAccount = bankAccount;
        this.comment = comment;
    }
}
