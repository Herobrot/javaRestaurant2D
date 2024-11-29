package domain.entities;

/**
 * Clase que representa a un cocinero en el restaurante
 * Los cocineros preparan las órdenes que reciben del buffer de órdenes
 */
public class Chef {
    private final int id; // Identificador único del cocinero
    private boolean isCooking; // Indica si el cocinero está cocinando
    private Order currentOrder; // Orden actual que está preparando

    // Constructor
    public Chef(int id) {
        this.id = id;
        this.isCooking = false;
        this.currentOrder = null;
    }

    /**
     * Método para comenzar a cocinar una orden
     *
     * @param order Orden a preparar
     * @return true si el cocinero puede comenzar a cocinar, false si está ocupado
     */
    public synchronized boolean startCooking(Order order) {
        if (!isCooking) {
            isCooking = true;
            currentOrder = order;
            return true;
        }
        return false;
    }

    /**
     * Método para finalizar la preparación de una orden
     */
    public synchronized void finishCooking() {
        currentOrder = null;
        isCooking = false;
    }

    public int getId() {
        return id;
    }
}