package domain.entities;

import domain.models.Order;
import domain.models.OrderStatus;
import domain.monitors.OrderQueueMonitor;

public class Cook implements Runnable {
    private final OrderQueueMonitor orderQueueMonitor;
    private volatile boolean isResting;
    private Order currentOrder;
    private volatile boolean running = true;

    public Cook(int id, OrderQueueMonitor orderQueueMonitor) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.isResting = true;
    }


    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Intentar obtener una nueva orden
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