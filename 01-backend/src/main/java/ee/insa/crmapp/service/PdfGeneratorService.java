package ee.insa.crmapp.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import ee.insa.crmapp.SumToEstWordsConverter;
import ee.insa.crmapp.configuration.AppConfig;
import ee.insa.crmapp.model.PolicyPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {
    private AppConfig appConfig;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private PolicyService policyService;

    @Autowired
    public PdfGeneratorService(AppConfig appConfig, PolicyService policyService) {
        this.appConfig = appConfig;
        this.policyService = policyService;
    }

    public File generateInvoicePdf(PolicyPart part) {
        File pdfFile = new File(appConfig.getBaseDirectory() + "/" + part.getPolicy().getFolder() + "/" + part.getInvoice().getInvoiceNumber() + ".pdf");


        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);

            // Add invoice header, customer details, and other necessary information
            addInvoiceHeader(document, part);
//            addCustomerDetails(document, invoice);
//            addInvoiceItems(document, invoice);

            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return pdfFile;
    }

    private void addInvoiceHeader(Document document, PolicyPart part) throws IOException {
        Table topTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        Cell cellInvoiceNumber = new Cell().add(new Paragraph("Arve nr " + part.getInvoice().getInvoiceNumber()).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14));
        Cell cellInvoiceDate = new Cell().add(new Paragraph("Kuupäev: " + formatter.format(part.getInvoice().getConclusionDate())).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12));

        cellInvoiceNumber.setBorder(Border.NO_BORDER);
        cellInvoiceNumber.setBorderBottom(new SolidBorder(1));
        cellInvoiceDate.setBorder(Border.NO_BORDER);

        topTable.addCell(cellInvoiceNumber);
        topTable.addCell(cellInvoiceDate);


        Table firmDataTable = new Table(UnitValue.createPercentArray(new float[]{1.2f, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.LEFT)
                .setFixedLayout();

        Cell cellFirmName = new Cell(1, 2)
                .add(new Paragraph(
                        part.getPolicy().getInsurer().getFirm().getName()
                )
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmCodeLine = new Cell()
                .add(new Paragraph("Registrikood:")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmCodeValue = new Cell()
                .add(new Paragraph(
                        part.getPolicy().getInsurer().getFirm().getCode()
                )
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmAddressLine = new Cell()
                .add(new Paragraph("Aadress:")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmAddressValue = new Cell()
                .add(new Paragraph(
                        part.getPolicy().getInsurer().getFirm().getAddress()
                )
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmPostAddressLine = new Cell()
                .add(new Paragraph("Posti aadress:")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmPostAddressValue = new Cell()
                .add(new Paragraph(
                        part.getPolicy().getInsurer().getFirm().getAddress()
                )
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmTelFaxLine = new Cell()
                .add(new Paragraph("Telefon, Fax:")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmTelFaxValue = new Cell()
                .add(new Paragraph(
                        part.getPolicy().getInsurer().getFirm().getPhone() + ", " +
                                part.getPolicy().getInsurer().getFirm().getFax())
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmBankLine = new Cell()
                .add(new Paragraph("Konto Swedbank")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellFirmBankValue = new Cell()
                .add(new Paragraph(
                        part.getPolicy().getInsurer().getFirm().getBankAccount()
                )
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        firmDataTable.addCell(cellFirmName);
        firmDataTable.addCell(cellFirmCodeLine);
        firmDataTable.addCell(cellFirmCodeValue);
        firmDataTable.addCell(cellFirmAddressLine);
        firmDataTable.addCell(cellFirmAddressValue);
        firmDataTable.addCell(cellFirmPostAddressLine);
        firmDataTable.addCell(cellFirmPostAddressValue);
        firmDataTable.addCell(cellFirmTelFaxLine);
        firmDataTable.addCell(cellFirmTelFaxValue);
        firmDataTable.addCell(cellFirmBankLine);
        firmDataTable.addCell(cellFirmBankValue);


        Table clientDataTable = new Table(UnitValue.createPercentArray(new float[]{1.2f, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.LEFT)
                .setFixedLayout();
        clientDataTable.setMarginTop(20f);

        Cell cellClientName = new Cell(1, 2)
                .add(new Paragraph("Maksja:").setFont(
                        PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14))
                .setBorder(Border.NO_BORDER);
        Cell cellClientNameLine = new Cell()
                .add(new Paragraph("Nimi:").setFont(
                        PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);
        Cell cellClientNameValue = new Cell()
                .add(new Paragraph(part.getPolicy().getClient().getName()).setFont(
                        PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);
        Cell cellClientRegCodeLine = new Cell()
                .add(new Paragraph("Reg.kood:").setFont(
                        PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);
        Cell cellClientRegCodeValue = new Cell()
                .add(new Paragraph(part.getPolicy().getClient().getCode()).setFont(
                        PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);
        Cell cellClientAddressLine = new Cell()
                .add(new Paragraph("Aadress:").setFont(
                        PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                .setBorder(Border.NO_BORDER);

        Cell cellClientAddressValue = new Cell();

        if (part.getPolicy().getClient().getAddress() != null){
            cellClientAddressValue
                    .add(new Paragraph(part.getPolicy().getClient().getAddress()).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                    .setBorder(Border.NO_BORDER);
        } else {
            cellClientAddressValue
                    .add(new Paragraph("").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12))
                    .setBorder(Border.NO_BORDER);
        }

        clientDataTable.addCell(cellClientName);
        clientDataTable.addCell(cellClientNameLine);
        clientDataTable.addCell(cellClientNameValue);
        clientDataTable.addCell(cellClientRegCodeLine);
        clientDataTable.addCell(cellClientRegCodeValue);
        clientDataTable.addCell(cellClientAddressLine);
        clientDataTable.addCell(cellClientAddressValue);


        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 15, 2, 4}))
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.LEFT)
                .setFixedLayout();
        table.setMarginTop(20f);

        Cell cell1 = new Cell().add(new Paragraph("N")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell cell2 = new Cell().add(new Paragraph("Nimetus")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell cell3 = new Cell().add(new Paragraph("Kogus")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell cell4 = new Cell().add(new Paragraph("    Maksumus\n         EUR")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell cell5 = new Cell(1, 4).add(new Paragraph(part.getPolicy().getInsurer().getName() + ", Kindlustuse poliis")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));

        Cell cellNumber = new Cell().add(new Paragraph("1")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell policyDataCell = new Cell().add(new Paragraph(part.getInvoice().getText())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell amountCell = new Cell().add(new Paragraph("1")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));
        Cell sumCell = new Cell().add(new Paragraph("   "  + part.getSum().toString())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));

        Cell cell6 = new Cell().setBorder(Border.NO_BORDER);
        Cell cell7 = new Cell().setBorder(Border.NO_BORDER);
        Cell cell8 = new Cell(1, 2).add(new Paragraph("Summa: " + part.getSum().toString() + "\n ")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12));

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.addCell(cellNumber);
        table.addCell(policyDataCell);
        table.addCell(amountCell);
        table.addCell(sumCell);
        table.addCell(cell6);
        table.addCell(cell7);
        table.addCell(cell8);


        document.add(topTable);
        document.add(firmDataTable);
        document.add(clientDataTable);
        document.add(table);


        BigDecimal tempBig = new BigDecimal(Double.toString(part.getSum()));
        int centsTotal = tempBig.movePointRight(2).intValueExact();
        Integer euros = part.getSum().intValue();
        Integer cents = centsTotal - euros * 100;

        String sumToWord = SumToEstWordsConverter.convert(part.getSum()) + " eur ja " + cents + " senti";

        Paragraph invoiceObjectLine = new Paragraph("Arve esitamise alus:").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12);

        Paragraph sumWithWordsPhrase = new Paragraph("Summa sõnadega:").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12);
        Paragraph sumToWordLine = new Paragraph(sumToWord).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14);
        Paragraph dateMaxLine = new Paragraph("\n\nMakse tähtaeg: " + formatter.format(part.getInvoice().getMaxDate())).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12);
        Paragraph invoiceMakerLine = new Paragraph("Arve koostaja:   Viktor Peterson").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(12);

        document.add(invoiceObjectLine);
        document.add(sumWithWordsPhrase);
        document.add(sumToWordLine);
        document.add(dateMaxLine);
        document.add(invoiceMakerLine);
    }
}
