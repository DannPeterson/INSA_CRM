package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyTypeRepository extends JpaRepository<PolicyType, Long> {
    PolicyType findByType(String type);
}
