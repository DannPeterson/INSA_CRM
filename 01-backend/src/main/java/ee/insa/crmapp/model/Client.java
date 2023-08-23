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
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String address;
    private String code;
    private String phone;
    private String mobile1;
    private String mobile2;
    private String email1;
    private String email2;
    private String email3;
    private String representative;
    @OneToOne
    private Bank bank;
    private String bankAccount;
    private String comment;

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", code='" + code + '\'' +
                ", phone='" + phone + '\'' +
                ", mobile1='" + mobile1 + '\'' +
                ", mobile2='" + mobile2 + '\'' +
                ", email1='" + email1 + '\'' +
                ", email2='" + email2 + '\'' +
                ", email3='" + email3 + '\'' +
                ", representative='" + representative + '\'' +
                ", bank=" + bank +
                ", bankAccount='" + bankAccount + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
