package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Policy;
import ee.insa.crmapp.model.PolicyPart;
import ee.insa.crmapp.service.PolicyPartService;
import ee.insa.crmapp.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = {"/policy"})
public class PolicyResource {
    private PolicyService policyService;
    private PolicyPartService policyPartService;

    @Autowired
    public PolicyResource(PolicyService policyService,
                          PolicyPartService policyPartService) {
        this.policyService = policyService;
        this.policyPartService = policyPartService;
    }

    @GetMapping()
    public Page<Policy> getPolicies(@RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "100") int size) {
        return policyService.getLastPolicies(page, size);
    }

    @PostMapping("/update/{policyId}")
    public Policy updatePolicy(@PathVariable("policyId") Long policyId, @RequestBody Policy policy) {
        Policy updatedPolicy = policyService.update(policy);
        policyPartService.deleteExcessPolicyPartsForPolicy(policyId);
        return updatedPolicy;
    }

    @PutMapping("/save")
    public Policy savePolicy(@RequestBody Policy policy) {
        Policy savedPolicy = policyService.save(policy);
        policyPartService.deleteExcessPolicyPartsForPolicy(savedPolicy.getId());
        return savedPolicy;
    }

    @GetMapping("/search")
    public Page<Policy> searchPolicies(
            @RequestParam(name = "policyTypeId", required = false) Long policyTypeId,
            @RequestParam(name = "paymentTypeId", required = false) Long paymentTypeId,
            @RequestParam(name = "clientName", required = false) String clientName,
            @RequestParam(name = "object", required = false) String object,
            @RequestParam(name = "invoiceNumber", required = false) String invoiceNumber,
            @RequestParam(name = "policyNumber", required = false) String policyNumber,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "100") int size,
            @RequestParam(name = "current") boolean current) {
        Page<Policy> result = policyService.searchPolicies(policyTypeId, paymentTypeId, clientName, object, invoiceNumber, policyNumber, current, page, size);
        return result;
    }

    @GetMapping("/search/reminder")
    public Page<Policy> searchPoliciesForReminder(
            @RequestParam(name = "policyTypeId", required = false) Long policyTypeId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "100") int size) {
        return policyService.searchReminderPolicies(policyTypeId, page, size);
    }

    @GetMapping("/deletionReport/{policyId}")
    public ResponseEntity<List<String>> getDeletionReport(@PathVariable("policyId") Long policyId) {
        List<String> report = policyService.getDeletionReport(policyId);
        if (report != null) {
            return ResponseEntity.ok(report);  // возвращает HTTP 200 с телом ответа, содержащим отчет
        } else {
            return ResponseEntity.notFound().build();  // возвращает HTTP 404 без тела ответа
        }
    }

    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable("id") Long id) {
        return policyService.getById(id);
    }

    @DeleteMapping("/delete/{policyId}")
    public ResponseEntity<Map<String, String>> deletePolicy(@PathVariable("policyId") Long policyId) {
        Map<String, String> response = new HashMap<>();
        if (policyService.deleteById(policyId)) {
            response.put("message", "Policy deleted successfully");
            return ResponseEntity.ok(response);  // возвращает HTTP 200 с телом ответа, содержащим сообщение
        } else {
            response.put("message", "Policy not found");
            return ResponseEntity.notFound().build();  // возвращает HTTP 404 без тела ответа
        }
    }

    @PutMapping("/clipboard")
    public Policy getPolicyFromClipboard(@RequestBody String clipboard) {
        return policyService.getPolicyFromClipboard(clipboard);
    }
}
