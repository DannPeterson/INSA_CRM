package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Insurer;
import ee.insa.crmapp.repository.InsurerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class InsurerService {
    private final InsurerRepository insurerRepository;

    @Autowired
    public InsurerService(InsurerRepository insurerRepository) {
        this.insurerRepository = insurerRepository;
    }

    public Insurer save(Insurer insurer) {
        return insurerRepository.save(insurer);
    }

    public Insurer findById(Long id) {
        return insurerRepository.findById(id).orElse(null);
    }

    public List<Insurer> findAll(){
        return insurerRepository.findAll();
    }

    public Insurer findByName(String name) {
        return insurerRepository.findByName(name);
    }

    @Transactional
    public String getAndIncrementInvoiceNumber(Long insurerId) {
        Optional<Insurer> insurerOpt = insurerRepository.findById(insurerId);

        if (!insurerOpt.isPresent()) {
            throw new NoSuchElementException("No insurer found with id: " + insurerId);
        }

        Insurer insurer = insurerOpt.get();
        String invoicePrefix = insurer.getInvoicePrefix();
        int invoiceNumber = Integer.parseInt(invoicePrefix.substring(1));

        // Increment the invoice number
        invoiceNumber++;

        // Update the invoice prefix in the database
        String newInvoicePrefix = invoicePrefix.substring(0, 1) + String.format("%06d", invoiceNumber);
        insurer.setInvoicePrefix(newInvoicePrefix);
        insurerRepository.save(insurer);

        return newInvoicePrefix;
    }
}
