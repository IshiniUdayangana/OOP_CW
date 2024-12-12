package edu.iit.crm.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private List<String> tickets = Collections.synchronizedList(new LinkedList<>());
    private int maxCapacity;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();
    private boolean isTicketProductionComplete;
    private int totalTicketsReleased;

    public TicketPool(int maxCapacity){

        this.maxCapacity = maxCapacity;
    }

    public void addTicket(String ticket) throws InterruptedException {
        lock.lock(); //Acquires the lock
        try{
            while(tickets.size() == maxCapacity){ //Blocks the thread if the ticket pool is full
                System.out.println("Ticket pool is full. Waiting for tickets to be purchased by customers...");
                notFull.await(); //Waits until the pool becomes not full
            }
            tickets.add(ticket);
            totalTicketsReleased++;
            notEmpty.signal(); //Sends a signal to the customer threads that the pool is not empty
        }
        finally {
            lock.unlock(); //Releases the lock
        }
    }

    public String removeTicket() throws InterruptedException {
        lock.lock();
        try{
            while(tickets.isEmpty()){ //Blocks the thread if the ticket pool is empty
                if(isTicketProductionComplete){
                    return null;
                }
                System.out.println("Ticket pool is empty. Waiting for tickets to be added to the pool by vendors...");
                notEmpty.await(); //Waits until the pool becomes not empty
            }
            String ticket = tickets.remove(0); // Removes the first ticket from the pool
            notFull.signal(); //Sends a signal to the vendor threads that the pool is not full
            return ticket;
        }
        finally {
            lock.unlock();
        }

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
        return tickets.isEmpty() && isTicketProductionComplete;
    }

    public int getTicketCount(){
        return tickets.size();
    }


    public int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

}
