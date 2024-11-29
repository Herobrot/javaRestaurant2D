package domain.entities;
public class Table {
    private int tableNumber;
    private boolean available;

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
}
