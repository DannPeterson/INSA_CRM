package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Firm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmRepository extends JpaRepository<Firm, Long> {
}
