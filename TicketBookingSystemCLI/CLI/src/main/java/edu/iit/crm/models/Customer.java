package edu.iit.crm.models;

public class Customer implements Runnable {
    private TicketPool ticketpool;
    private int customerRetrievalRate;
    private String customerID;
    private static int doneVendorCount;

    public Customer(TicketPool ticketpool, int customerRetrievalRate, String customerID){
        this.ticketpool = ticketpool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.customerID = customerID;
    }

    public void run(){
        try {
            while(!Thread.currentThread().isInterrupted()){
                String purchasedTicket = ticketpool.removeTicket(); //Stores the removed ticket
                if(purchasedTicket != null){
                    LogsContainer.logMessage(customerID +" purchased: "+ purchasedTicket);
                }
                else{
                    System.out.println("Tickets are sold out for this event");
                    LogsContainer.logMessage("Tickets are sold out for this event");
                    break;
                }
                Thread.sleep(1000/customerRetrievalRate);


//                for(Vendor vendor : TicketSystemCommands.getVendors()){
//                    if(vendor.isDoneReleasing()){
//                        doneVendorCount++;
//                    }
//                }
//
//                if(doneVendorCount == 4 && ticketpool.getTicketCount() == 0){
//                    System.out.println("Tickets are sold out for this event.");
//                    for(Thread customerThread : TicketSystemCommands.getCustomerThreads()){
//                        customerThread.interrupt();
//                    }
//                }
            }

        }
        catch (InterruptedException e) {
            LogsContainer.logMessage("Customer interrupted while purchasing the tickets");
            Thread.currentThread().interrupt();
        }
    }

    public TicketPool getTicketpool() {

        return ticketpool;
    }

    public int getCustomerRetrievalRate() {

        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public static int getDoneVendorCount() {
        return doneVendorCount;
    }
}
