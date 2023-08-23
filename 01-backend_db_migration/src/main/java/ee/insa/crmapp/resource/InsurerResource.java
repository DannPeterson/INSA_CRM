package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.Insurer;
import ee.insa.crmapp.service.InsurerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"/insurer"})
public class InsurerResource {
    private InsurerService insurerService;

    @Autowired
    public InsurerResource(InsurerService insurerService){
        this.insurerService = insurerService;
    }

    @GetMapping("/all")
    public List<Insurer> findAll(){
        return insurerService.findAll();
    }

    @PostMapping()
    public Insurer save(@RequestBody Insurer insurer){
        return insurerService.save(insurer);
    }

}
