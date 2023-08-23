package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Insurer;
import ee.insa.crmapp.model.InsurerTypeProfitPercent;
import ee.insa.crmapp.model.PolicyType;
import ee.insa.crmapp.repository.InsurerTypeProfitPercentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurerTypeProfitPercentService {
    private final InsurerTypeProfitPercentRepository insurerTypeProfitPercentRepository;

    @Autowired
    public InsurerTypeProfitPercentService(InsurerTypeProfitPercentRepository insurerTypeProfitPercentRepository) {
        this.insurerTypeProfitPercentRepository = insurerTypeProfitPercentRepository;
    }

    public InsurerTypeProfitPercent save(InsurerTypeProfitPercent insurerTypeProfitPercent) {
        return insurerTypeProfitPercentRepository.save(insurerTypeProfitPercent);
    }

    public List<InsurerTypeProfitPercent> findAll(){
        return insurerTypeProfitPercentRepository.findAll();
    }

    public InsurerTypeProfitPercent findByPolicyTypeAndInsurer(PolicyType policyType, Insurer insurer) {
        return insurerTypeProfitPercentRepository.findByPolicyTypeAndInsurer(policyType, insurer);
    }
}
