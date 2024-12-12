package edu.iit.crm.service;

import org.springframework.stereotype.Service;

@Service

public class SystemStatusService {

    private boolean isRunning = false;

    private int activeVendors = 0;
    private int activeCustomers = 0;

    public synchronized boolean isRunning(){
        return isRunning;
    }

    public synchronized void startSystem(){
        isRunning = true;
    }

    public synchronized void stopSystem(){
        isRunning = false;
    }

}
