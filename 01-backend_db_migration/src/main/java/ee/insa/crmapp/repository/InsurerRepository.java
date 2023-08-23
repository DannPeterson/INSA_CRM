package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Insurer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsurerRepository extends JpaRepository<Insurer, Long> {
    Insurer findByName(String name);
}
