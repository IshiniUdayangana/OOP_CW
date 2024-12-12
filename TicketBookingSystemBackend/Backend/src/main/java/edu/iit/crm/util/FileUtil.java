package edu.iit.crm.util;

import edu.iit.crm.entity.ConfigurationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void saveConfigurationToFile(String filename, List<ConfigurationDetails> configurations) throws IOException{
        List<ConfigurationDetails> existingDetails = new ArrayList<>();
        File file = new File(filename);
        if(file.exists() && file.length() > 0){
            existingDetails = loadConfigurationFromFile(filename);
        }
        else{
            System.out.println("File connot be found");
        }

        existingDetails.addAll(configurations);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(existingDetails);
            writer.write(jsonString);

        }
    }

    public static List<ConfigurationDetails> loadConfigurationFromFile(String filename) throws IOException{
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            StringBuilder json = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                json.append(line);
            }
            return objectMapper.readValue(json.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, ConfigurationDetails.class));
        }
    }

}
