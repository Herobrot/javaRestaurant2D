package domain.entities;

import com.almasb.fxgl.texture.Texture;
import domain.components.RecepcionistComponent;
import domain.components.services.Direction;
import javafx.geometry.Point2D;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Recepcionist {
    private Point2D position;
    private Texture texture;
    private boolean isAvailable;
    private Client currentCustomer;
    private final BlockingQueue<Order> readyOrders;
    private RecepcionistComponent component;


    public Recepcionist(Point2D position) {
        this.position = position;
        this.isAvailable = true;
        this.currentCustomer = null;
        this.readyOrders = new LinkedBlockingDeque<>();
        this.component = new RecepcionistComponent();
    }

    public synchronized boolean attendCustomer(Client customer) {
        if (isAvailable) {
            this.component.updateTextureBasedOnDirection(Direction.RIGHT, this);
            isAvailable = false;
            currentCustomer = customer;
            customer.setState(Client.ClientState.WAITING_FOR_FOOD);
            System.out.println("[RECEPCIONIST] Atendi el cliente " + customer.getId());
            return true;
        }
        return false;
    }
    public synchronized void finishService() {
        this.component.updateTextureBasedOnDirection(Direction.DOWN, this);
        currentCustomer = null;
        isAvailable = true;
    }

    public boolean isAvailable(){
        return isAvailable;
    }
    public Point2D getPosition(){ return position; }
    public void setPosition(Point2D position) { this.position = position; }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
