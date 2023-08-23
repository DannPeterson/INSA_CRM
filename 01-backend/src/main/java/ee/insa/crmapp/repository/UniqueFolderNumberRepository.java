package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.UniqueFolderNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface UniqueFolderNumberRepository extends JpaRepository<UniqueFolderNumber, Long> {
}
