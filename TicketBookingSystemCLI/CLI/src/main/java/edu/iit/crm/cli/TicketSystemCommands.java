package edu.iit.crm.cli;

import edu.iit.crm.models.Customer;
import edu.iit.crm.models.LogsContainer;
import edu.iit.crm.models.TicketPool;
import edu.iit.crm.models.Vendor;

import java.util.ArrayList;
import java.util.List;

public class TicketSystemCommands {

    private ConfigurationDetails configDetails;
    private boolean isRunning;
    private TicketPool ticketPool;
    private static List<Thread> customerThreads = new ArrayList<>();
    private static List<Thread> vendorThreads = new ArrayList<>();
    private static List<Vendor> vendors = new ArrayList<>();
    private static int doneVendorCount;

    public TicketSystemCommands(ConfigurationDetails configDetails){
        this.configDetails = configDetails;
        this.isRunning = false;
        ticketPool = new TicketPool(configDetails.getMaxTicketCapacity());
    }

    public ConfigurationDetails getConfigDetails() {

        return configDetails;
    }

//    public void setConfigDetails(ConfigurationDetails configDetails) {
//
//        this.configDetails = configDetails;
//    }

    public boolean isRunning() {

        return isRunning;
    }

    public void setRunning(boolean running) {

        isRunning = running;
    }

    public TicketPool getTicketPool() {

        return ticketPool;
    }

//    public void setTicketPool(TicketPool ticketPool) {
//
//        this.ticketPool = ticketPool;
//    }

    public void startSystem(int numberOfVendors) {

        if (isRunning) {
            System.out.println("System has started running already");
            return;
        }

        LogsContainer.logMessage("Starting the system...");
        //Logic to start the system

        vendorThreads.clear();
        customerThreads.clear();
        vendors.clear();

        //Add vendor threads to the list
        for (int i = 0; i < numberOfVendors; i++) {
            Vendor vendor = new Vendor(ticketPool, "Vendor-" + (i + 1), configDetails.getTicketReleaseRate(), configDetails.getTotalTickets());
            vendors.add(vendor);
            Thread vendorThread = new Thread(vendor);
            vendorThreads.add(vendorThread);
        }

        //Add customer threads to the list
        for (int i = 0; i < 2; i++) {
            Customer customer = new Customer(ticketPool, configDetails.getCustomerRetrievalRate(), "Customer-" + (i + 1));
            Thread customerThread = new Thread(customer);
            customerThreads.add(customerThread);
        }
        isRunning = true;

        //Start vendor threads
        for (Thread vendorThread : vendorThreads) {
            vendorThread.start();
        }
        //Start customer threads
        for (Thread customerThread : customerThreads) {
            customerThread.start();
        }

        //Wait for vendor threads to finish
        try {
            for (Thread vendorThread : vendorThreads) {
                vendorThread.join();
                ticketPool.setTicketProductionComplete();
            }

        }
        catch (InterruptedException e) {
            System.out.println("Vendor thread was interrupted while waiting to finish");;
        }

        // Monitor ticket pool until tickets are sold out
        synchronized (ticketPool) {
            while (!ticketPool.areTicketsSoldOut()) {
                try {
                    ticketPool.wait(200); // Wait for updates on the ticket pool
                } catch (InterruptedException e) {
                    LogsContainer.logMessage("System interrupted while monitoring ticket pool");
                }
            }
        }


        //Interrupt customer threads
        for(Thread customerThread : customerThreads){
            customerThread.interrupt();
        }

        //Wait for the customer threads to finish
        for(Thread customerThread : customerThreads){
            try{
                customerThread.join();
            }
            catch(InterruptedException e){
                LogsContainer.logMessage("Customer thread was interrupted while waiting to finish");
            }
        }

        isRunning = true; //System stops running
        System.out.println("Tickets are sold out for this event...");

    }




    public void showStatus(){
        //Logic to display the system status
        System.out.println("Displaying status...");
        System.out.println("System status: " + isRunning);
        System.out.println("Number of tickets in the pool: " + ticketPool.getTicketCount());
        System.out.println("How many vendors have released tickets: " + vendors.size());
        System.out.println("How many tickets were released by the vendors: " + ticketPool.getTotalTicketsReleased());
        System.out.println("Number of released tickets per each vendor: ");
        for(Vendor vendor : vendors){
            System.out.println(vendor.getVendorID()+" released: "+ (vendor.getTicketsReleased()-1));
        }
    }

    public void stopSystem(){
        if(!isRunning){
            System.out.println("System hasn't started running yet");
            return;
        }
        else{
            for(Thread customerThread : customerThreads){
                try{
                    customerThread.join();
                }
                catch(InterruptedException e){
                    LogsContainer.logMessage("Customer thread was interrupted while waiting to finish");
                }
            }

            try {
                for (Thread vendorThread : vendorThreads) {
                    vendorThread.join();
                }

            }
            catch (InterruptedException e) {
                System.out.println("Vendor thread was interrupted while waiting to finish");;
            }
        }

        System.out.println("Stopping the system...");
        //Logic to stop the system
        isRunning = false;

    }

    public static List<Thread> getCustomerThreads() {
        return customerThreads;
    }

    public static List<Thread> getVendorThreads() {
        return vendorThreads;
    }

    public static List<Vendor> getVendors() {
        return vendors;
    }

    public static int getDoneVendorCount() {
        return doneVendorCount;
    }

}
