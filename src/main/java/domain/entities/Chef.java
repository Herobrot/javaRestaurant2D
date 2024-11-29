package domain.entities;

import domain.monitors.RestaurantMonitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Chef {
    private final int id;
    private boolean isAvailable;
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
}