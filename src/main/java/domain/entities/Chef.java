package domain.entities;

import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.monitors.RestaurantMonitor;
import javafx.geometry.Point2D;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Chef {
    private final int id;
    private boolean isAvailable;
    private Point2D position;
    private Direction direction;
    private Texture texture;
    private BlockingQueue<Order> ordersToCook;


    public Chef(int id) {
        this.id = id;
        this.isAvailable = true;
        this.ordersToCook = new LinkedBlockingDeque<>();
    }

    public void startCooking(Order order) {
        if (order == null) {
            throw new NullPointerException("Cannot add null order");
        }
        boolean success = ordersToCook.offer(order);
        if (!success) {
            System.out.println("Failed to add order to the cooking queue.");
        }
    }


    public void cook(RestaurantMonitor monitor){
        new Thread(()->{
            while(true){
                try {
                    Order order = ordersToCook.take();
                    System.out.println("Chef " + id + " cooking order " + order.getOrderId());

                    Thread.sleep(3000);
                    order.setState(Order.OrderState.READY);
                    System.out.println("Chef " + id + " finished cooking order " + order.getOrderId());
                    monitor.completeOrder(order);
                    isAvailable = true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public boolean isAvailable(){
        return isAvailable;
    }

    public int getId(){
        return id;
    }
    public void finishCooking(){
        isAvailable = true;
    }
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPosition(Point2D position) { this.position = position; }
    public Point2D getPosition() { return position; }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}