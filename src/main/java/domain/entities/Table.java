package domain.entities;

import javafx.geometry.Point2D;

import java.util.List;

public class Table {
    private int tableNumber;
    private boolean available;
    private List<Point2D> route;

    public Table(int tableNumber, boolean available) {
        this.tableNumber = tableNumber;
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setRoute(List<Point2D> route) { this.route = route; }
}
