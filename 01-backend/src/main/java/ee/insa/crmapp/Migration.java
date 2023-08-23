package ee.insa.crmapp;

import ee.insa.crmapp.model.*;
import ee.insa.crmapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class Migration {
    private AgentService agentService;
    private BankService bankService;
    private ClientService clientService;
    private FirmService firmService;
    private InsurerService insurerService;
    private InvoiceService invoiceService;
    private PaymentTypeService paymentTypeService;
    private PolicyTypeService policyTypeService;
    private PolicyPartService policyPartService;
    private PolicyService policyService;
    private final String SQLITE_URL = "jdbc:sqlite:C:\\Users\\danii\\Desktop\\INSA_CRM\\01-backend\\insaDB.db";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private List<String> errors = new ArrayList<>();

    @Autowired
    public Migration(AgentService agentService,
                     BankService bankService,
                     ClientService clientService,
                     FirmService firmService,
                     InsurerService insurerService,
                     InvoiceService invoiceService,
                     PaymentTypeService paymentTypeService,
                     PolicyTypeService policyTypeService,
                     PolicyPartService policyPartService,
                     PolicyService policyService) {
        this.agentService = agentService;
        this.bankService = bankService;
        this.clientService = clientService;
        this.firmService = firmService;
        this.insurerService = insurerService;
        this.invoiceService = invoiceService;
        this.paymentTypeService = paymentTypeService;
        this.policyTypeService = policyTypeService;
        this.policyPartService = policyPartService;
        this.policyService = policyService;
    }

    public void migrate() throws SQLException {
        System.out.println("MIGRATION STARTED");
        getAgents();
        System.out.println("AGENTS DONE");
        getBanks();
        System.out.println("BANKS DONE");
        getClients();
        System.out.println("CLIENTS DONE");
        getFirms();
        System.out.println("FIRMS DONE");
        getInsurers();
        System.out.println("INSURERS DONE");
        getPaymentTypes();
        System.out.println("PAYMENT TYPES DONE");
        getPolicyTypes();
        System.out.println("POLICY TYPES DONE");
        getPolicies();
        System.out.println("POLICIES DONE");

        System.out.println("ERRORS: ");
        errors.forEach(System.out::println);

        System.out.println();
        System.out.println("Clients consolidation started");
        policyService.consolidateDuplicateClients();
        System.out.println("Clients consolidation finished");
    }

    private void getAgents() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM agents");
        ) {
            while (resultSet.next()) {
                Agent agent = new Agent();
                agent.setId(resultSet.getLong("_id"));
                agent.setPrefix(resultSet.getString("prefix"));
                agentService.save(agent);
            }
        }
    }

    private void getBanks() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM banks");
        ) {
            while (resultSet.next()) {
                Bank bank = new Bank();
                bank.setId(resultSet.getLong("_id"));
                bank.setName(resultSet.getString("name"));
                bank.setEmail(resultSet.getString("email"));
                bankService.save(bank);
            }
        }
    }

    private void getClients() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM clients");
        ) {
            while (resultSet.next()) {
                Client client = new Client();
                client.setId(resultSet.getLong("_id"));
                client.setName(resultSet.getString("name"));
                client.setCode(resultSet.getString("personal_code"));
                client.setAddress(resultSet.getString("address"));
                client.setPhone(resultSet.getString("phone"));
                client.setMobile1(resultSet.getString("mob_phone_1"));
                client.setMobile2(resultSet.getString("mob_phone_2"));
                client.setEmail1(resultSet.getString("email_1"));
                client.setEmail2(resultSet.getString("email_2"));
                client.setEmail3(resultSet.getString("email_3"));
                client.setRepresentative(resultSet.getString("represent"));

                Long bankId = resultSet.getLong("bank_id");
                if (bankId != 0) {
                    Bank bank = new Bank();
                    bank.setId(bankId);
                    client.setBank(bank);
                }

                client.setBankAccount(resultSet.getString("bank_account"));
                client.setComment(resultSet.getString("comment"));

                clientService.insertClientWithId(client);
            }
        }
    }

    private void getFirms() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM firms");
        ) {
            while (resultSet.next()) {
                Firm firm = new Firm();
                firm.setId(resultSet.getLong("_id"));
                firm.setName(resultSet.getString("name"));
                firm.setCode(resultSet.getString("code"));
                firm.setAddress(resultSet.getString("address"));
                firm.setPhone(resultSet.getString("phone"));
                firm.setFax(resultSet.getString("fax"));
                firm.setEmail(resultSet.getString("email"));

                Long bankId = resultSet.getLong("bank_id");
                if (bankId != 0) {
                    Bank bank = new Bank();
                    bank.setId(bankId);
                    firm.setBank(bank);
                }

                firm.setBankAccount(resultSet.getString("bank_account"));
                firm.setComment(resultSet.getString("comment"));

                firmService.save(firm);
            }
        }
    }

    private void getInsurers() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM insurers");
        ) {
            while (resultSet.next()) {
                Insurer insurer = new Insurer();
                insurer.setId(resultSet.getLong("_id"));
                insurer.setName(resultSet.getString("name"));
                insurer.setInvoicePrefix(resultSet.getString("invoice_pref"));
                insurer.setComment(resultSet.getString("comment"));

                Firm firm = new Firm();
                firm.setId(resultSet.getLong("firm_id"));

                insurer.setFirm(firm);

                insurerService.save(insurer);
            }
        }
    }

    private void getPaymentTypes() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM payments");
        ) {
            while (resultSet.next()) {
                PaymentType paymentType = new PaymentType();
                paymentType.setId(resultSet.getLong("_id"));
                paymentType.setPrefix(resultSet.getString("type"));

                switch (paymentType.getPrefix()){
                    case "SR":
                        paymentType.setPrefix("SR");
                        paymentType.setType("Sularaha");
                        break;
                    case "ÜL":
                        paymentType.setPrefix("ÜL");
                        paymentType.setType("Ülekanne");
                        break;
                    case "TR":
                        paymentType.setPrefix("TR");
                        paymentType.setType("Terminaal");
                        break;
                    case "OÜ":
                        paymentType.setPrefix("OÜ");
                        paymentType.setType("Otse ülekanne");
                        break;
                }

                paymentTypeService.save(paymentType);
            }
        }
    }

    private void getPolicyTypes() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM policy_type");
        ) {
            while (resultSet.next()) {
                PolicyType policyType = new PolicyType();
                policyType.setId(resultSet.getLong("_id"));
                policyType.setType(resultSet.getString("type"));

                policyTypeService.save(policyType);
            }
        }
    }



    private void getPolicies() throws SQLException {
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM policies");
        ) {
            while (resultSet.next()) {
                Policy policy = new Policy();
                policy.setId(resultSet.getLong("_id"));

                Client client = clientService.findById(resultSet.getLong("client_id"));
                if (client != null) {
                    policy.setClient(client);
                }

                Insurer insurer = insurerService.findById(resultSet.getLong("insurer_id"));
                if (insurer != null) {
                    policy.setInsurer(insurer);
                }

                policy.setPolicyNumber(resultSet.getString("number"));

                PolicyType policyType = policyTypeService.findById(resultSet.getLong("type_id"));
                if (policyType != null) {
                    policy.setPolicyType(policyType);
                }

                PaymentType paymentType = paymentTypeService.findById(resultSet.getLong("payment_id"));
                if(paymentType != null) {
                    policy.setPaymentType(paymentType);
                }

                String dateConl = resultSet.getString("date_concl");
                if (dateConl != null) {
                    policy.setConclusionDate(LocalDate.parse(dateConl, formatter));
                }

                String dateStart = resultSet.getString("date_start");
                if (dateStart != null) {
                    policy.setStartDate(LocalDate.parse(dateStart, formatter));
                }

                String dateEnd = resultSet.getString("date_end");
                if (dateEnd != null) {
                    policy.setEndDate(LocalDate.parse(dateEnd, formatter));
                }

                String dateRemind = resultSet.getString("date_reminder");
                if (dateRemind != null) {
                    policy.setReminderDate(LocalDate.parse(dateRemind, formatter));
                }
                if (resultSet.getInt("reminder") == 1) {
                    policy.setReminder(true);
                } else {
                    policy.setReminder(false);
                }

                String dateStop = resultSet.getString("date_stop");
                if (dateStop != null) {
                    policy.setStopDate(LocalDate.parse(dateStop, formatter));
                }

                policy.setSum(resultSet.getDouble("sum"));

                policy.setObject(resultSet.getString("object"));
                policy.setFolder(resultSet.getString("folder"));
                policy.setPercent(BigDecimal.valueOf(resultSet.getInt("percent")));

                Agent agent = agentService.findById(resultSet.getLong("agent_id"));
                if (agent != null) {
                    policy.setAgent(agent);
                }

                policy.setProvision(resultSet.getDouble("provision"));
                policy.setComment(resultSet.getString("comment"));
                policy.setParts(resultSet.getInt("parts"));

                // сохраняем полис
                policyService.insertPolicyWithId(policy);

                // сохраняем части, приложив к ним полис
                List<PolicyPart> policyParts = new ArrayList<>();
                for (int i = 1; i <= 12; i++) {
                    PolicyPart policyPart = getPart(resultSet, policy, i);

                    if (policyPart != null) {
                        policyPartService.save(policyPart);
                        System.out.println("Policy part saved: "  + policyPart.getId());

                        Long partInvoiceId = resultSet.getLong("part" + i + "_invoice_id");
                        if(partInvoiceId != 0) {
                            Invoice partInvoice = findInvoiceById(partInvoiceId);
                            partInvoice.setId(partInvoiceId);

                            // сохраняем инвойс, приложив к ним часть
                            try {
                                invoiceService.insertInvoiceWithId(partInvoice);
                                policyPart.setInvoice(partInvoice);
                                policyPartService.save(policyPart);
                            } catch (Exception e) {
                                errors.add(partInvoice.getInvoiceNumber());
                                System.out.println("Error saving invoice: " + partInvoice.getInvoiceNumber() + " " + partInvoice.getId());
                            }
                        }
                    }
                    policyParts.add(policyPart);
                }
            }
        }
    }

    private Invoice findInvoiceById(Long id) throws SQLException {
        Invoice invoice = new Invoice();
        try (
                Connection connection = DriverManager.getConnection(SQLITE_URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM invoices WHERE _id = " + id);
        ) {
            while (resultSet.next()) {
                invoice.setInvoiceNumber(resultSet.getString("number"));
                invoice.setText(resultSet.getString("body"));

                String conclusionDateString = resultSet.getString("date_concl");
                if (conclusionDateString != null) {
                    invoice.setConclusionDate(LocalDate.parse(conclusionDateString, formatter));
                }

                String maxDateString = resultSet.getString("date_max");
                if (maxDateString != null) {
                    invoice.setMaxDate(LocalDate.parse(maxDateString, formatter));
                }

                String paiDateString = resultSet.getString("date_payd");
                if (paiDateString != null) {
                    invoice.setPaidDate(LocalDate.parse(paiDateString, formatter));
                }
            }
        }
        return invoice;
    }

    private PolicyPart getPart(ResultSet resultSet, Policy policy, int part) throws SQLException {
        String part_date = resultSet.getString("part" + part + "_date");

        if (part_date != null) {
            PolicyPart policyPart = new PolicyPart();
            policyPart.setPolicy(policy);
            policyPart.setPart(part);
            policyPart.setDate(LocalDate.parse(part_date, formatter));

            String part_date_paid = resultSet.getString("part" + part + "_date_payd");
            if (part_date_paid != null) {
                policyPart.setDatePaid(LocalDate.parse(part_date_paid, formatter));
            }

            policyPart.setSum(resultSet.getDouble("part" + part + "_sum"));

            String part_date_confirmed = resultSet.getString("part" + part + "_date_confirmed");
            if(part_date_confirmed != null) {
                policyPart.setDateConfirmed(LocalDate.parse(part_date_confirmed, formatter));
            }

            if(resultSet.getInt("part" + part + "_reminder") == 1) {
                policyPart.setReminder(true);
            } else {
                policyPart.setReminder(false);
            }

            if(part == 1 && policy.getParts() == 1) {
                policyPart.setReminder(false);
            }

            policyPart.setSumReal(resultSet.getDouble("part" + part + "_sum_real"));
            return policyPart;
        }
        return null;
    }
}