package edu.iit.crm.service;

import edu.iit.crm.util.FileUtil;
import edu.iit.crm.entity.ConfigurationDetails;
import edu.iit.crm.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service


public class ConfigurationService {

    private static String configFile = "config.txt";
    private List<ConfigurationDetails> configurations = new ArrayList<>();


    //Save configurations to a file
    public void saveConfiguration(ConfigurationDetails configDetails){
        configurations.add(configDetails);
        try{
            FileUtil.saveConfigurationToFile(configFile, configurations);
        } catch (IOException e) {
            System.out.println("Error occurred while saving to file" + e);

        }
    }


    //Load configurations from a file
    public List<ConfigurationDetails> loadCofigurations(){
        try{
            configurations = FileUtil.loadConfigurationFromFile(configFile);
        } catch (IOException e) {
            System.out.println("Error occurred while loading from file");
        }
        return configurations;
    }

}
