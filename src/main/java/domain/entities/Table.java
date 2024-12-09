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
        System.out.println("Liberando mesa " + number);
        this.currentCustomer = null;
        this.isOccupied = false;
    }

    public synchronized void setCurrentCustomer(Customer customer) {
        synchronized (tableLock) {
            System.out.println("Asignando cliente a mesa " + number);
            this.currentCustomer = customer;
            this.isOccupied = (customer != null);
            if (customer != null) {
                System.out.println("Mesa " + number + " ocupada por cliente");
            }
        }
    }

    public synchronized Customer getCurrentCustomer() {
        synchronized (tableLock) {
            if (currentCustomer == null) {
                System.out.println("Advertencia: Solicitando cliente de mesa " + number + " vacía");
            } else {
                System.out.println("Obteniendo cliente de mesa " + number);
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
