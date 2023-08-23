package ee.insa.crmapp.service;

import ee.insa.crmapp.model.PolicyType;
import ee.insa.crmapp.repository.PolicyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyTypeService {
    private final PolicyTypeRepository policyTypeRepository;

    @Autowired
    public PolicyTypeService(PolicyTypeRepository policyTypeRepository) {
        this.policyTypeRepository = policyTypeRepository;
    }

    public PolicyType save(PolicyType policyType) {
        return policyTypeRepository.save(policyType);
    }

    public PolicyType findById(Long id) {
        return policyTypeRepository.findById(id).orElse(null);
    }

    public PolicyType findByType(String name) {
        return policyTypeRepository.findByType(name);
    }

    public List<PolicyType> findAll(){
        return policyTypeRepository.findAll();
    }
}
