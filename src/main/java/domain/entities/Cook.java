package domain.entities;

import domain.models.Order;
import domain.models.OrderStatus;
import domain.monitors.OrderQueueMonitor;

public class Cook implements Runnable, observers.OrderObserver {
    private final OrderQueueMonitor orderQueueMonitor;
    private volatile boolean isResting;
    private Order currentOrder;
    private volatile boolean running = true;

    public Cook(int id, OrderQueueMonitor orderQueueMonitor) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.isResting = true;
        orderQueueMonitor.addObserver(this);
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                currentOrder = orderQueueMonitor.getNextOrder();
                if (currentOrder != null) {
                    isResting = false;

                    // Cooking simulation
                    Thread.sleep(currentOrder.getPreparationTime());

                    // Mark the order as ready only if we are still processing the order
                    if (currentOrder.getStatus() == OrderStatus.PREPARING) {
                        orderQueueMonitor.markOrderAsReady(currentOrder);
                    }

                    currentOrder = null;
                    isResting = true;
                } else {
                    // If there are no orders, wait a little while before checking again.
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void onOrderReady(Order order) {
        System.out.println("The order for table " + order.getTableNumber() + " is ready.");
        orderQueueMonitor.notifyWaitersAboutReadyOrder(order);
    }

    public void stop() {
        running = false;
        Thread.currentThread().interrupt();
    }

    public boolean isResting() {
        return isResting;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
}
