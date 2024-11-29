package domain.entities;

/**
 * Clase que representa una orden en el restaurante
 */
public class Order {
    public enum OrderState {
        PENDING,
        IN_PROGRESS,
        READY,
        DELIVERED
    }

    private final int orderId;
    private final int clientId;
    private OrderState state;
    private final long orderTime;

    public Order(int orderId, int clientId) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.state = OrderState.PENDING;
        this.orderTime = System.currentTimeMillis();
    }

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