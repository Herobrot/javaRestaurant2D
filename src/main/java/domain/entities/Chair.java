package domain.entities;

import javafx.geometry.Point2D;

public class Chair {
    private int tableNumber;
    private Point2D position;

    public Chair(int tableNumber, double x, double y) {
        this.position = new Point2D(x, y);
        this.tableNumber = tableNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }
    public void setPosition(Point2D position){ this.position = position; }
    public Point2D getPosition(){ return position; }

}
