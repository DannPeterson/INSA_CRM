package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Insurer;
import ee.insa.crmapp.model.Invoice;
import ee.insa.crmapp.model.Policy;
import ee.insa.crmapp.model.PolicyPart;
import ee.insa.crmapp.repository.PolicyPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PolicyPartService {
    private final PolicyPartRepository policyPartRepository;
    private final ExcelGeneratorService excelGeneratorService;

    @Autowired
    public PolicyPartService(PolicyPartRepository policyPartRepository, ExcelGeneratorService excelGeneratorService) {
        this.policyPartRepository = policyPartRepository;
        this.excelGeneratorService = excelGeneratorService;
    }

    public PolicyPart save(PolicyPart policyPart) {
        return policyPartRepository.save(policyPart);
    }

    public PolicyPart findById(Long id) {
        return policyPartRepository.findById(id).orElse(null);
    }

    public PolicyPart findByInvoice(Invoice invoice) {
        return policyPartRepository.findByInvoice(invoice);
    }

    public List<PolicyPart> findAllByPolicy(Policy policy) {
        return policyPartRepository.findAllByPolicy(policy);
    }

    public PolicyPart update(PolicyPart policyPart) {
        return policyPartRepository.save(policyPart);
    }

    public void generateReport(List<PolicyPart> parts) throws IOException {
        excelGeneratorService.generateExcelReport(parts);
    }

    public List<PolicyPart> findFirstPartsByPolicyIds(List<Long> policyIds) {
        System.out.println(policyIds);
        List<PolicyPart> result = policyPartRepository.findFirstPartsByPolicyIds(policyIds);
        return result;
    }

    @Transactional
    public void deleteExcessPolicyParts() {
        List<PolicyPart> excessParts = policyPartRepository.findExcessParts();
        policyPartRepository.deleteInBatch(excessParts);
    }

    @Transactional
    public void deleteExcessPolicyPartsForPolicy(Long policyId) {
        List<PolicyPart> excessParts = policyPartRepository.findExcessPartsForPolicy(policyId);
        if (!excessParts.isEmpty()) {
            policyPartRepository.deleteInBatch(excessParts);
        }
    }

    @Transactional
    public void insertPolicyPartWithId(PolicyPart policyPart) {
        policyPartRepository.insertPolicyPartWithId(
                policyPart.getId(),
                policyPart.getPolicy() != null ? policyPart.getPolicy().getId() : null,
                policyPart.getPart(),
                policyPart.getSum(),
                policyPart.getDate(),
                policyPart.getDatePaid(),
                policyPart.getDateConfirmed(),
                policyPart.getSumReal(),
                policyPart.isReminder()
        );
    }

    public List<PolicyPart> saveAll(List<PolicyPart> policyParts) {
        return policyPartRepository.saveAll(policyParts);
    }

    public Page<PolicyPart> findByReminderTrueAndDatePaidIsNullAndSumIsNotNullOrderByDateAsc(int pageIndex, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("date").ascending());
        return policyPartRepository.findByReminderTrueAndDatePaidIsNullAndSumIsNotNullOrderByDateAsc(pageRequest);
    }

    // AGENT REPORT
    public List<PolicyPart> findPolicyPartsByAgentAndDateBetween(Long agentId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByAgentAndDateBetween(agentId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByAgentAndDatePaidBetween(Long agentId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByAgentAndDatePaidBetween(agentId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByAgentAndDateBetweenAndDatePaidIsNull(Long agentId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByAgentAndDateBetweenAndDatePaidIsNull(agentId, start, end);
    }

    // INSURER REPORT
    public List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_All(Long insurerId, Long policyTypeId, Long paymentTypeId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_All(insurerId, policyTypeId, paymentTypeId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Paid(Long insurerId, Long policyTypeId, Long paymentTypeId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Paid(insurerId, policyTypeId, paymentTypeId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Debt(Long insurerId, Long policyTypeId, Long paymentTypeId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByInsurerAndTypeAndPayment_byPartDate_Debt(insurerId, policyTypeId, paymentTypeId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_All(Long insurerId, Long policyTypeId, Long paymentTypeId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_All(insurerId, policyTypeId, paymentTypeId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Paid(Long insurerId, Long policyTypeId, Long paymentTypeId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Paid(insurerId, policyTypeId, paymentTypeId, start, end);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Debt(Long insurerId, Long policyTypeId, Long paymentTypeId, LocalDate start, LocalDate end) {
        return policyPartRepository.findPolicyPartsByInsurerAndTypeAndPayment_byPolicyConclDate_Debt(insurerId, policyTypeId, paymentTypeId, start, end);
    }

    // INSURER CONTROL REPORT
    public List<PolicyPart> findPolicyPartsByInsurerAndDate_All(Long insurerId, LocalDate date, String policyNumber, String clientName, Long policyTypeId) {
        return policyPartRepository.findPolicyPartsByInsurerAndDate_All(insurerId, date, policyNumber, clientName, policyTypeId);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndDate_Paid(Long insurerId, LocalDate date, String policyNumber, String clientName, Long policyTypeId) {
        return policyPartRepository.findPolicyPartsByInsurerAndDate_Paid(insurerId, date, policyNumber, clientName, policyTypeId);
    }

    public List<PolicyPart> findPolicyPartsByInsurerAndDate_Debt(Long insurerId, LocalDate date, String policyNumber, String clientName, Long policyTypeId) {
        return policyPartRepository.findPolicyPartsByInsurerAndDate_Debt(insurerId, date, policyNumber, clientName, policyTypeId);
    }
}