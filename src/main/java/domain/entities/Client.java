package domain.entities;

import com.almasb.fxgl.texture.Texture;
import domain.components.ClientComponent;
import domain.components.services.Direction;
import domain.monitors.RestaurantMonitor;
import javafx.geometry.Point2D;

public class Client {
    private Point2D position;
    private Direction direction;
    private final int id;
    private int tableNumber;
    private ClientState state;
    private Texture texture;
    private ClientComponent component;

    public enum ClientState {
        WAITING_FOR_WAITER,
        WAITING_FOR_FOOD,
        EATING,             // Comiendo
        LEAVING
    }

    public Client(int id) {
        this.id = id;
        this.state = ClientState.WAITING_FOR_WAITER;
        this.direction = Direction.UP;
        this.component = new ClientComponent();
    }

    public int getId() {
        return id;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public ClientState getState() {
        return state;
    }
    public void setComponent(ClientComponent component) {
        this.component = component;
    }


    public void eatAndLeave(RestaurantMonitor monitor) {
        try {

            System.out.println("Client " + id + " is eating at table " + tableNumber);
            Thread.sleep(5000);
            System.out.println("Client " + id + " is done eating at table " + tableNumber);
            System.out.println("Client " + id + " notifying waiter that they are leaving.");
            monitor.leaveRestaurant(tableNumber);
            this.state = ClientState.LEAVING;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void move() {
        this.position = position.add(direction.vector);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
