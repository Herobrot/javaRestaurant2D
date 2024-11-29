package domain.entities;

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
