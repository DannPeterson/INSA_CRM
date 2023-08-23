package ee.insa.crmapp.resource;

import ee.insa.crmapp.model.InsurerTypeProfitPercent;
import ee.insa.crmapp.service.InsurerTypeProfitPercentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"/percent"})
public class PercentResource {
    private final InsurerTypeProfitPercentService percentService;

    @Autowired
    public PercentResource(InsurerTypeProfitPercentService percentService){
        this.percentService = percentService;
    }

    @RequestMapping("/all")
    public List<InsurerTypeProfitPercent> findAll(){
        return percentService.findAll();
    }

    @PostMapping("/update")
    public InsurerTypeProfitPercent save(@RequestBody InsurerTypeProfitPercent insurerTypeProfitPercent){
        return percentService.save(insurerTypeProfitPercent);
    }
}
