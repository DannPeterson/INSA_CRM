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
@Table(name = "insurer")
public class Insurer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @OneToOne
    private Firm firm;
    private String name;
    private String invoicePrefix;
    private String comment;
    private String url;
    private String username;
    private String password;

    public Insurer(Firm firm, String name, String invoicePrefix, String comment) {
        this.firm = firm;
        this.name = name;
        this.invoicePrefix = invoicePrefix;
        this.comment = comment;
    }
}