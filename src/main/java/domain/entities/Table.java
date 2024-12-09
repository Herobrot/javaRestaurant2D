package domain.entities;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class Table extends Component {
    private final int number;
    private final Point2D position;
    private volatile boolean isOccupied;
    private volatile Customer currentCustomer;
    private final Object tableLock = new Object();

    public Table(int number, Point2D position) {
        this.number = number;
        this.position = position;
        this.isOccupied = false;
        this.currentCustomer = null;
    }

    public synchronized void release() {
        System.out.println("Freeing table " + number);

        this.currentCustomer = null;
        this.isOccupied = false;
    }

    public synchronized void setCurrentCustomer(Customer customer) {
        synchronized (tableLock) {
            System.out.println("Assigning customer to table " + number);
            this.currentCustomer = customer;
            this.isOccupied = (customer != null);
            if (customer != null) {
                System.out.println("Table " + number + " occupied by customer");
            }
        }
    }

    public synchronized Customer getCurrentCustomer() {
        synchronized (tableLock) {
            if (currentCustomer == null) {
                System.out.println("Warning: Requesting customer from empty table " + number);
            } else {
                System.out.println("Getting customer from table " + number);
            }
            return currentCustomer;
        }
    }

    public synchronized boolean isOccupied() {
        return isOccupied;
    }

    public int getNumber() {
        return number;
    }
}
