package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Bank;
import ee.insa.crmapp.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"api/bank"})
public class BankResource {
    private final BankService bankService;

    @Autowired
    public BankResource(BankService bankService){
        this.bankService = bankService;
    }

    @GetMapping("/all")
    public List<Bank> findAll(){
        return bankService.findAll();
    }
}
