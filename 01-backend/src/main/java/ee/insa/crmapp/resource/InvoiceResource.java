package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Invoice;
import ee.insa.crmapp.model.PolicyPart;
import ee.insa.crmapp.service.FileService;
import ee.insa.crmapp.service.InsurerService;
import ee.insa.crmapp.service.InvoiceService;
import ee.insa.crmapp.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping(path = {"/invoice"})
public class InvoiceResource {
    private InvoiceService invoiceService;

    @Autowired
    public InvoiceResource(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/{policyPartId}/createInvoice")
    public Invoice createInvoice(@PathVariable("policyPartId") Long policyPartId, @RequestBody Invoice invoice) {
        if(invoice.getId() == 0) invoice.setId(null);
        return invoiceService.save(invoice, policyPartId);
    }
}
