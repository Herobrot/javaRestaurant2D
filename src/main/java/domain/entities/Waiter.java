package domain.entities;

import domain.monitors.RestaurantMonitor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
        this.readyOrders = new LinkedBlockingQueue<>();
    }

    /**
     * Método para atender a un cliente
     *
     * @param customer Cliente a atender
     * @return true si el mesero pudo atender al cliente, false si está ocupado
     */
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
        System.out.println("Mesero " + id + " Getting to client " + client.getId() + " on the  table " + tableNumber);

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
        return order ;
    }

    /**
     * Método para liberar al mesero cuando termina de atender
     */
    public synchronized void finishService() {
        currentCustomer = null;
        isAvailable = true;
    }
    /**
     * Devuelve si el mesero está disponible.
     *
     * @return true si el mesero está disponible, false si está ocupado.
     */
    public synchronized boolean isAvailable() {
        return isAvailable;
    }
    public int getId() {
        return id;
    }
    public void takeOrder(RestaurantMonitor monitor) {
        new Thread(() -> {
            while(true){
                try {
                    Order order = readyOrders.take(); //Blocks until an order is available
                    System.out.println("Waiter " + id + " taking order " + order.getOrderId() + " for client " + order.getCustomerId());
                    //Simulate delivering the order
                    Thread.sleep(1000);
                    System.out.println("Waiter " + id + " delivered order " + order.getOrderId() + " to client " + order.getCustomerId());
                    isAvailable = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
    public void addReadyOrder(Order order){
        readyOrders.offer(order);
    }
}
