package ee.insa.crmapp.service;

import ee.insa.crmapp.model.*;
import ee.insa.crmapp.repository.InvoiceRepository;
import ee.insa.crmapp.repository.PolicyPartRepository;
import ee.insa.crmapp.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final UniqueFolderNumberService uniqueFolderNumberService;
    private final ClipboardPolicyService clipboardPolicyService;
    private final PolicyPartRepository policyPartRepository;
    private final InsurerTypeProfitPercentService insurerTypeProfitPercentService;
    private final ClientService clientService;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public PolicyService(PolicyRepository policyRepository,
                         UniqueFolderNumberService uniqueFolderNumberService,
                         ClipboardPolicyService clipboardPolicyService,
                         PolicyPartRepository policyPartRepository,
                         InsurerTypeProfitPercentService insurerTypeProfitPercentService,
                         ClientService clientService,
                         InvoiceRepository invoiceRepository) {
        this.policyRepository = policyRepository;
        this.uniqueFolderNumberService = uniqueFolderNumberService;
        this.clipboardPolicyService = clipboardPolicyService;
        this.policyPartRepository = policyPartRepository;
        this.insurerTypeProfitPercentService = insurerTypeProfitPercentService;
        this.clientService = clientService;
        this.invoiceRepository = invoiceRepository;
    }

    public Policy save(Policy policy) {
        policy.setFolder(uniqueFolderNumberService.getFolderNumber());
        return policyRepository.save(policy);
    }

    public List<Policy> findAll() {
        return policyRepository.findAll();
    }

    public Policy findById(Long id) {
        return policyRepository.findById(id).orElse(null);
    }

    public Page<Policy> findLastPolicies(Pageable pageable) {
        return policyRepository.findLastPolicies(pageable);
    }

    public long count() {
        return policyRepository.countPolicies();
    }

    public Policy update(Policy policy) {
        return policyRepository.save(policy);
    }

    public Page<Policy> getLastPolicies(int pageIndex, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("conclusionDate").descending());
        Page<Policy> lastPoliciesPage = policyRepository.findByOrderByIdDesc(pageRequest);

        return reversePoliciesOrder(lastPoliciesPage, pageRequest);
    }

    public Page<Policy> searchPolicies(Long policyTypeId, Long paymentTypeId, String clientName, String object, String invoiceNumber, String policyNumber, boolean current, int pageIndex, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        Page<Policy> policiesPage = policyRepository.findPoliciesBySearchCriteria(policyTypeId, paymentTypeId, clientName, object, invoiceNumber, policyNumber, current, pageRequest);
        return reversePoliciesOrder(policiesPage, pageRequest);
    }

    public Page<Policy> searchReminderPolicies(Long policyTypeId, int pageIndex, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        return policyRepository.searchReminderPolicies(policyTypeId, LocalDate.now(), pageRequest);
    }

    private Page<Policy> reversePoliciesOrder(Page<Policy> policiesPage, PageRequest pageRequest) {
        // Reverse the order of the policies within the fetched page
        List<Policy> policies = new ArrayList<>(policiesPage.getContent());
        Collections.reverse(policies);

        // Create a new Page object with the reversed order of policies
        return new PageImpl<>(policies, pageRequest, policiesPage.getTotalElements());
    }

    @Transactional
    public void insertPolicyWithId(Policy policy) {
        policyRepository.insertPolicyWithId(
                policy.getId(),
                policy.getClient() != null ? policy.getClient().getId() : null,
                policy.getInsurer() != null ? policy.getInsurer().getId() : null,
                policy.getPolicyType() != null ? policy.getPolicyType().getId() : null,
                policy.getPaymentType() != null ? policy.getPaymentType().getId() : null,
                policy.getAgent() != null ? policy.getAgent().getId() : null,
                policy.getUser() != null ? policy.getUser().getId() : null,
                policy.getConclusionDate(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStopDate(),
                policy.getReminderDate(),
                policy.getReminder(),
                policy.getObject(),
                policy.getFolder(),
                policy.getPolicyNumber(),
                policy.getSum(),
                policy.getPercent(),
                policy.getProvision(),
                policy.getComment(),
                policy.getParts()
        );
    }

    public Policy getById(Long id) {
        return policyRepository.findById(id).orElse(null);
    }

    public Policy getPolicyFromClipboard(String clipboard) {
        Policy policy = clipboardPolicyService.getPolicy(clipboard);
        policy.setFolder(uniqueFolderNumberService.getFolderNumber());
        policy.setReminderDate(policy.getEndDate().minusMonths(1));
        if (policy.getInsurer() != null && policy.getPolicyType() != null) {
            InsurerTypeProfitPercent insurerTypeProfitPercent = insurerTypeProfitPercentService.findByPolicyTypeAndInsurer(policy.getPolicyType(), policy.getInsurer());
            if (insurerTypeProfitPercent != null) {
                policy.setPercent(insurerTypeProfitPercent.getPercent());
            }
        }

        BigDecimal bd = new BigDecimal(Double.toString(policy.getSum()));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double sumTotal = bd.doubleValue();
        policy.setSum(sumTotal);
        if(policy.getClient().getId() == null) {
            policy.setClient(clientService.save(policy.getClient()));
        }

        policyRepository.save(policy);

        int duration = 12 / policy.getParts();
        LocalDate startDate = policy.getStartDate();
        Double sumPerPart = policy.getSum() / policy.getParts();
        for (int i = 0; i < policy.getParts(); i++) {
            PolicyPart part = new PolicyPart();
            part.setPolicy(policy);
            part.setPart(i + 1);
            part.setSum(sumPerPart);
            part.setDate(startDate.plusMonths(duration * i));
            part.setReminder((i + 1) != 1);
            policyPartRepository.save(part);
        }

        return policy;
    }

    public void updateClient(Long policyId, Long clientId) {
        policyRepository.updateClient(policyId, clientId);
    }

    @Transactional
    public void consolidateDuplicateClients() {
        // Находим все дублирующиеся коды клиентов
        List<String> duplicateCodes = clientService.findDuplicateCodes();

        for (String code : duplicateCodes) {
            // Находим всех клиентов с данным дублирующим кодом
            List<Client> clients = clientService.findAllByCode(code);

            // Берем первого клиента из списка для консолидации полисов
            Client mainClient = clients.get(0);

            // Обновляем все полисы, связанные с остальными клиентами, чтобы они были связаны с первым клиентом
            for (int i = 1; i < clients.size(); i++) {
                policyRepository.updateClient(clients.get(i).getId(), mainClient.getId());
            }

            // Удаляем остальных клиентов
            for (int i = 1; i < clients.size(); i++) {
                clientService.deleteById(clients.get(i).getId());
            }
        }
    }

    public List<String> getDeletionReport(Long policyId) {
        Policy policy = policyRepository.findById(policyId).orElse(null);
        List<PolicyPart> parts = policyPartRepository.findAllByPolicy(policy);
        List<Invoice> invoices = parts.stream().filter(part -> part.getInvoice() != null).map(PolicyPart::getInvoice).collect(Collectors.toList());

        List<String> report = new ArrayList<>();
        report.add("Полис " + policy.getPolicyNumber());
        report.add("Клиент: " + policy.getClient().getName());
        if (invoices.size() > 0) {
            report.add("Полис содержит счета, которые также будут удалены:");
            for (Invoice invoice : invoices) {
                report.add(invoice.getInvoiceNumber());
            }
        }
        return report;
    }

    public boolean deleteById(Long policyId) {
        Policy policy = policyRepository.findById(policyId).orElse(null);
        List<PolicyPart> parts = policyPartRepository.findAllByPolicy(policy);
        List<Invoice> invoices = parts.stream().filter(part -> part.getInvoice() != null).map(PolicyPart::getInvoice).collect(Collectors.toList());
        policyPartRepository.deleteAll(parts);

        for (Invoice invoice : invoices) {
            invoiceRepository.deleteById(invoice.getId());
        }

        policyRepository.deleteById(policyId);

        return true;
    }
}