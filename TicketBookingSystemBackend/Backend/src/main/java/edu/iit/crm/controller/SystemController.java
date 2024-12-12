package edu.iit.crm.controller;

import edu.iit.crm.entity.ConfigurationDetails;
import edu.iit.crm.service.*;
import edu.iit.crm.util.FileUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/system")

public class SystemController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private VendorService vendor;

    @Autowired
    private CustomerService customer;

    @Autowired
    private SystemStatusService systemStatusService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    ConfigurationService configurationService;




    private List<Thread> vendorThreads = new ArrayList<>();
    private List<Thread> customerThreads = new ArrayList<>();

    @PostMapping("/start")
    public ResponseEntity<?> startSystem(@RequestParam int vendorsNum, @RequestBody ConfigurationDetails configurationDetails){
        int totalTickets = configurationDetails.getTotalTickets();
        int ticketReleaseRate = configurationDetails.getTicketReleaseRate();
        int customerRetrievalRate = configurationDetails.getCustomerRetrievalRate();
        int maxTicketCapacity = configurationDetails.getMaxTicketCapacity();

        configurationService = new ConfigurationService();
        configurationService.saveConfiguration(configurationDetails);

        System.out.println("Total Tickets: " + totalTickets);
        System.out.println("Ticket Release Rate: " + ticketReleaseRate);
        System.out.println("Customer Retrieval Rate: " + customerRetrievalRate);
        System.out.println("Max Ticket Capacity: " + maxTicketCapacity);


        try {
            if (systemStatusService.isRunning()) {
                System.out.println("System is already running...");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("System is already running");
            }
            systemStatusService.startSystem();
            System.out.println("Starting the System...");

            vendorThreads.clear();
            customerThreads.clear();

            for (int i = 0; i < vendorsNum; i++) {
                VendorService vendor = context.getBean(VendorService.class);
                int finalI = i;
                Thread vendorThread = new Thread(() -> vendor.startVendor("Vendor-" + (finalI + 1), configurationDetails.getTotalTickets(), configurationDetails.getTicketReleaseRate()));
                vendorThreads.add(vendorThread);
            }

            for (int i = 0; i < 4; i++) {
                CustomerService customer = context.getBean(CustomerService.class);
                int finalI = i;
                Thread customerThread = new Thread(() -> customer.startCustomer("Customer-" + (finalI + 1), configurationDetails.getCustomerRetrievalRate()));
                customerThreads.add(customerThread);
            }

            //Start vendor threads
            for (Thread vendorThread : vendorThreads) {
                vendorThread.start();
            }
            //Start customer threads
            for (Thread customerThread : customerThreads) {
                customerThread.start();
            }


            // Monitor ticket pool until tickets are sold out
            synchronized (ticketService) {
                while (!ticketService.areTicketsSoldOut()) {
                    try {
                        ticketService.wait(200); // Wait for updates on the ticket pool
                    } catch (InterruptedException e) {
                        System.out.println("System interrupted while monitoring ticket pool");
                    }
                }
                System.out.println("Tickets are sold out for this event...");

            }

            //Wait for vendor threads to finish
            try {
                for (Thread vendorThread : vendorThreads) {
                    vendorThread.join();
                    ticketService.setTicketProductionComplete();
                }

            } catch (InterruptedException e) {
                System.out.println("Vendor thread was interrupted while waiting to finish");
            }


            //Interrupt customer threads
            for (Thread customerThread : customerThreads) {
                customerThread.interrupt();
            }

            //Wait for the customer threads to finish
            for (Thread customerThread : customerThreads) {
                try {
                    customerThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Customer thread was interrupted while waiting to finish");
                }
            }

        } catch (BeansException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body("System worked successfully...");


    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopSystem(){
        if(!systemStatusService.isRunning()){
            System.out.println("System is already running");
            return ResponseEntity.status(HttpStatus.OK).body("System is already running");
        }
        for(Thread vendorThread : vendorThreads){
            vendorThread.interrupt();
        }
        for(Thread customerThread : customerThreads){
            customerThread.interrupt();
        }
        systemStatusService.stopSystem();
        System.out.println("System stopped successfully...");
        return ResponseEntity.status(HttpStatus.OK).body("System stopped successfully...");
    }

    @GetMapping("/status")
    public String getSystemStatus(){
        return "System running: " + systemStatusService.isRunning() + "\n" + "Number of tickets in the pool: " + ticketService.ticketsInThePool();
    }
}
