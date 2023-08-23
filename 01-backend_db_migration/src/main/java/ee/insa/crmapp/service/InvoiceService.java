package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Invoice;
import ee.insa.crmapp.model.PolicyPart;
import ee.insa.crmapp.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InsurerService insurerService;
    private final FileService fileService;
    private final PdfGeneratorService pdfGeneratorService;
    private final PolicyPartService policyPartService;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository,
                          InsurerService insurerService,
                          FileService fileService,
                          PdfGeneratorService pdfGeneratorService,
                          PolicyPartService policyPartService) {
        this.invoiceRepository = invoiceRepository;
        this.insurerService = insurerService;
        this.fileService = fileService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.policyPartService = policyPartService;
    }

    public Invoice save(Invoice invoice, Long policyPartId) {
        PolicyPart policyPart = policyPartService.findById(policyPartId);
        if(invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()){
            invoice.setInvoiceNumber(insurerService.getAndIncrementInvoiceNumber(policyPart.getPolicy().getInsurer().getId()));
        }

        invoice = invoiceRepository.save(invoice);
        System.out.println("<<< INSERTED INVOICE ID: " + invoice.getId());
        policyPart.setInvoice(invoice);
        policyPartService.save(policyPart);

        fileService.createDirectory(policyPart.getPolicy().getFolder());

        File pdfFile = pdfGeneratorService.generateInvoicePdf(policyPart);
        fileService.openFileInExplorer(pdfFile);
        return invoice;
    }

    public Invoice findById(Long id) {return invoiceRepository.findById(id).orElse(null);}

    public void deleteById(Long id) {invoiceRepository.deleteById(id);}

    @Transactional
    public void insertInvoiceWithId(Invoice invoice) {
        invoiceRepository.insertInvoiceWithId(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getText(),
                invoice.getConclusionDate(),
                invoice.getMaxDate(),
                invoice.getPaidDate()
        );
    }
}