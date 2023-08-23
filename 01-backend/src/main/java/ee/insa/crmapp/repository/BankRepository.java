package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
