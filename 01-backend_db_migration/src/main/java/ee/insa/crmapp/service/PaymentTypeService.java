package ee.insa.crmapp.service;

import ee.insa.crmapp.model.PaymentType;
import ee.insa.crmapp.repository.PaymentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentTypeService {
    private final PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public PaymentTypeService(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public PaymentType save(PaymentType paymentType) {
        return paymentTypeRepository.save(paymentType);
    }

    public PaymentType findById(Long id) {
        return paymentTypeRepository.findById(id).orElse(null);
    }

    public List<PaymentType> findAll(){
        return paymentTypeRepository.findAll();
    }

    public PaymentType findByPrefix(String name) {
        return paymentTypeRepository.findByPrefix(name);
    }
}