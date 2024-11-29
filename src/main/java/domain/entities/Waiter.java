package domain.entities;

import domain.monitors.RestaurantMonitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Waiter {
    private final int id; // Identificador único del mesero
    private boolean isAvailable; // Indica si el mesero está disponible
    private Client currentCustomer; // Cliente actual que está siendo atendido
    private final BlockingQueue<Order> readyOrders;

    // Constructor
    public Waiter(int id) {
        this.id = id;
        this.isAvailable = true;
        this.currentCustomer = null;
        this.readyOrders = new LinkedBlockingDeque<>();
    }

    public synchronized boolean attendCustomer(Client customer) {
        if (isAvailable) {
            isAvailable = false;
            currentCustomer = customer;
            return true;
        }
        return false;
    }

    public Order serveClient(Client client, int tableNumber, RestaurantMonitor monitor) {
        isAvailable = false;
        System.out.println("Mesero " + id + " Getting to client " + client.getId() + " on the table " + tableNumber);

        // Crear y agregar pedido
        Order order = new Order(client.getId(), tableNumber);
        new Thread(() -> {
            try {
                // Simular tiempo de servicio
                Thread.sleep(2000);
                monitor.getOrderBuffer().addOrder(order);
                System.out.println("Order created: " + order.getOrderId() + " by client " + client.getId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isAvailable = true;
            }
        }).start();
        return order;
    }

    public synchronized void finishService() {
        currentCustomer = null;
        isAvailable = true;
    }

    public synchronized void takeOrder(RestaurantMonitor monitor) {
        new Thread(() -> {
            while (true) {
                try {
                    Order order = readyOrders.take(); // Blocks until an order is available
                    System.out.println("Waiter " + id + " took order " + order.getOrderId() + " for client " + order.getCustomerId());
                    Thread.sleep(1000);
                    System.out.println("Waiter " + id + " delivered order " + order.getOrderId() + " to client " + order.getCustomerId());
                    Client client  = monitor.notifyClientFoodReady(order);
                    client.eatAndLeave(monitor);
                    isAvailable = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void addReadyOrder(Order order) {
        System.out.println("Waiter " + id + " adding ready order by kitchen " + order.getOrderId());
        readyOrders.offer(order);
    }
    public void monitorClient(Client client, RestaurantMonitor monitor) {
        new Thread(() -> {
            try {
                while (client.getState() != Client.ClientState.LEAVING) {
                    // Simulate waiting for the client to notify waiter
                    Thread.sleep(1000);
                }
                // Once client finishes, handle the table release
                System.out.println("Waiter " + id + " received notification that client " + client.getId() + " is leaving.");
                monitor.leaveRestaurant(client.getTableNumber());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    public boolean isAvailable(){
        return isAvailable;
    }

    public int getId(){
        return id;
    }
}
