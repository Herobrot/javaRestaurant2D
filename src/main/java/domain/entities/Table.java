package domain.entities;

import javafx.geometry.Point2D;

import java.util.List;

public class Table {
    private int tableNumber;
    private boolean available;
    private List<Point2D> route;
    private Chair chair;

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
    public void setChair(Chair chair) { this.chair = chair; }
    public Chair getChair(){ return chair; }

    public void setRoute(List<Point2D> route) { this.route = route; }

    public List<Point2D> getRoute(){ return route; }
}
