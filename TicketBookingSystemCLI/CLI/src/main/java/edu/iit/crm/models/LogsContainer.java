package edu.iit.crm.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogsContainer {

    public static void logMessage(String message){
        String messageWithTime = LocalDateTime.now() + ": " + message;
        System.out.println(messageWithTime);
        String logsFile = "files/logs.txt";
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(logsFile,true));
            writer.write(messageWithTime);
            writer.newLine();
            writer.close();
        }
        catch(IOException e){
            System.out.println("Error occurred while logging messages to the file");
        }
    }

}
