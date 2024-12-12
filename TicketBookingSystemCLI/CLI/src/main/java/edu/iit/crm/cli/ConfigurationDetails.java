package edu.iit.crm.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationDetails {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    public ConfigurationDetails(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity){
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public void saveConfiguration(String filename, ConfigurationDetails configDetails){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try{
            List<ConfigurationDetails> configurationList;
            File file = new File(filename);
            if(file.exists() && file.length() > 0){
                try(FileReader fileReader = new FileReader(filename)){
                    Type type = new TypeToken<ArrayList<ConfigurationDetails>>() {}.getType();
                    configurationList = gson.fromJson(fileReader, type);
                }
            }
            else{
                configurationList = new ArrayList<>();
            }
            configurationList.add(configDetails);

            try(FileWriter fileWriter = new FileWriter(filename)){
                gson.toJson(configurationList, fileWriter);
            }
        }
        catch (IOException e) {
            System.out.println("Error occurred while creating the file");
        }
    }

    public ConfigurationDetails loadConfiguration(String filename, Gson gson){
        try{
            FileReader fileReader = new FileReader(filename);
            return gson.fromJson(fileReader, ConfigurationDetails.class);

        }
        catch (FileNotFoundException e) {
            System.out.println("File cannot be found");
        }
        return null;
    }



    public int getTotalTickets() {

        return totalTickets;
    }

//    public void setTotalTickets(int totalTickets) {
//
//        this.totalTickets = totalTickets;
//    }

    public int getTicketReleaseRate() {

        return ticketReleaseRate;
    }

//    public void setTicketReleaseRate(int ticketReleaseRate) {
//
//        this.ticketReleaseRate = ticketReleaseRate;
//    }

    public int getCustomerRetrievalRate() {

        return customerRetrievalRate;
    }

//    public void setCustomerRetrievalRate(int customerRetrievalRate) {
//        this.customerRetrievalRate = customerRetrievalRate;
//    }

    public int getMaxTicketCapacity() {

        return maxTicketCapacity;
    }

//    public void setMaxTicketCapacity(int maxTicketCapacity) {
//
//        this.maxTicketCapacity = maxTicketCapacity;
//    }
}
