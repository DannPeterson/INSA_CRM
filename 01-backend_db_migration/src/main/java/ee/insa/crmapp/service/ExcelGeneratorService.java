package ee.insa.crmapp.service;

import ee.insa.crmapp.configuration.AppConfig;
import ee.insa.crmapp.model.PolicyPart;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelGeneratorService {
    private AppConfig appConfig;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public ExcelGeneratorService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void generateExcelReport(List<PolicyPart> parts) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("платежи");

        int rownum = 0;
        Row dataDescrRow = sheet.createRow(rownum++);
        int dataDescrcCellnum = 0;

        XSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setBorderBottom(BorderStyle.MEDIUM);

        Cell cellPolicyTypeHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPolicyTypeHead.setCellValue("Тип");
        cellPolicyTypeHead.setCellStyle(headStyle);

        Cell cellClientHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellClientHead.setCellValue("Клиент");
        cellClientHead.setCellStyle(headStyle);

        Cell cellObjectHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellObjectHead.setCellValue("Объект");
        cellObjectHead.setCellStyle(headStyle);

        Cell cellInsurerHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellInsurerHead.setCellValue("СК");
        cellInsurerHead.setCellStyle(headStyle);

        Cell cellAgentHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellAgentHead.setCellValue("Агент");
        cellAgentHead.setCellStyle(headStyle);

        Cell cellPolicyHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPolicyHead.setCellValue("Полис");
        cellPolicyHead.setCellStyle(headStyle);

        Cell cellPolicyStartHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPolicyStartHead.setCellValue("Дата начала");
        cellPolicyStartHead.setCellStyle(headStyle);

        Cell cellPolicyEndHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPolicyEndHead.setCellValue("Дата окончания");
        cellPolicyEndHead.setCellStyle(headStyle);

        Cell cellTotalPartsHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellTotalPartsHead.setCellValue("Частей");
        cellTotalPartsHead.setCellStyle(headStyle);

        Cell cellPartHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPartHead.setCellValue("Часть");
        cellPartHead.setCellStyle(headStyle);

        Cell cellPolicySumHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPolicySumHead.setCellValue("Сумма");
        cellPolicySumHead.setCellStyle(headStyle);

        Cell cellPaymentTypeHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPaymentTypeHead.setCellValue("Тип оплаты");
        cellPaymentTypeHead.setCellStyle(headStyle);

        Cell cellPartDateHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPartDateHead.setCellValue("Срок оплаты");
        cellPartDateHead.setCellStyle(headStyle);

        Cell cellPartDatePaidHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPartDatePaidHead.setCellValue("Дата оплаты");
        cellPartDatePaidHead.setCellStyle(headStyle);

        Cell cellInvoiceHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellInvoiceHead.setCellValue("Счет");
        cellInvoiceHead.setCellStyle(headStyle);

        Cell cellPartSumHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPartSumHead.setCellValue("Платеж");
        cellPartSumHead.setCellStyle(headStyle);

        Cell cellPartSumPaidHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPartSumPaidHead.setCellValue("Оплачено");
        cellPartSumPaidHead.setCellStyle(headStyle);

        Cell cellPercentHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellPercentHead.setCellValue("%");
        cellPercentHead.setCellStyle(headStyle);

        Cell cellProvisionHead = dataDescrRow.createCell(dataDescrcCellnum++);
        cellProvisionHead.setCellValue("Пров.");
        cellProvisionHead.setCellStyle(headStyle);

        Cell cellReportDateHead = dataDescrRow.createCell(dataDescrcCellnum);
        cellReportDateHead.setCellValue("Отчет");
        cellReportDateHead.setCellStyle(headStyle);

        for (PolicyPart part : parts) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;

            Cell cellPolicyType = row.createCell(cellnum++);
            cellPolicyType.setCellValue(part.getPolicy().getPolicyType().getType());

            Cell cellClient = row.createCell(cellnum++);
            cellClient.setCellValue(part.getPolicy().getClient().getName());

            Cell cellObject = row.createCell(cellnum++);
            cellObject.setCellValue(part.getPolicy().getObject());

            Cell cellInsurer = row.createCell(cellnum++);
            cellInsurer.setCellValue(part.getPolicy().getInsurer().getName());

            Cell cellAgent = row.createCell(cellnum++);
            cellAgent.setCellValue(part.getPolicy().getAgent().getPrefix());

            Cell cellPolicy = row.createCell(cellnum++);
            cellPolicy.setCellValue(part.getPolicy().getPolicyNumber());

            Cell cellPolicyStart = row.createCell(cellnum++);
            if (part.getPolicy().getStartDate() != null) {
                cellPolicyStart.setCellValue(part.getPolicy().getStartDate().format(formatter));
            }

            Cell cellPolicyEnd = row.createCell(cellnum++);
            if (part.getPolicy().getEndDate() != null) {
                cellPolicyEnd.setCellValue(part.getPolicy().getEndDate().format(formatter));
            }

            Cell cellTotalParts = row.createCell(cellnum++);
            cellTotalParts.setCellValue(part.getPolicy().getParts());

            Cell cellPart = row.createCell(cellnum++);
            cellPart.setCellValue(part.getPart());

            Cell cellPolicySum = row.createCell(cellnum++);
            cellPolicySum.setCellValue(part.getPolicy().getSum());

            Cell cellPaymentType = row.createCell(cellnum++);
            cellPaymentType.setCellValue(part.getPolicy().getPaymentType().getPrefix());

            Cell cellPartDate = row.createCell(cellnum++);
            if (part.getDate() != null) {
                cellPartDate.setCellValue(part.getDate().format(formatter));
            }

            Cell cellPartDatePaid = row.createCell(cellnum++);
            if (part.getDatePaid() != null) {
                cellPartDatePaid.setCellValue(part.getDatePaid().format(formatter));
            }

            Cell cellInvoice = row.createCell(cellnum++);
            if(part.getInvoice() != null){
                cellInvoice.setCellValue(part.getInvoice().getInvoiceNumber());
            }

            Cell cellPartSum = row.createCell(cellnum++);
            cellPartSum.setCellValue(part.getSum());

            Cell cellPartSumPaid = row.createCell(cellnum++);
            if(part.getSumReal() != null){
                cellPartSumPaid.setCellValue(part.getSumReal());
            }

            Cell cellPercent = row.createCell(cellnum++);
            cellPercent.setCellValue(part.getPolicy().getPercent().doubleValue());

            Cell cellProvision = row.createCell(cellnum++);
            cellProvision.setCellValue(part.getSum() / 100 * part.getPolicy().getPercent().doubleValue());

            Cell cellReportDate = row.createCell(cellnum);
            if (part.getDateConfirmed() != null) {
                cellReportDate.setCellValue(part.getDateConfirmed().format(formatter));
            }
        }

        for (int i = 0; i < 20; i++) {
            sheet.autoSizeColumn(i, true);
        }

        try {
            FileOutputStream out = new FileOutputStream(new File(appConfig.getBaseDirectory() + "/report.xlsx"));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cmds[] = new String[]{"cmd", "/c", appConfig.getBaseDirectory() + "/report.xlsx"};
        try {
            Runtime.getRuntime().exec(cmds);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}