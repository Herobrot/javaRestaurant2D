package domain.monitors;

import utils.GameConfig;
import domain.entities.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class RestaurantMonitor {
    private final boolean[] tables;
    private final ReentrantLock lock;
    private final Condition tableAvailable;
    private final Map<Integer, Customer> tableAssignments;  // Nuevo


    public RestaurantMonitor() {
        tables = new boolean[GameConfig.TOTAL_TABLES];
        lock = new ReentrantLock();
        tableAvailable = lock.newCondition();
        tableAssignments = new HashMap<>();  // Inicializar mapa
    }

    public int findAvailableTable() {
        lock.lock();
        try {
            for (int i = 0; i < tables.length; i++) {
                if (!tables[i]) {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public void occupyTable(int tableNumber) {
        lock.lock();
        try {
            if (tableNumber >= 0 && tableNumber < tables.length) {
                tables[tableNumber] = true;
                System.out.println("Mesa " + tableNumber + " marcada como ocupada en monitor");
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseTable(int tableNumber) {
        lock.lock();
        try {
            if (tableNumber >= 0 && tableNumber < tables.length) {
                tables[tableNumber] = false;
                tableAssignments.remove(tableNumber);
                System.out.println("Mesa " + tableNumber + " liberada en monitor");
                tableAvailable.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void waitForAvailableTable() throws InterruptedException {
        lock.lock();
        try {
            while (findAvailableTable() == -1) {
                tableAvailable.await();
            }
        } finally {
            lock.unlock();
        }
    }
}