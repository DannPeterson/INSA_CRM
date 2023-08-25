package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.PolicyType;
import ee.insa.crmapp.service.PolicyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"api/policy-type"})
public class PolicyTypeResource {
    private PolicyTypeService policyTypeService;

    @Autowired
    public PolicyTypeResource(PolicyTypeService policyTypeService){
        this.policyTypeService = policyTypeService;
    }

    @GetMapping("/all")
    public List<PolicyType> findAll(){
        return policyTypeService.findAll();
    }
}
