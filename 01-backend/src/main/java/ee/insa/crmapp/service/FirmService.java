package ee.insa.crmapp.service;

import ee.insa.crmapp.model.Firm;
import ee.insa.crmapp.repository.FirmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirmService {
    private final FirmRepository firmRepository;

    @Autowired
    public FirmService(FirmRepository firmRepository) {
        this.firmRepository = firmRepository;
    }

    public Firm save(Firm firm) {
        return firmRepository.save(firm);
    }
    public Firm findById(Long id) {
        return firmRepository.findById(id).orElse(null);
    }
}
