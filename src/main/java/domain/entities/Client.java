package domain.entities;

import domain.monitors.RestaurantMonitor;

public class Client {
    private final int id;
    private int tableNumber;
    private ClientState state;

    public enum ClientState {
        WAITING_FOR_WAITER,
        WAITING_FOR_FOOD,
        EATING,             // Comiendo
        LEAVING
    }

    public Client(int id) {
        this.id = id;
        this.state = ClientState.WAITING_FOR_WAITER;
    }

    public int getId() {
        return id;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public ClientState getState() {
        return state;
    }

    public void eatAndLeave(RestaurantMonitor monitor) {
        try {

            System.out.println("Client " + id + " is eating at table " + tableNumber);
            Thread.sleep(50000); // Simulate time taken to eat
            System.out.println("Client " + id + " is done eating at table " + tableNumber);
            System.out.println("Client " + id + " notifying waiter that they are leaving.");
            monitor.leaveRestaurant(tableNumber);


            this.state = ClientState.LEAVING;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
