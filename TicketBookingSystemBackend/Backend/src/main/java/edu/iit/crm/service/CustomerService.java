package edu.iit.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private TicketService ticketService;


    @Async
    public void startCustomer(String customerId, int customerRetrievalRate){
        try {
            while(!Thread.currentThread().isInterrupted()){
                String purchasedTicket = ticketService.removeTicket(); //Stores the removed ticket
                if(purchasedTicket != null){
                    System.out.println(customerId +" purchased: "+ purchasedTicket);
                }
                else{
                    break;
                }
                Thread.sleep(1000/customerRetrievalRate);
            }

        }
        catch (InterruptedException e) {
            System.out.println("Customer interrupted while purchasing the tickets");
            Thread.currentThread().interrupt();
        }
//        while (!Thread.currentThread().isInterrupted() && !ticketService.areTicketsSoldOut()) {
//            String ticket = ticketService.removeTicket();
//            if (ticket != null) {
//                System.out.println(customerId + " purchased: " + ticket);
//            } else {
//                try {
//                    Thread.sleep(100); // Pause briefly before retrying
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break; // Exit the loop on interruption
//                }
//            }
//        }




    }

}
