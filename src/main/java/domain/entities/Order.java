package domain.entities;

/**
 * Clase que representa una orden en el restaurante
 */
public class Order {
    // Enumeración para los posibles estados de una orden
    public enum OrderState {
        PENDING, // Orden pendiente de preparación
        IN_PROGRESS, // Orden en preparación
        READY, // Orden lista para servir
        DELIVERED // Orden entregada al cliente
    }

    private final int orderId; // Identificador único de la orden
    private final int clientId; // ID del cliente que realizó la orden
    private OrderState state; // Estado actual de la orden
    private final long orderTime; // Tiempo en que se realizó la orden

    // Constructor
    public Order(int orderId, int clientId) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.state = OrderState.PENDING;
        this.orderTime = System.currentTimeMillis();
    }

    // Métodos getter y setter
    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return clientId;
    }
}