package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
    PaymentType findByPrefix(String prefix);
}
