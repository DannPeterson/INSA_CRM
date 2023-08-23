package ee.insa.crmapp.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "unique_folder_number")
public class UniqueFolderNumber {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDate date;
    private int lastUsedNumber;
}
