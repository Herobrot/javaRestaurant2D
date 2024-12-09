package domain.entities;

import domain.models.Order;
import domain.models.OrderStatus;
import domain.monitors.OrderQueueMonitor;
import observers.OrderObserver;

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

                    // Simular la preparación de la comida
                    Thread.sleep(currentOrder.getPreparationTime());

                    // Marcar la orden como lista solo si aún estamos procesando esa orden
                    if (currentOrder.getStatus() == OrderStatus.PREPARING) {
                        orderQueueMonitor.markOrderAsReady(currentOrder);
                    }

                    currentOrder = null;
                    isResting = true;
                } else {
                    // Si no hay órdenes, esperar un poco antes de verificar nuevamente
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
        System.out.println("La orden para la mesa " + order.getTableNumber() + " está lista.");
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
