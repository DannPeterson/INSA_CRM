package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.PaymentType;
import ee.insa.crmapp.service.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"/payment-type"})
public class PaymentTypeResource {
    private final PaymentTypeService paymentTypeService;

    @Autowired
    public PaymentTypeResource(PaymentTypeService paymentTypeService){
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/all")
    public List<PaymentType> findAll(){
        return paymentTypeService.findAll();
    }
}
