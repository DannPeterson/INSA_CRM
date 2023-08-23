package ee.insa.crmapp.repository;

import ee.insa.crmapp.model.Insurer;
import ee.insa.crmapp.model.InsurerTypeProfitPercent;
import ee.insa.crmapp.model.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsurerTypeProfitPercentRepository extends JpaRepository<InsurerTypeProfitPercent, Long> {
    InsurerTypeProfitPercent findByPolicyTypeAndInsurer(PolicyType policyType, Insurer insurer);
}
