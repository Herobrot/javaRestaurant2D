package domain.monitors;

import domain.entities.Customer;
import java.util.LinkedList;
import java.util.Queue;

public class CustomerQueueMonitor {
    private final Queue<CustomerRequest> waitingCustomers;


    public static class CustomerRequest {
        public final Customer customer;
        public final int tableNumber;
        public final long arrivalTime;

        public CustomerRequest(Customer customer, int tableNumber) {
            this.customer = customer;
            this.tableNumber = tableNumber;
            this.arrivalTime = System.currentTimeMillis();
        }
    }

    public CustomerQueueMonitor() {
        waitingCustomers = new LinkedList<>();
    }


    public synchronized void addCustomer(Customer customer, int tableNumber) {
        waitingCustomers.add(new CustomerRequest(customer, tableNumber));
        notify();
    }

    public synchronized CustomerRequest getNextCustomer() throws InterruptedException {
        while (waitingCustomers.isEmpty()) {
            wait();
        }
        return waitingCustomers.poll();
    }


    public synchronized boolean hasWaitingCustomers() {
        return !waitingCustomers.isEmpty();
    }
}
