package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Policy;
import ee.insa.crmapp.model.PolicyPart;
import ee.insa.crmapp.service.PolicyPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = {"/policy-part"})
public class PolicyPartResource {
    //AGENT
    public static final int AGENT_REPORT_BY_PART_DATE = 1;
    public static final int AGENT_REPORT_BY_PAID_DATE = 2;
    public static final int AGENT_REPORT_BY_DEBT = 3;

    //INSURER
    public static final int INSURER_REPORT_BY_PART_DATE_PAID = 1;
    public static final int INSURER_REPORT_BY_PART_DATE_UNPAID = 2;
    public static final int INSURER_REPORT_BY_PART_DATE_ALL = 3;

    public static final int INSURER_REPORT_BY_CONCLUSION_DATE_PAID = 4;
    public static final int INSURER_REPORT_BY_CONCLUSION_DATE_UNPAID = 5;
    public static final int INSURER_REPORT_BY_CONCLUSION_DATE_ALL = 6;

    //INSURER CONTROL
    public static final int INSURER_CONTROL_REPORT_BY_PART_DATE_PAID = 1;
    public static final int INSURER_CONTROL_REPORT_BY_PART_DATE_UNPAID = 2;
    public static final int INSURER_CONTROL_REPORT_BY_PART_DATE_ALL = 3;

    private PolicyPartService policyPartService;

    @Autowired
    public PolicyPartResource(PolicyPartService policyPartService) {
        this.policyPartService = policyPartService;
    }

    @GetMapping("/{policyId}")
    public List<PolicyPart> findAllByPolicyId(@PathVariable("policyId") Long policyId) {
        Policy policy = new Policy();
        policy.setId(policyId);
        return policyPartService.findAllByPolicy(policy);
    }

    @PostMapping("/save-list")
    public List<PolicyPart> saveAll(@RequestBody List<PolicyPart> policyParts) {
        return policyPartService.saveAll(policyParts);
    }

    @PostMapping("/update/{policyPartId}")
    public PolicyPart updatePolicyPart(@PathVariable("policyPartId") Long policyPartId, @RequestBody PolicyPart policyPart) {
        return policyPartService.update(policyPart);
    }

    @GetMapping("/reminder")
    public Page<PolicyPart> findByReminderTrueAndDatePaidIsNullOrderByDateAsc(
            @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "100") int pageSize
    ) {
        return policyPartService.findByReminderTrueAndDatePaidIsNullAndSumIsNotNullOrderByDateAsc(pageIndex, pageSize);
    }

    @GetMapping("/agent/{agentId}")
    public List<PolicyPart> findPolicyPartsForAgentReport(
            @PathVariable("agentId") Long agentId,
            @RequestParam(value = "start") String startUTC,
            @RequestParam(value = "end") String endUTC,
            @RequestParam(value = "payment") int payment
    ) {
        LocalDate start = LocalDate.parse(startUTC, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endUTC, DateTimeFormatter.ISO_DATE);

        switch (payment) {
            case AGENT_REPORT_BY_PART_DATE:
                return policyPartService.findPolicyPartsByAgentAndDateBetween(agentId, start, end);
            case AGENT_REPORT_BY_PAID_DATE:
                return policyPartService.findPolicyPartsByAgentAndDatePaidBetween(agentId, start, end);
            case AGENT_REPORT_BY_DEBT:
                return policyPartService.findPolicyPartsByAgentAndDateBetweenAndDatePaidIsNull(agentId, start, end);
            default:
                throw new IllegalArgumentException("Invalid payment type: " + payment);
        }
    }

    @GetMapping("/insurer/{insurerId}")
    public List<PolicyPart> findPolicyPartsForInsurerReport(
            @PathVariable("insurerId") Long insurerId,
            @RequestParam(value = "policyTypeId", required = false) Long policyTypeId,
            @RequestParam(value = "paymentTypeId", required = false) Long paymentTypeId,
            @RequestParam(value = "start") String startUTC,
            @RequestParam(value = "end") String endUTC,
            @RequestParam(value = "type") int type
    ) {
        LocalDate start = LocalDate.parse(startUTC, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endUTC, DateTimeFormatter.ISO_DATE);

        switch (type) {
            case INSURER_REPORT_BY_PART_DATE_PAID:
                return policyPartService.findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Paid(insurerId, policyTypeId, paymentTypeId, start, end);
            case INSURER_REPORT_BY_PART_DATE_UNPAID:
                return policyPartService.findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Debt(insurerId, policyTypeId, paymentTypeId, start, end);
            case INSURER_REPORT_BY_PART_DATE_ALL:
                return policyPartService.findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_All(insurerId, policyTypeId, paymentTypeId, start, end);
            case INSURER_REPORT_BY_CONCLUSION_DATE_PAID:
                return policyPartService.findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Paid(insurerId, policyTypeId, paymentTypeId, start, end);
            case INSURER_REPORT_BY_CONCLUSION_DATE_UNPAID:
                return policyPartService.findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Debt(insurerId, policyTypeId, paymentTypeId, start, end);
            case INSURER_REPORT_BY_CONCLUSION_DATE_ALL:
                return policyPartService.findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_All(insurerId, policyTypeId, paymentTypeId, start, end);
            default:
                throw new IllegalArgumentException("Invalid insurer request type: " + type);
        }
    }

    @GetMapping("/insurer-control/{insurerId}")
    List<PolicyPart> findPolicyPartsForInsurerControlReport(
            @PathVariable("insurerId") Long insurerId,
            @RequestParam(value = "policyTypeId", required = false) Long policyTypeId,
            @RequestParam(value = "start") String dateUtc,
            @RequestParam(value = "client", required = false) String client,
            @RequestParam(value = "policy", required = false) String policy,
            @RequestParam(value = "type") int type
    ) {
        LocalDate date = LocalDate.parse(dateUtc, DateTimeFormatter.ISO_DATE);

        switch (type){
            case INSURER_CONTROL_REPORT_BY_PART_DATE_PAID:
                return policyPartService.findPolicyPartsByInsurerAndDate_Paid(insurerId, date, client, policy, policyTypeId);
            case INSURER_CONTROL_REPORT_BY_PART_DATE_UNPAID:
                return policyPartService.findPolicyPartsByInsurerAndDate_Debt(insurerId, date, client, policy, policyTypeId);
            case INSURER_CONTROL_REPORT_BY_PART_DATE_ALL:
                return policyPartService.findPolicyPartsByInsurerAndDate_All(insurerId, date, client, policy, policyTypeId);
            default:
                throw new IllegalArgumentException("Invalid insurer control request type: " + type);
        }
    }

    @PostMapping("/report")
    public void generateReport(@RequestBody List<PolicyPart> parts) throws IOException {
        policyPartService.generateReport(parts);
    }

    @PostMapping("/first-parts")
    public List<PolicyPart> findFirstPartsByPolicyIds(@RequestBody List<Long> policyIds) {
        return policyPartService.findFirstPartsByPolicyIds(policyIds);
    }

    @GetMapping("/debts")
    public List<PolicyPart> findAllDebts() {
        List<PolicyPart> result = policyPartService.findAllDebts();
        return policyPartService.findAllDebts();
    }
}