package domain.monitors;

import domain.entities.Waiter;
import domain.models.Order;
import domain.models.OrderStatus;
import observers.OrderObserver;

import java.util.*;

public class OrderQueueMonitor {
    private final Queue<Order> pendingOrders;
    private final Map<Integer, Order> readyOrders;
    private final List<OrderObserver> observers;
    private final List<Waiter> waiters;

    public OrderQueueMonitor() {
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new HashMap<>();
        this.observers = new ArrayList<>();
        this.waiters = new ArrayList<>();
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderReady(order);
        }
    }

    public void notifyWaitersAboutReadyOrder(Order order) {
        for (Waiter waiter : waiters) {
            waiter.notifyOrderReady(order);
        }
    }

    public synchronized void addOrder(Order order) {
        pendingOrders.add(order);
        notify();
    }

    public synchronized Order getNextOrder() throws InterruptedException {
        while (pendingOrders.isEmpty()) {
            wait();
        }
        Order order = pendingOrders.poll();
        if (order != null) {
            order.setStatus(OrderStatus.PREPARING);
        }
        return order;
    }

    public synchronized void markOrderAsReady(Order order) {
        order.setStatus(OrderStatus.READY);
        readyOrders.put(order.getTableNumber(), order);
        notifyObservers(order);
        notify();
    }

    public synchronized Order checkReadyOrder(int tableNumber) throws InterruptedException {
        if (!readyOrders.containsKey(tableNumber)) {
            return null;
        }
        Order order = readyOrders.remove(tableNumber);
        if (order != null) {
            order.setStatus(OrderStatus.DELIVERED);
        }
        return order;
    }
}
