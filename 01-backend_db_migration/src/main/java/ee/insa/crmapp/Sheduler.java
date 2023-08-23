package ee.insa.crmapp;

import ee.insa.crmapp.model.*;
import ee.insa.crmapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class Sheduler {
    private UniqueFolderNumberService uniqueFolderNumberService;
    private BankService bankService;
    private ClientService clientService;
    private FirmService firmService;
    private InsurerService insurerService;
    private InvoiceService invoiceService;
    private UserService userService;
    private AgentService agentService;
    private PaymentTypeService paymentTypeService;
    private PolicyTypeService policyTypeService;
    private PolicyService policyService;
    private PolicyPartService policyPartService;
    private InsurerTypeProfitPercentService insurerTypeProfitPercentService;
    private MailService mailService;

    @Autowired
    public Sheduler(UniqueFolderNumberService uniqueFolderNumberService,
                    BankService bankService,
                    ClientService clientService,
                    FirmService firmService,
                    InsurerService insurerService,
                    InvoiceService invoiceService,
                    UserService userService,
                    AgentService agentService,
                    PolicyTypeService policyTypeService,
                    PolicyService policyService,
                    PaymentTypeService paymentTypeService,
                    PolicyPartService policyPartService,
                    MailService mailService,
                    InsurerTypeProfitPercentService insurerTypeProfitPercentService) {

        this.uniqueFolderNumberService = uniqueFolderNumberService;
        this.bankService = bankService;
        this.clientService = clientService;
        this.firmService = firmService;
        this.insurerService = insurerService;
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.agentService = agentService;
        this.policyTypeService = policyTypeService;
        this.policyService = policyService;
        this.paymentTypeService = paymentTypeService;
        this.policyPartService = policyPartService;
        this.mailService = mailService;
        this.insurerTypeProfitPercentService = insurerTypeProfitPercentService;
    }

    @Scheduled(fixedRate = 500_000_000)
    public void migrate() throws SQLException {
        Migration migration = new Migration(
                agentService,
                bankService,
                clientService,
                firmService,
                insurerService,
                invoiceService,
                paymentTypeService,
                policyTypeService,
                policyPartService,
                policyService,
                insurerTypeProfitPercentService
        );
        migration.migrate();
    }
}