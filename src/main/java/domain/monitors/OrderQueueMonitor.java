package domain.monitors;

import domain.entities.Waiter;
import domain.models.Order;
import domain.models.OrderStatus;
import observers.OrderObserver;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OrderQueueMonitor {
    private final Queue<Order> pendingOrders;
    private final Map<Integer, Order> readyOrders;
    private final ReentrantLock lock;
    private final Condition orderAvailable;
    private final Condition orderReady;
    private final List<OrderObserver> observers;
    private final List<Waiter> waiters;

    public OrderQueueMonitor() {
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new HashMap<>();
        this.lock = new ReentrantLock();
        this.orderAvailable = lock.newCondition();
        this.orderReady = lock.newCondition();
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

    public void addOrder(Order order) {
        lock.lock();
        try {
            pendingOrders.add(order);
            orderAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    public Order getNextOrder() throws InterruptedException {
        lock.lock();
        try {
            while (pendingOrders.isEmpty()) {
                orderAvailable.await();
            }
            Order order = pendingOrders.poll();
            if (order != null) {
                order.setStatus(OrderStatus.PREPARING);
            }
            return order;
        } finally {
            lock.unlock();
        }
    }

    public void markOrderAsReady(Order order) {
        lock.lock();
        try {
            order.setStatus(OrderStatus.READY);
            readyOrders.put(order.getTableNumber(), order);
            notifyObservers(order);
            orderReady.signal();
        } finally {
            lock.unlock();
        }
    }

    public Order checkReadyOrder(int tableNumber) throws InterruptedException {
        lock.lock();
        try {
            if (!readyOrders.containsKey(tableNumber)) {
                return null;
            }
            Order order = readyOrders.remove(tableNumber);
            if (order != null) {
                order.setStatus(OrderStatus.DELIVERED);
            }
            return order;
        } finally {
            lock.unlock();
        }
    }
}