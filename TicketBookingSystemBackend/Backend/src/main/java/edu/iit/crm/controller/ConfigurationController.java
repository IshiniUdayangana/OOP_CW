package edu.iit.crm.controller;

import edu.iit.crm.entity.ConfigurationDetails;
import edu.iit.crm.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configurations")

public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @PostMapping
    public String saveConfiguration(@RequestBody ConfigurationDetails configDetails){
        configurationService.saveConfiguration(configDetails);
        return "Configuration save successfully";
    }

    @GetMapping("/all")
    public List<ConfigurationDetails> getAllConfigurations(){
        return configurationService.loadCofigurations();
    }

}
