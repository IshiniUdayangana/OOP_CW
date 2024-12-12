package edu.iit.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")

public class VendorService {

    @Autowired
    private TicketService ticketService;

    private int ticketsReleased = 0;
    private String vendorId;
    private boolean isDoneReleasing = false;

    @Async
    public void startVendor(String vendorId, int totalTickets, int ticketReleaseRate) {
        while(ticketsReleased <= totalTickets){
            for(int i = 0;i < ticketReleaseRate; i++){
                if(ticketsReleased == totalTickets){
                    System.out.println(vendorId + " is done releasing tickets");
                    isDoneReleasing = true;
                    ticketsReleased++;
                    break;
                }

                try {
                    String ticket = vendorId + "-Ticket-"+ System.currentTimeMillis();
                    ticketService.addTicket(ticket);
                    ticketsReleased++;
                    System.out.println("Tickets in the pool: " + ticketService.ticketsInThePool());
                    System.out.println(vendorId +" released: "+ ticket );
                    System.out.println(vendorId+"--> total released tickets: " + ticketsReleased);
                    Thread.sleep(1000/ticketReleaseRate);

                } catch (InterruptedException e) {
                    System.out.println("Vendor interrupted while adding tickets");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        ticketService.setTicketProductionComplete();

    }

}
