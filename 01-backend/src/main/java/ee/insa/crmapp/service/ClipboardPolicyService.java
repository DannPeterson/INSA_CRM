package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Client;
import ee.insa.crmapp.model.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ClipboardPolicyService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final InsurerService insurerService;
    private final PolicyTypeService policyTypeService;
    private final PaymentTypeService paymentTypeService;
    private final AgentService agentService;
    private final ClientService clientService;
    private final InsurerTypeProfitPercentService insurerTypeProfitPercentService;

    @Autowired
    public ClipboardPolicyService(InsurerService insurerService,
                                  PolicyTypeService policyTypeService,
                                  PaymentTypeService paymentTypeService,
                                  AgentService agentService,
                                  ClientService clientService,
                                  InsurerTypeProfitPercentService insurerTypeProfitPercentService) {
        this.insurerService = insurerService;
        this.policyTypeService = policyTypeService;
        this.paymentTypeService = paymentTypeService;
        this.agentService = agentService;
        this.clientService = clientService;
        this.insurerTypeProfitPercentService = insurerTypeProfitPercentService;
    }

    public Policy getPolicy(String clipboard) {
//        System.out.println(clipboard);
        Policy policy = new Policy();

        String policyString = clipboard;
        String[] policyStringArray = clipboard.split("\\r?\\n");

        if (policyString.contains("PZU") && policyString.contains("Liikluskindlustuse poliis")) {
            policy = getPzuLkPolicy(policy, policyStringArray);
        }

        if (policyString.contains("Compensa Vienna Insurance Group") && policyString.contains("LIIKLUSKINDLUSTUSE")) {
            policy = getSeesamLkPolicy(policy, policyStringArray);
        }

        if (policyString.contains("INGES") && policyString.contains("Liikluskindlustus")) {
            policy = getIngesLkPolicy(policy, policyStringArray);
        }

        if (policyString.contains("Kindlustusandja If P&C Insurance AS") && policyString.contains("Liikluskindlustus")) {
            policy = getIfLkPolicy(policy, policyStringArray);
        }

        if (policyString.contains("ERGO") && policyString.contains("Liikluskindlustus")) {
            policy = getErgoLkPolicy(policy, policyStringArray);
        }

        if (policyString.contains("Gjensidige") && policyString.contains("Liikluskindlustuse")) {
            policy = getGjensidigeLkPolicy(policy, policyStringArray);
        }

        if (policyString.contains("BTA Baltic Insurance Company") && policyString.contains("LIIKLUSKINDLUSTUS")) {
            policy = getBtaLkPolicy(policy, policyStringArray);
        }


        return policy;
    }

    // POLICIES
    private Policy getPzuLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("PZU"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Nimi") && policyStringArray[i + 1].contains("Registrikood")) {
                String[] persCodeLineArray = policyStringArray[i + 1].split(" ");
                String persCode = persCodeLineArray[1];

                Client client = new Client();
                client.setCode(persCode);
                if (!clientService.existsByCode(persCode)) {
                    client = newClientPzu(client, policyStringArray);
                } else {
                    client = clientService.findByCode(persCode);
                }
                policy.setClient(client);
            } else if (policyStringArray[i].contains("Nimi") && policyStringArray[i + 1].contains("Isikukood")) {
                String[] persCodeLineArray = policyStringArray[i + 1].split(" ");
                String persCode = persCodeLineArray[1];

                Client client = new Client();
                client.setCode(persCode);
                if (!clientService.existsByCode(persCode)) {
                    client = newClientPzu(client, policyStringArray);
                } else {
                    client = clientService.findByCode(persCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Mudel")) {
                String regNr = policyStringArray[i + 3];
                String model = policyStringArray[i + 5];
                policy.setObject(regNr + ", " + model);
            }

            if (policyStringArray[i].equals("Kindlustusperiood")) {
                String dateStart = policyStringArray[i + 1].split(" ")[0];
                String dateEnd = policyStringArray[i + 1].split(" ")[4];
                try {
                    policy.setStartDate(LocalDate.parse(dateStart, formatter));
                    policy.setEndDate(LocalDate.parse(dateEnd, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Rohelise kaardi riigid va Venemaa, Valgevene, Iraan.")) {
                String dateConcl = policyStringArray[i + 1].trim();
                try {
                    policy.setConclusionDate(LocalDate.parse(dateConcl, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Liikluskindlustuse poliis nr ")) {
                String[] numberLineArray = policyStringArray[i].split(" ");
                String number = numberLineArray[3];
                policy.setPolicyNumber(number);
            }

            if (policyStringArray[i].contains("Kindlustusmakse kokku ")) {
                try {
                    String[] sumLineArray = policyStringArray[i].split(" ");
                    String sumString = sumLineArray[2];
                    if (sumString.contains(",")) {
                        String[] sumParts = sumString.split(",");
                        Double sumPart1 = Double.valueOf(sumParts[0]);
                        Double sumPart2 = Double.valueOf(sumParts[1]);
                        Double sumTotal = sumPart1 + sumPart2 / 100;
                        policy.setSum(sumTotal);
                    } else {
                        Double sumTotal = Double.valueOf(sumString);
                        policy.setSum(sumTotal);
                    }
                } catch (Exception e) {
                }
            }
        }

        return policy;
    }

    private Policy getSeesamLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("Seesam"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {


            if (policyStringArray[i].contains("Isikukood / Registrikood ")) {
                Client client = new Client();

                String code = policyStringArray[i].split("Isikukood / Registrikood ")[1].trim();
                if (!clientService.existsByCode(code)) {
                    client = newClientSeesamLk(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(code);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("KINDLUSTATUD ESE")) {
                String carNumberString = policyStringArray[i + 1];
                String carNumber = carNumberString.split("Registreerimismärk ")[1];

                String carModelString = policyStringArray[i + 2];
                String carModel = carModelString.split("Sõiduki mark ja mudel ")[1];

                policy.setObject(carNumber + ", " + carModel);
            }

            if (policyStringArray[i].contains("LIIKLUSKINDLUSTUSE LEPING NR ")) {
                String[] numberLineArray = policyStringArray[i].split("LIIKLUSKINDLUSTUSE LEPING NR ");
                String number = numberLineArray[1];
                policy.setPolicyNumber(number);
            }

            if (policyStringArray[i].contains("Registreeritud Liikluskindlustuse registris ")) {
                String[] dateConclArray = policyStringArray[i].split(" ");
                String dateConcl = dateConclArray[3];
                try {
                    policy.setConclusionDate(LocalDate.parse(dateConcl, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Kindlustusperiood ")) {
                String[] periodLine = policyStringArray[i].split(" ");
                try {
                    String start = periodLine[1];
                    String end = periodLine[3];
                    policy.setStartDate(LocalDate.parse(start, formatter));
                    policy.setEndDate(LocalDate.parse(end, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Kindlustusmakse ")) {
                try {
                    String[] sumLineArray = policyStringArray[i].split(" ");
                    String sumString = sumLineArray[1];
                    if (sumString.contains(",")) {
                        String[] sumParts = sumString.split(",");
                        Double sumPart1 = Double.valueOf(sumParts[0]);
                        Double sumPart2 = Double.valueOf(sumParts[1]);
                        Double sumTotal = sumPart1 + sumPart2 / 100;
                        policy.setSum(sumTotal);
                    } else {
                        Double sumTotal = Double.valueOf(sumString);
                        policy.setSum(sumTotal);
                    }
                } catch (Exception e) {
                }
            }
        }

        return policy;
    }

    private Policy getIngesLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("INGES"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Isiku- või registrikood ")) {
                String[] persCodeLineArray = policyStringArray[i].split(" Isiku- või registrikood ");

                Client client = new Client();
                String persCode = persCodeLineArray[1];
                client.setCode(persCode);
                if (!clientService.existsByCode(persCode)) {
                    client = newClientIngesLk(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(persCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Sõiduk / Vehicle")) {
                String[] lineForMark = policyStringArray[i].split(" Registreerimismärk ");
                String lineFormark1 = lineForMark[0];
                String[] lineForMark2 = lineFormark1.split("Sõiduk / Vehicle ");
                String mark = lineForMark2[1];
                String carNumber = lineForMark[1];
                policy.setObject(carNumber + ", " + mark);
            }

            if (policyStringArray[i].contains("Kindlustusleping / Insurance policy nr ")) {
                String[] numberLineArray = policyStringArray[i].split(" ");
                String number = numberLineArray[5];
                policy.setPolicyNumber(number);
            }

            if (policyStringArray[i].contains("Leping väljastatud ")) {
                String[] datesLineArray = policyStringArray[i].split(" Leping väljastatud ");
                String dateConclString = datesLineArray[1];
                try {
                    policy.setConclusionDate(LocalDate.parse(dateConclString, formatter));
                } catch (Exception e) {
                }
                String period = datesLineArray[0];

                if (period.length() > 21) {
                    String dateStart = period.split(" ")[0];
                    String dateEnd = period.split(" ")[3];
                    try {
                        policy.setStartDate(LocalDate.parse(dateStart, formatter));
                    } catch (Exception e) {
                    }
                    try {
                        policy.setEndDate(LocalDate.parse(dateEnd, formatter));
                    } catch (Exception e) {
                    }
                } else {
                    String[] dateStartEndArray = datesLineArray[0].split("-");
                    String dateStart = dateStartEndArray[0];
                    String dateEnd = dateStartEndArray[1];
                    try {
                        policy.setStartDate(LocalDate.parse(dateStart, formatter));
                    } catch (Exception e) {
                    }
                    try {
                        policy.setEndDate(LocalDate.parse(dateEnd, formatter));
                    } catch (Exception e) {
                    }
                }
            }

            if (policyStringArray[i].contains("Kindlustusmakse ")) {
                try {
                    String[] sumLineArray = policyStringArray[i].split(" ");
                    String sumString = sumLineArray[1];
                    if (sumString.contains(".")) {
                        String[] sumParts = sumString.split("\\.");
                        Double sumPart1 = Double.valueOf(sumParts[0]);
                        Double sumPart2 = 0d;
                        if (sumParts[1].length() == 1) {
                            sumPart2 = Double.valueOf(sumParts[1]) * 10;
                        } else {
                            sumPart2 = Double.valueOf(sumParts[1]);
                        }
                        Double sumTotal = sumPart1 + sumPart2 / 100;
                        policy.setSum(sumTotal);
                    } else {
                        Double sumTotal = Double.valueOf(sumString);
                        policy.setSum(sumTotal);
                    }
                } catch (Exception e) {
                }
            }
        }

        return policy;
    }

    private Policy getIfLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("IF"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("registrikood")) {
                String personRegCode = policyStringArray[i + 1].trim();
                Client client = new Client();
                client.setCode(personRegCode);
                if (!clientService.existsByCode(personRegCode)) {
                    client = newClientIfLk(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(personRegCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Sõiduk ")) {
                String carData = policyStringArray[i].split("Sõiduk")[1];
                String markNumber = carData.split(", VIN")[0].trim();

                String[] markNumberArray = markNumber.split(" ");
                String number = markNumberArray[markNumberArray.length - 1];

                String mark = markNumber.split(number)[0].trim();

                policy.setObject(number + ", " + mark);
            }

            if (policyStringArray[i].contains("Poliis nr ")) {
                String[] numberLineArray = policyStringArray[i].split(" ");
                String number = numberLineArray[2];
                policy.setPolicyNumber(number);
            }

            if (policyStringArray[i].contains("Kindlustusperiood ")) {
                String start = policyStringArray[i].split(" ")[1];
                String end = policyStringArray[i].split(" ")[4];
                try {
                    policy.setStartDate(LocalDate.parse(start, formatter));
                } catch (Exception e) {
                }
                try {
                    policy.setEndDate(LocalDate.parse(end, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Vormistamise kuupäev: ")) {
                String date = policyStringArray[i].split("Vormistamise kuupäev: ")[1];
                try {
                    policy.setConclusionDate(LocalDate.parse(date, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].startsWith("Kindlustusmakse ") && policyStringArray[i].contains("€")) {
                try {
                    String sumString = "";
                    if(policyStringArray[i].contains(" Kokku ")){
                        sumString = policyStringArray[i].split(" ")[2];
                    } else {
                        sumString = policyStringArray[i].split(" ")[1];
                    }

                    if (sumString.contains(".")) {
                        String[] sumParts = sumString.split("\\.");
                        Double sumPart1 = Double.valueOf(sumParts[0]);
                        Double sumPart2 = 0d;
                        if (sumParts[1].length() == 1) {
                            sumPart2 = Double.valueOf(sumParts[1]) * 10;
                        } else {
                            sumPart2 = Double.valueOf(sumParts[1]);
                        }
                        Double sumTotal = sumPart1 + sumPart2 / 100;
                        policy.setSum(sumTotal);
                    } else {
                        Double sumTotal = Double.valueOf(sumString);
                        policy.setSum(sumTotal);
                    }
                } catch (Exception e) {
                }
            }
        }
        return policy;
    }

    private Policy getErgoLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("ERGO"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {
            if (i == 1 && policyStringArray[i].contains("KINDLUSTUSVÕTJA")) {
                String regCodeLine = policyStringArray[i + 2];
                Client client = new Client();
                String regCode = "";
                if (regCodeLine.contains("Registrikood")) {
                    regCode = regCodeLine.split(" Registrikood ")[1];
                }
                if (regCodeLine.contains("Isikukood")) {
                    regCode = regCodeLine.split(" Isikukood ")[1];
                }
                if (!clientService.existsByCode(regCode)) {
                    client.setCode(regCode);
                    client = newClientErgoLk(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(regCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Kindlustusobjekt Sõiduk ")) {
                if (policyStringArray[i].contains("Tehasetähis ")) {
                    String carModel = policyStringArray[i].split("Tehasetähis")[0].split("Kindlustusobjekt Sõiduk ")[1].trim();
                    policy.setObject(carModel);
                } else {
                    String carModel = policyStringArray[i].split("Kindlustusobjekt Sõiduk ")[1].trim();
                    policy.setObject(carModel);
                }
            }

            if (policyStringArray[i].contains("Reg. märk ")) {
                String regCode = policyStringArray[i].split(" ")[2];
                policy.setObject(regCode + ", " + policy.getObject());
            }

            if (policyStringArray[i].contains("Kindlustusperiood ")) {
                if (policyStringArray[i].contains(":")) {
                    try {
                        String periodsLine = policyStringArray[i];
                        String startDateString = periodsLine.split(" ")[4];
                        String endDateString = periodsLine.split(" ")[7];

                        policy.setStartDate(LocalDate.parse(startDateString, formatter));
                        policy.setEndDate(LocalDate.parse(endDateString, formatter));

                    } catch (Exception e) {
                    }
                } else {
                    try {
                        String periodsLine = policyStringArray[i];
                        String startDateString = periodsLine.split(" ")[4];
                        String endDateString = periodsLine.split(" ")[6];

                        policy.setStartDate(LocalDate.parse(startDateString, formatter));
                        policy.setEndDate(LocalDate.parse(endDateString, formatter));

                    } catch (Exception e) {
                    }
                }
            }

            if (policyStringArray[i].contains("Väljastamise kuupäev ")) {
                try {
                    String dateConcString = policyStringArray[i].split(" ")[2];
                    dateConcString = dateConcString.substring(0, dateConcString.length() - 1);
                    policy.setConclusionDate(LocalDate.parse(dateConcString, formatter));

                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("KINDLUSTUSPOLIIS nr ")) {
                String number = policyStringArray[i].split(" ")[2];
                policy.setPolicyNumber(number);
            }

            if (policyStringArray[i].contains("Makseosade arv")) {
                int parts = Integer.valueOf(policyStringArray[i].split(" ")[2]);
                policy.setParts(parts);
            }

            if (policyStringArray[i].contains("Kindlustusperioodi makse ")) {
                String sumString = policyStringArray[i]
                        .split("eurot")[0].trim()
                        .split("Kindlustusperioodi makse ")[1]
                        .replaceAll(" ", "");


                if (sumString.contains(",")) {
                    String[] sumParts = sumString.split(",");
                    Double sumPart1 = Double.valueOf(sumParts[0]);
                    Double sumPart2 = 0d;
                    if (sumParts[1].length() == 1) {
                        sumPart2 = Double.valueOf(sumParts[1]) * 10;
                    } else {
                        sumPart2 = Double.valueOf(sumParts[1]);
                    }
                    Double sumTotal = sumPart1 + sumPart2 / 100;
                    policy.setSum(sumTotal);
                } else {
                    Double sumTotal = Double.valueOf(sumString);
                    policy.setSum(sumTotal);
                }
            }
        }
        return policy;
    }

    private Policy getGjensidigeLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("GJENSIDIGE AAS"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains(" registrikood ") && !policyStringArray[i].contains("Gjensidige")) {
                String[] persCodeLineArray = policyStringArray[i].split(", registrikood ");
                String persCode = persCodeLineArray[1];

                Client client = new Client();
                client.setCode(persCode);
                if (!clientService.existsByCode(persCode)) {
                    client = newFirmClientGjensidige(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(persCode);
                }
                policy.setClient(client);
            } else if (policyStringArray[i].contains("isikukood")) {
                String[] persCodeLineArray = policyStringArray[i].split("isikukood ");
                String persCode = persCodeLineArray[1];

                Client client = new Client();
                client.setCode(persCode);
                if (!clientService.existsByCode(persCode)) {
                    client = newPersonClientGjensidige(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(persCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Insurance period ")) {
                try {
                    String[] datesLineArray = policyStringArray[i].split(" ");
                    String dateStartString = datesLineArray[2];
                    String dateEndString = datesLineArray[5];

                    policy.setStartDate(LocalDate.parse(dateStartString, formatter));
                    policy.setEndDate(LocalDate.parse(dateEndString, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Date of issue")) {
                try {
                    String[] conclDateLineArray = policyStringArray[i + 1].split(" ");
                    String dateConclString = conclDateLineArray[0];
                    policy.setConclusionDate(LocalDate.parse(dateConclString, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Mark / Mudel")) {
                String carMark = policyStringArray[i - 1].trim();
                policy.setObject(carMark);
            }

            if (policyStringArray[i].contains("Vehicle plate number ")) {
                String[] regMarkLineArray = policyStringArray[i].split(" ");
                String regMark = regMarkLineArray[3];
                policy.setObject(regMark + ", " + policy.getObject());
            }

            if (policyStringArray[i].contains("Kindlustusmakse kokku")) {
                try {
                    String[] sumLineArray = policyStringArray[i - 1].split(" ");
                    String sumString = sumLineArray[0];
                    if (sumString.contains(",")) {
                        String[] sumParts = sumString.split(",");
                        Double sumPart1 = Double.valueOf(sumParts[0]);
                        Double sumPart2 = 0d;
                        if (sumParts[1].length() == 1) {
                            sumPart2 = Double.valueOf(sumParts[1]) * 10;
                        } else {
                            sumPart2 = Double.valueOf(sumParts[1]);
                        }
                        Double sumTotal = sumPart1 + sumPart2 / 100;
                        policy.setSum(sumTotal);
                    } else {
                        Double sumTotal = Double.valueOf(sumString);
                        policy.setSum(sumTotal);
                    }
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("R80")) {
                policy.setPolicyNumber(policyStringArray[i].trim());
            }
        }


        return policy;
    }

    private Policy getBtaLkPolicy(Policy policy, String[] policyStringArray) {
        policy.setInsurer(insurerService.findByName("BTA"));
        policy.setPolicyType(policyTypeService.findByType("LK"));
        policy.setParts(1);
        policy.setReminder(true);
        policy.setPaymentType(paymentTypeService.findByPrefix("SR"));
        policy.setAgent(agentService.findByPrefix(""));

        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Poliis nr ")) {
                String[] numberLineArray = policyStringArray[i].split("Poliis nr ");
                String number = numberLineArray[1];
                policy.setPolicyNumber(number);
            }

            if (policyStringArray[i].contains("Kindlustusvõtja") && policyStringArray[i + 2].contains("Isikukood:")) {
                String personalCode = policyStringArray[i + 2].split("Isikukood: ")[1].trim();

                Client client = new Client();
                client.setCode(personalCode);
                if(!clientService.existsByCode(personalCode)) {
                    client = newPersonClientBta(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(personalCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Kindlustusvõtja") && policyStringArray[i + 2].contains("Registrikood: ")) {
                String personalCode = policyStringArray[i + 2].split("Registrikood: ")[1].trim();

                Client client = new Client();
                client.setCode(personalCode);
                if(!clientService.existsByCode(personalCode)) {
                    client = newFirmClientBta(client, policyStringArray);
                    clientService.save(client);
                } else {
                    client = clientService.findByCode(personalCode);
                }
                policy.setClient(client);
            }

            if (policyStringArray[i].contains("Reg märk: ")) {
                String markString = policyStringArray[i].split("Reg märk: ")[1].trim().split(" Väljalaskeaasta: ")[0];
                policy.setObject(markString);
            }

            if (policyStringArray[i].contains("Mark, Mudel: ")) {
                String markString = policyStringArray[i].split("Mark, Mudel: ")[1].trim().split(" Reg tunnistuse nr: ")[0];
                policy.setObject(policy.getObject() + ", " + markString);
            }

            if (policyStringArray[i].contains("Kindlustusperiood: ")) {
                String startString = policyStringArray[i].split("Kindlustusperiood: ")[1].trim().split(",")[0];
                String endString = policyStringArray[i].split(" - ")[1].trim();
                try {
                    policy.setStartDate(LocalDate.parse(startString, formatter));
                    policy.setEndDate(LocalDate.parse(endString, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Väljastamise kuupäev ja aeg: ")) {
                String conclDateString = policyStringArray[i].split("Väljastamise kuupäev ja aeg: ")[1].trim().split(",")[0].trim();
                System.out.println("Conclusion: " + conclDateString);
                try {
                    policy.setConclusionDate(LocalDate.parse(conclDateString, formatter));
                } catch (Exception e) {
                }
            }

            if (policyStringArray[i].contains("Makse kokku: ")) {
                String sumString = policyStringArray[i].split("Makse kokku: ")[1].split("EUR")[0];
                if (sumString.contains(",")) {
                    String[] sumParts = sumString.split(",");
                    Double sumPart1 = Double.valueOf(sumParts[0]);
                    Double sumPart2 = Double.valueOf(sumParts[1]);
                    Double sumTotal = sumPart1 + sumPart2 / 100;
                    policy.setSum(sumTotal);
                } else {
                    Double sumTotal = Double.valueOf(sumString);
                    policy.setSum(sumTotal);
                }
            }

        }

        return policy;
    }


    // CLIENTS
    private Client newClientPzu(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Nimi: ") && !policyStringArray[i].contains("Kindlustuskaitse OÜ ") && !policyStringArray[i].contains("PZU")) {
                String name = policyStringArray[i].split("Nimi: ")[1].split(" Aadress:")[0];
                client.setName(name);
            }
        }
        return client;
    }

    private Client newClientSeesamLk(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("KINDLUSTUSVÕTJA")) {
                String nameString = policyStringArray[i + 1];
                String name = nameString.split("Nimi")[1].trim();
                client.setName(name);
            }
        }
        return client;
    }

    private Client newClientIngesLk(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Isiku- või registrikood ") && !policyStringArray[i].contains("Sõiduki vastutav kasutaja")) {
                String name = policyStringArray[i].split("Isiku- või registrikood ")[0];
                client.setName(name);
            }
        }
        return client;
    }

    private Client newClientIfLk(Client client, String[] policyStringArray) {
        String name = "";
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Kindlustusvõtja")) {
                name = policyStringArray[i].split("Kindlustusvõtja")[1].trim();
                break;
            }
        }
        client.setName(name);
        return client;
    }

    private Client newClientErgoLk(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (i == 1 && policyStringArray[i].contains("KINDLUSTUSVÕTJA")) {
                String firmName = policyStringArray[i + 1].trim();
                client.setName(firmName);
                break;
            }
        }
        return client;
    }

    public Client newPersonClientGjensidige(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("isikukood")) {
                String[] nameLineArray = policyStringArray[i].split(", isikukood ");
                client.setName(nameLineArray[0]);
                if (!policyStringArray[i + 1].contains("Kindlustusperiood")) {
                    String address = policyStringArray[i + 1].trim();
                    client.setAddress(address);
                }
            }
        }
        return client;
    }

    public Client newFirmClientGjensidige(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("registrikood ") && !policyStringArray[i].contains("Gjensidige")) {
                String[] nameLineArray = policyStringArray[i].split(", registrikood");
                client.setName(nameLineArray[0].trim());
                if (!policyStringArray[i + 1].contains("Kindlustusperiood")) {
                    String address = policyStringArray[i + 1].trim();
                    client.setAddress(address);
                }
            }
        }
        return client;
    }

    public Client newPersonClientBta(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Kindlustusvõtja:") && policyStringArray[i + 1].contains("Nimi: ")) {
                String name = policyStringArray[i + 1].split("Nimi: ")[1].split(" Aadress: ")[0].trim();
                client.setName(name);
            }
        }
        return client;
    }

    public Client newFirmClientBta(Client client, String[] policyStringArray) {
        for (int i = 0; i < policyStringArray.length; i++) {
            if (policyStringArray[i].contains("Kindlustusvõtja:") && policyStringArray[i + 1].contains("Nimi: ")) {
                String name = policyStringArray[i + 1].split("Nimi: ")[1].split(" Aadress: ")[0].trim();
                client.setName(name);
            }
        }
        return client;
    }

}