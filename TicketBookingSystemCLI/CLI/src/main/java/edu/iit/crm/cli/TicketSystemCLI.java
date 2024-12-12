package edu.iit.crm.cli;

import edu.iit.crm.models.LogsContainer;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TicketSystemCLI {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Real-time Ticketing System!!!");
        LogsContainer.logMessage("Starting system configuration....");

        //System Configuration
        int numberOfVendors = inputValidation("Enter the number of vendors: ",scanner);

        int totalTickets = inputValidation("Enter the total number of tickets: ",scanner);

        int ticketReleaseRate = inputValidation("Enter the ticket release rate (Number of releasing tickets per second): ",scanner);

        int customerRetrievalRate = inputValidation("Enter the customer retrieval rate (Number of buying tickets per second): ",scanner);

        int maxTicketCapacity = inputValidation("Enter the maximum ticket capacity: ",scanner);

        //Passing the parameters to the configDetails object
        ConfigurationDetails configDetails = new ConfigurationDetails(totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);

        LogsContainer.logMessage("System configuration successfully completed...");

        Thread menuThread = new Thread(()-> runMenu(configDetails, numberOfVendors));
        menuThread.start();

        //Save configuration details to a file
        configDetails.saveConfiguration("files/configuration.json", configDetails);
        System.out.println();


    }

    public static void runMenu(ConfigurationDetails configDetails, int numberOfVendors){
        Scanner scanner = new Scanner(System.in);
        //Creation of TicketSystemCommands object to execute the system commands
        TicketSystemCommands systemCommands = new TicketSystemCommands(configDetails);
        while(true){
            try {
                System.out.println("Choose one of the below commands to interact with the system...");
                System.out.println("1. Start the system");
                System.out.println("2. Show system status");
                System.out.println("3. Stop the system");
                System.out.println("4. Exit");

                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        systemCommands.startSystem(numberOfVendors);
                        break;
                    case 2:
                        systemCommands.showStatus();
                        break;
                    case 3:
                        systemCommands.stopSystem();
                        break;
                    case 4:
                        System.out.println("Exiting the system");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input. Please try again");
                }
            }catch (Exception e){
                System.out.println("Please enter a numeric value!");
                scanner.next();
            }
        }
    }

    //Method to check if the user enters positive integers for system configuration
    public static int inputValidation(String prompt,Scanner scanner){
        while(true) {
            try{
                System.out.print(prompt);
                int input = scanner.nextInt();
                scanner.nextLine();

                if(input > 0){
                    return input;
                }
                System.out.println("Invalid input. Please enter a positive integer");

            }
            catch(NumberFormatException | InputMismatchException e){
                System.out.println("Invalid input. Please try again and enter a number");
                scanner.nextLine();
            }

        }
    }
}
