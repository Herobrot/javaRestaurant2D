package domain.entities;

import domain.monitors.RestaurantMonitor;

public class Waiter {
    private final int id; // Identificador único del mesero
    private boolean isAvailable; // Indica si el mesero está disponible
    private Client currentCustomer; // Cliente actual que está siendo atendido

    // Constructor
    public Waiter(int id) {
        this.id = id;
        this.isAvailable = true;
        this.currentCustomer = null;
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
    public void serveClient(Client client, int tableNumber, RestaurantMonitor monitor) {
        isAvailable = false;
        System.out.println("Mesero " + id + " sirviendo al cliente " + client.getId() + " en la mesa " + tableNumber);

        new Thread(() -> {
            try {
                // Simular tiempo de servicio
                Thread.sleep(2000);

                // Crear y agregar pedido
                Order order = new Order(client.getId(), tableNumber);
                monitor.getOrderBuffer().addOrder(order);

                System.out.println("Mesero " + id + " ha generado el pedido " + order.getOrderId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isAvailable = true;
            }
        }).start();
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
}
