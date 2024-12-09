package domain.entities;

import com.almasb.fxgl.entity.component.Component;
import domain.monitors.RestaurantMonitor;
import domain.models.CustomerStats;
import javafx.geometry.Point2D;
import java.util.Queue;
import java.util.LinkedList;

public class Receptionist extends Component {
    private final Point2D position;
    private final RestaurantMonitor restaurantMonitor;
    private final Queue<Customer> waitingCustomers;
    private boolean isBusy;
    private final CustomerStats customerStats;

    public Receptionist(RestaurantMonitor restaurantMonitor, Point2D position, CustomerStats customerStats) {
        this.restaurantMonitor = restaurantMonitor;
        this.position = position;
        this.customerStats = customerStats;
        this.waitingCustomers = new LinkedList<>();
        this.isBusy = false;

        startReceptionistBehavior();
    }

    private void startReceptionistBehavior() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    processNextCustomer();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public synchronized void addCustomerToQueue(Customer customer) {
        int tableNumber = restaurantMonitor.findAvailableTable();

        if (tableNumber != -1) {
            restaurantMonitor.occupyTable(tableNumber);
            customer.assignTable(tableNumber);
        } else {
            waitingCustomers.add(customer);
            customerStats.incrementWaitingForTable();
            customer.waitForTable();
            notifyAll(); // Notificar a cualquier hilo que esté esperando en `processNextCustomer`
        }
    }

    private synchronized void processNextCustomer() throws InterruptedException {
        while (waitingCustomers.isEmpty()) {
            wait(); // Esperar hasta que haya clientes en la cola
        }

        int tableNumber = restaurantMonitor.findAvailableTable();
        if (tableNumber != -1) {
            Customer customer = waitingCustomers.poll();
            if (customer != null) {
                restaurantMonitor.occupyTable(tableNumber);
                customer.assignTable(tableNumber);
            }
        }
    }
}
