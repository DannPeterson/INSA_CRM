package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Bank;
import ee.insa.crmapp.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {
    private final BankRepository bankRepository;

    @Autowired
    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public Bank save(Bank bank) {
        return bankRepository.save(bank);
    }
    public Bank findById(Long id) {
        return bankRepository.findById(id).orElse(null);
    }

    public List<Bank> findAll(){
        return bankRepository.findAll();
    }
}
