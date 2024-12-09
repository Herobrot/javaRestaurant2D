package domain.monitors;

import domain.models.Order;
import domain.models.OrderStatus;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import java.util.HashMap;

public class OrderQueueMonitor {
    private final Queue<Order> pendingOrders;
    private final Map<Integer, Order> readyOrders;
    private final ReentrantLock lock;
    private final Condition orderAvailable;
    private final Condition orderReady;

    public OrderQueueMonitor() {
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new HashMap<>();
        this.lock = new ReentrantLock();
        this.orderAvailable = lock.newCondition();
        this.orderReady = lock.newCondition();
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