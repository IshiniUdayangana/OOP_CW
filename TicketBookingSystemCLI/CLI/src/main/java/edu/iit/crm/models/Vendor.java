package edu.iit.crm.models;

public class Vendor implements Runnable{
    private TicketPool ticketPool;
    private int ticketReleaseRate;
    private String vendorID;
    private int totalTickets;
    private int ticketsReleased = 0;
    private boolean isDoneReleasing = false;

    public Vendor(TicketPool ticketPool, String vendorID, int ticketReleaseRate, int totalTickets){
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
        this.vendorID = vendorID;
        this.totalTickets = totalTickets;
    }

    @Override
    public void run(){
        while(ticketsReleased <= totalTickets){
            for(int i = 0;i < ticketReleaseRate; i++){
                if(ticketsReleased == totalTickets){
                    System.out.println(vendorID + " is done releasing tickets");
                    isDoneReleasing = true;
                    ticketsReleased++;
                    break;
                }

                try {
                    String ticket = vendorID + "-Ticket-"+ System.currentTimeMillis();
                    ticketPool.addTicket(ticket);
                    ticketsReleased++;
                    LogsContainer.logMessage(vendorID +" released: "+ ticket );
                    Thread.sleep(1000/ticketReleaseRate);

                } catch (InterruptedException e) {
                    LogsContainer.logMessage("Vendor interrupted while adding tickets");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        ticketPool.setTicketProductionComplete();

    }

    public int getTicketsReleased() {
        return ticketsReleased;
    }

    public String getVendorID() {

        return vendorID;
    }


}
