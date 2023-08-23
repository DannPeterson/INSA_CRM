package ee.insa.crmapp.google;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import ee.insa.crmapp.model.PolicyPart;
import ee.insa.crmapp.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SheetsService {
    private final PolicyService policyService;
    private static final String APPLICATION_NAME = "INSA CRM";
    private static final String SPREADSHEET_ID = "1jKqnHQQf9wKHsQeJBPh0ZoWMi_dBfupLoh_FaUsR_3w";
    private static final String KEY_FILE_LOCATION = "insa-392711-c734389e6777.json";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy");

    private Sheets service;

    @Autowired
    public SheetsService(PolicyService policyService) throws GeneralSecurityException, IOException {
        this.policyService = policyService;

        InputStream keyFileStream = new ClassPathResource(KEY_FILE_LOCATION).getInputStream();
        GoogleCredentials credentials = ServiceAccountCredentials
                .fromStream(keyFileStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        service = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public boolean isPartInserted(PolicyPart policyPart) throws IOException {
        if (policyPart.getPolicy().getInsurer() == null) return false;

        List<List<Object>> colPolicyNumberAndColComment = new ArrayList<>();

        switch (policyPart.getPolicy().getInsurer().getName()) {
            case "Seesam":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "N", SheetName.SEES);
                break;
            case "ERGO":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "N", SheetName.ERGO);
                break;
            case "PZU":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "N", SheetName.PZU_KKS);
                break;
            case "GJENSIDIGE AAS":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "R", SheetName.GJ_FIE);
                break;
            case "INGES":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "P", SheetName.INGES);
                break;
            case "BTA":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "M", SheetName.BTA);
                break;
            case "IF":
                colPolicyNumberAndColComment = getCol1andCol2FromSheet("D", "O", SheetName.IF);
                break;
        }

        if (policyPart.getPolicy().getParts() == 1) {
            return colPolicyNumberAndColComment.stream()
                    .anyMatch(row ->
                            row.size() > 0 &&
                                    row.get(0) != null &&
                                    ((String) row.get(0)).trim().equals(policyPart.getPolicy().getPolicyNumber().trim()));

        } else if (policyPart.getPolicy().getParts() > 1) {
            String colCommentValue = policyPart.getPart() + "/" + policyPart.getPolicy().getParts();
            return colPolicyNumberAndColComment.stream()
                    .anyMatch(row ->
                            row.size() == 2 &&
                                    row.get(0) != null &&
                                    row.get(1) != null &&
                                    ((String) row.get(0)).trim().equals(policyPart.getPolicy().getPolicyNumber().trim()) &&
                                    ((String) row.get(1)).trim().equals(colCommentValue));
        }
        return false;
    }

    public boolean insertPolicyPart(PolicyPart policyPart) throws IOException {
        if (policyPart.getPolicy().getInsurer() == null) {
            return false;
        }
        if (policyPart.getPolicy().getPolicyNumber() == null) {
            return false;
        }
        List<List<Object>> colABdata = new ArrayList<>();
        switch (policyPart.getPolicy().getInsurer().getName()) {
            case "Seesam":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.SEES);
                break;
            case "BTA":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.BTA);
                break;
            case "ERGO":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.ERGO);
                break;
            case "GJENSIDIGE AAS":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.GJ_FIE);
                break;
            case "IF":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.IF);
                break;
            case "INGES":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.INGES);
                break;
            case "PZU":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.PZU_KKS);
                break;
            case "Salva":
                colABdata = getCol1andCol2FromSheet("A", "B", SheetName.SALVA);
                break;
        }
        Map<Integer, DatePair> datePairMap = getDatePairsFromABdata(colABdata);

        Map.Entry<Integer, DatePair> insertionRangeStartEntry = findInsertionRangeStart(LocalDate.now(), datePairMap);
        Map<Integer, List<Object>> insertionRangeABCells = getPeriodABCellsWithIndexes(insertionRangeStartEntry, colABdata);

        int firstEmptyRowIndex;

        switch (policyPart.getPolicy().getInsurer().getName()) {
            case "Seesam":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.SEES, new int[]{4, 5, 6, 7, 8, 9, 14});
                insertSeesamPartRow(firstEmptyRowIndex, policyPart);
                break;
            case "BTA":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.BTA, new int[]{4, 5, 6, 13});
                insertBtaPartRow(firstEmptyRowIndex, policyPart);
                break;
            case "ERGO":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.ERGO, new int[]{4, 5, 6, 7, 8, 9, 14});
                insertErgoPartRow(firstEmptyRowIndex, policyPart);
                break;
            case "GJENSIDIGE AAS":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.GJ_FIE, new int[]{4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 18});
                insertGjensidigePartRow(firstEmptyRowIndex, policyPart);
                break;
            case "IF":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.IF, new int[]{4, 5, 6, 7, 8, 9, 10, 16});
                insertIfPartRow(firstEmptyRowIndex, policyPart);
                break;
            case "INGES":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.INGES, new int[]{4, 5, 6, 7, 8, 9, 10, 16});
                insertIngesPartRow(firstEmptyRowIndex, policyPart);
                break;
            case "PZU":
                firstEmptyRowIndex = findOrAddFirstEmptyRow(insertionRangeABCells, SheetName.PZU_KKS, new int[]{4, 5, 6, 7, 8, 9, 14});
                insertPzuPartRow(firstEmptyRowIndex, policyPart);
                break;
        }
        return true;
    }

    private Map<Integer, DatePair> getDatePairsFromABdata(List<List<Object>> data) {
        Map<Integer, DatePair> datePairs = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            List<Object> row = data.get(i);
            if (row.size() >= 2) {
                String cellA = (String) row.get(0);
                if (!cellA.isEmpty()) {
                    try {
                        LocalDate dateA = LocalDate.parse((String) row.get(0), formatter);
                        LocalDate dateB = LocalDate.parse((String) row.get(1), formatter);
                        datePairs.put(i + 1, new DatePair(dateA, dateB));
                    } catch (DateTimeParseException e) {
                        System.out.println("ERROR PARSING DATES");
                    }
                }
            }
        }
        return datePairs;
    }

    private List<List<Object>> getCol1andCol2FromSheet(String col1, String col2, SheetName sheetName) throws IOException {
        String range1 = sheetName.getSheetName() + "!" + col1 + ":" + col1;
        ValueRange response1 = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range1)
                .execute();

        String range2 = sheetName.getSheetName() + "!" + col2 + ":" + col2;
        ValueRange response2 = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range2)
                .execute();

        List<List<Object>> result = new ArrayList<>();
        for (int i = 0; i < response1.getValues().size() || i < response2.getValues().size(); i++) {
            List<Object> row = new ArrayList<>();
            if (i < response1.getValues().size()) {
                row.addAll(response1.getValues().get(i));
            }
            if (i < response2.getValues().size()) {
                row.addAll(response2.getValues().get(i));
            }
            result.add(row);
        }

        return result;
    }

    private Map.Entry<Integer, DatePair> findInsertionRangeStart(LocalDate date, Map<Integer, DatePair> datePairs) {
        for (Map.Entry<Integer, DatePair> entry : datePairs.entrySet()) {
            if ((date.isAfter(entry.getValue().getStartDate()) || date.isEqual(entry.getValue().getStartDate()))
                    && (date.isBefore(entry.getValue().getEndDate()) || date.isEqual(entry.getValue().getEndDate()))) {
                return entry;
            }
        }
        return null;
    }

    private Map<Integer, List<Object>> getPeriodABCellsWithIndexes(Map.Entry<Integer, DatePair> insertionRangeStartEntry, List<List<Object>> allData) {
        int startRow = insertionRangeStartEntry.getKey();
        Map<Integer, List<Object>> periodRows = new LinkedHashMap<>();

        for (int i = startRow; i < allData.size(); i++) {
            List<Object> row = allData.get(i);
            if (row.isEmpty()) {  // Добавляем пустые строки без каких-либо проверок.
                periodRows.put(i, row);
            } else if (row.size() >= 1) {
                String cellB = (String) row.get(0);
                // Проверяем, является ли содержимое клетки нулём, целым числом или числом с двуми знаками после запятой.
                if (cellB.matches("^0$|^[0-9]+$|^[0-9]+,[0-9]{2}$")) {
                    break; // End of the period.
                } else {
                    periodRows.put(i, row);
                }
            }
        }

        return periodRows;
    }

    private Integer findOrAddFirstEmptyRow(Map<Integer, List<Object>> insertionRangeABCells, SheetName sheetName, int[] columnsWithAutosumToUpdate) throws IOException {
        Integer firstEmptyRow = findFirstEmptyRow(insertionRangeABCells);
        if (firstEmptyRow == null) {
            firstEmptyRow = Collections.max(insertionRangeABCells.keySet()) + 1;
            insertEmptyRowInSheet(firstEmptyRow, sheetName);  // Вызываем метод добавления строки только если пустая строка не найдена.

            for (int column : columnsWithAutosumToUpdate) {
                updateSumFormula(firstEmptyRow + 1, column, sheetName);
            }
        }
        return firstEmptyRow;
    }

    private void insertSeesamPartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell G

                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell H
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell I
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell J

                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "",  // Cell K

                        policyPart.getPolicy().getClient().getName(),  // Cell L
                        policyPart.getPolicy().getObject(), // Cell M
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell N
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.SEES.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertBtaPartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell G
                        "",  // Cell H
                        "",  // Cell I
                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "",  // Cell J
                        policyPart.getPolicy().getClient().getName(),  // Cell K
                        policyPart.getPolicy().getObject(),  // Cell L
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell M
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.BTA.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertErgoPartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell G
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell H
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell I
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell J
                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "",  // Cell K
                        policyPart.getPolicy().getClient().getName(),  // Cell L
                        policyPart.getPolicy().getObject(), // Cell M
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell N
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.ERGO.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertGjensidigePartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        String policyType = policyPart.getPolicy().getPolicyType().getType();
        String paymentType = policyPart.getPolicy().getPaymentType().getType();

        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyType.equals("LK") && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyType.equals("LK") && paymentType.equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyType.equals("LK") && paymentType.equals("Terminaal") ? policyPart.getSum() : "",  // Cell G
                        (policyType.equals("Kasko") || policyType.equals("Cargo") || policyType.equals("CAR Ehitus-Montaaž") || policyType.equals("CMR") || policyType.equals("Garantii") || policyType.equals("Reisi Kindlustus")) && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell H 10%
                        (policyType.equals("Õnnetus") || policyType.equals("Ehitusmasinad")) && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell I 12%
                        (policyType.equals("Vastutus") || policyType.equals("VJ")) && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell J 14%
                        policyType.equals("VI") && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell K 15%
                        "",  // Cell L "
                        !policyType.equals("LK") && paymentType.equals("Sularaha") ? policyPart.getSum() : "",  // Cell M
                        !policyType.equals("LK") && paymentType.equals("Terminaal") ? policyPart.getSum() : "",  // Cell N
                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "", // Cell O
                        policyPart.getPolicy().getClient().getName(), // Cell P
                        policyPart.getPolicy().getObject(), // Cell Q
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell R
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.GJ_FIE.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertIfPartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        String policyType = policyPart.getPolicy().getPolicyType().getType();
        String paymentType = policyPart.getPolicy().getPaymentType().getType();

        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyType.equals("LK") && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyType.equals("LK") && paymentType.equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyType.equals("LK") && paymentType.equals("Terminaal") ? policyPart.getSum() : "",  // Cell G
                        (policyType.equals("KASKO") || policyType.equals("Ehitusmasinad") || policyType.equals("VJ") || policyType.equals("Reisi Kindlustus")) && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell H
                        policyType.equals("VI") && paymentType.equals("Ülekanne") ? policyPart.getSum() : "", // Cell I
                        !policyType.equals("LK") && paymentType.equals("Sularaha") ? policyPart.getSum() : "",  // Cell J
                        !policyType.equals("LK") && paymentType.equals("Terminaal") ? policyPart.getSum() : "",  // Cell K
                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "",  // Cell L
                        policyPart.getPolicy().getClient().getName(),  // Cell M
                        policyPart.getPolicy().getObject(), // Cell N
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell O
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.IF.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertIngesPartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        String policyType = policyPart.getPolicy().getPolicyType().getType();
        String paymentType = policyPart.getPolicy().getPaymentType().getType();

        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyType.equals("LK") && paymentType.equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyType.equals("LK") && paymentType.equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyType.equals("LK") && paymentType.equals("Terminaal") ? policyPart.getSum() : "",  // Cell G

                        policyType.equals("Reisi Kindlustus") && paymentType.equals("Ülekanne") ? policyPart.getSum() : "", // Cell H
                        "",  // Cell I
                        policyType.equals("Reisi Kindlustus") && paymentType.equals("Sularaha") ? policyPart.getSum() : "", // Cell J
                        policyType.equals("Reisi Kindlustus") && paymentType.equals("Terminaal") ? policyPart.getSum() : "", // Cell K
                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "", // Cell L
                        policyPart.getPolicy().getClient().getName(),  // Cell M
                        policyPart.getPolicy().getObject(), // Cell N
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell O
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.INGES.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertPzuPartRow(int rowIndex, PolicyPart policyPart) throws IOException {
        // Создаем данные для вставки.
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        "",  // Cell A
                        LocalDate.now().format(formatter),  // Cell B
                        policyPart.getInvoice() != null ? policyPart.getInvoice().getInvoiceNumber() : "",  // Cell C
                        policyPart.getPolicy().getPolicyNumber(),  // Cell D
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell E
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell F
                        policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell G

                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Ülekanne") ? policyPart.getSum() : "",  // Cell H
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Sularaha") ? policyPart.getSum() : "",  // Cell I
                        !policyPart.getPolicy().getPolicyType().getType().equals("LK") && policyPart.getPolicy().getPaymentType().getType().equals("Terminaal") ? policyPart.getSum() : "",  // Cell J

                        policyPart.getPolicy().getPercent() != null ? policyPart.getPolicy().getPercent().divide(new BigDecimal(100)) : "",  // Cell K

                        policyPart.getPolicy().getClient().getName(),  // Cell L
                        policyPart.getPolicy().getObject(), // Cell M
                        policyPart.getPolicy().getParts() > 1 ? policyPart.getPart() + "/" + policyPart.getPolicy().getParts() : ""   // Cell N
                )
        );

        // Создаем объект ValueRange, содержащий новые значения.
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = service.spreadsheets().values()
                .update(SPREADSHEET_ID, SheetName.PZU_KKS.getSheetName() + "!A" + (rowIndex + 1), body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    private void insertEmptyRowInSheet(Integer rowIndex, SheetName sheetName) throws IOException {
        // Добавляем пустую строку.
        List<Request> requests = new ArrayList<>();

        // Добавляем пустую строку на место rowIndex.
        requests.add(new Request()
                .setInsertDimension(new InsertDimensionRequest()
                        .setRange(new DimensionRange()
                                .setSheetId(sheetName.getSheetId())
                                .setDimension("ROWS")
                                .setStartIndex(rowIndex)
                                .setEndIndex(rowIndex + 1))
                        .setInheritFromBefore(false)));

        // Сбрасываем форматирование ячеек в новой строке.
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(sheetName.getSheetId())
                                .setStartRowIndex(rowIndex)
                                .setEndRowIndex(rowIndex + 1))
                        .setCell(new CellData()
                                .setUserEnteredFormat(new CellFormat()
                                        .setTextFormat(new TextFormat()
                                                .setBold(false)
                                                .setItalic(false)
                                                .setStrikethrough(false)
                                                .setUnderline(false))))
                        .setFields("userEnteredFormat.textFormat")));

        // Выполняем запрос.
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(SPREADSHEET_ID, batchUpdateRequest).execute();
    }

    private void updateSumFormula(Integer rowIndex, Integer columnIndex, SheetName sheetName) throws IOException {
        // Получение текущей формулы
        String range = sheetName.getSheetName() + "!R" + (rowIndex + 1) + "C" + (columnIndex + 1);
        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .setValueRenderOption("FORMULA")
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            String formula = (String) values.get(0).get(0);

            // Изменение диапазона в формуле
            Pattern pattern = Pattern.compile("SUM\\((.+?):(.+?)\\)");
            Matcher matcher = pattern.matcher(formula);
            if (matcher.find()) {
                String startCell = matcher.group(1);
                String endCell = matcher.group(2);
                String newEndCell = endCell.substring(0, endCell.length() - 1) + (Integer.parseInt(endCell.substring(endCell.length() - 1)) + 1);
                String newFormula = formula.replace(startCell + ":" + endCell, startCell + ":" + newEndCell);

                // Установка новой формулы
                List<Request> requests = new ArrayList<>();
                requests.add(new Request()
                        .setUpdateCells(new UpdateCellsRequest()
                                .setStart(new GridCoordinate()
                                        .setSheetId(sheetName.getSheetId())
                                        .setRowIndex(rowIndex)
                                        .setColumnIndex(columnIndex))
                                .setRows(Collections.singletonList(new RowData()
                                        .setValues(Collections.singletonList(new CellData()
                                                .setUserEnteredValue(new ExtendedValue()
                                                        .setFormulaValue(newFormula))))))
                                .setFields("userEnteredValue.formulaValue")));

                // Выполнение запроса
                BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                service.spreadsheets().batchUpdate(SPREADSHEET_ID, batchUpdateRequest).execute();
            }
        }
    }

    private Integer findFirstEmptyRow(Map<Integer, List<Object>> insertionRangeABCells) {
        for (Map.Entry<Integer, List<Object>> entry : insertionRangeABCells.entrySet()) {
            List<Object> row = entry.getValue();
            if (row.size() == 0) {
                return entry.getKey();
            }
        }
        return null;
    }
}
