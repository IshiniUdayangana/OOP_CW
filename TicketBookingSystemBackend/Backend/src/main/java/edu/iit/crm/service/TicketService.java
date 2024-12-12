package edu.iit.crm.service;

import edu.iit.crm.entity.ConfigurationDetails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Service

public class TicketService {

    private List<String> ticketPool = Collections.synchronizedList(new LinkedList<>());
    private int maxCapacity = 10;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();
    private boolean isTicketProductionComplete;
    private int totalTicketsReleased;


    @Async
    public void addTicket(String ticket) throws InterruptedException {
        lock.lock(); //Acquires the lock
        try{
            while(ticketPool.size() == maxCapacity){ //Blocks the thread if the ticket pool is full
                System.out.println("Ticket pool is full. Waiting for tickets to be purchased by customers...");
                notFull.await(); //Waits until the pool becomes not full
            }
            ticketPool.add(ticket);
            System.out.println("Max capacity: " + maxCapacity);
            totalTicketsReleased++;
            notEmpty.signal(); //Sends a signal to the customer threads that the pool is not empty
        }
        catch(InterruptedException e){
            System.out.println("Error occurred");
            Thread.currentThread().interrupt();
        }
        finally {
            lock.unlock(); //Releases the lock
        }
    }

    public String removeTicket() throws InterruptedException {
        lock.lock();
        try{
            while(ticketPool.isEmpty()){ //Blocks the thread if the ticket pool is empty
                if(isTicketProductionComplete){
                    return null;
                }
                System.out.println("Ticket pool is empty. Waiting for tickets to be added to the pool by vendors...");
                notEmpty.await(); //Waits until the pool becomes not empty
            }
            String ticket = ticketPool.remove(0); // Removes the first ticket from the pool
            notFull.signal(); //Sends a signal to the vendor threads that the pool is not full
            return ticket;
        }
        finally {
            lock.unlock();
        }

    }

    public List<String> getAllTickets(){
        return ticketPool;
    }

    public int ticketsInThePool(){
        return ticketPool.size();
    }

    public void clearTickets(){
        ticketPool.clear();
    }

    public void setTicketProductionComplete(){
        lock.lock();
        try{
            isTicketProductionComplete = true;
            notEmpty.signalAll(); //Wake up all waiting customers
        }
        finally {
            lock.unlock();
        }
    }

    public synchronized boolean areTicketsSoldOut(){
        lock.lock();
        try{
            return ticketPool.isEmpty() && isTicketProductionComplete;
        }
        finally {
            lock.unlock();
        }

    }

}
