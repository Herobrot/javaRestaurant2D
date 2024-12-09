package domain.monitors;

import utils.GameConfig;
import domain.entities.Customer;

import java.util.HashMap;
import java.util.Map;

public class RestaurantMonitor {
    private final boolean[] tables;
    private final Map<Integer, Customer> tableAssignments;

    public RestaurantMonitor() {
        tables = new boolean[GameConfig.TOTAL_TABLES];
        tableAssignments = new HashMap<>();
    }

    public synchronized int findAvailableTable() {
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i]) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void occupyTable(int tableNumber) {
        if (tableNumber >= 0 && tableNumber < tables.length) {
            tables[tableNumber] = true;
        }
    }

    public synchronized void releaseTable(int tableNumber) {
        if (tableNumber >= 0 && tableNumber < tables.length) {
            tables[tableNumber] = false;
            tableAssignments.remove(tableNumber);
            notifyAll();
        }
    }

    public synchronized void waitForAvailableTable() throws InterruptedException {
        while (findAvailableTable() == -1) {
            wait();
        }
    }
}
