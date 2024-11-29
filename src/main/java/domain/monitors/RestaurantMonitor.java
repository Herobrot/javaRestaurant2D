package domain.monitors;

import com.almasb.fxgl.dsl.FXGL;
import domain.entities.Client;
import domain.entities.Order;
import domain.entities.Table;
import domain.entities.Waiter;
import javafx.geometry.Point2D;
import presentation.events.ClientMoveToTableEvent;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RestaurantMonitor {
    private final int capacity;
    private List<Table> tables;
    public final Queue<Client> waitingQueue;
    private final KitchenMonitor orderBuffer;
    private Queue<Client> kitchenOrders;
    private final Waiter waiter;

    public RestaurantMonitor(int capacity,Waiter waiter) {
        this.capacity = capacity;
        this.waitingQueue = new LinkedList<>();
        this.orderBuffer = new KitchenMonitor();
        this.kitchenOrders =  new LinkedList<>();
        this.tables = new ArrayList<>();
        this.waiter = waiter;
        for (int i = 0; i < capacity; i++) {
            tables.add(new Table(i, true));
        }

    }

    public synchronized int enterRestaurant(Client client) {
        System.out.println("Entering restaurant " + client.getId());
        System.out.println(tables.size());
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).isAvailable()) {
                System.out.println("Entering restaurant " + i);
                client.setTableNumber(i);
                kitchenOrders.add(client);
                tables.get(i).setAvailable(false);
                return i;
            }
        }
        waitingQueue.add(client);
        return -1;
    }

    public synchronized void leaveRestaurant(int tableNumber) {
       tables.get(tableNumber).setAvailable(true);
        System.out.println("Leaving restaurant " + tableNumber);


        if (!waitingQueue.isEmpty()) {
            Client nextClient = waitingQueue.poll();
            notifyClient(nextClient, tableNumber);
        }
    }


    private void notifyClient(Client client, int tableNumber) {
        List<Point2D> route = this.tables.get(tableNumber).getRoute();
        client.setTableNumber(tableNumber);
        FXGL.getEventBus().fireEvent(new ClientMoveToTableEvent(client, route));
        client.setState(Client.ClientState.WAITING_FOR_WAITER);
    }
    public KitchenMonitor getOrderBuffer() {
        return orderBuffer;
    }


    public synchronized void addOrder(Order order) {
        orderBuffer.addOrder(order);
    }


    public synchronized Order getOrderToCook() {
        return orderBuffer.getOrderToCook();
    }

    public synchronized void completeOrder(Order order) {
        orderBuffer.completeOrder(order);
        System.out.println("Completed order " + order);
        waiter.addReadyOrder(order);
    }
    public Client notifyClientFoodReady(Order order) {
        System.out.println(order.getCustomerId());
        Client client = getClientById(order.getCustomerId());
        if (client != null) {
            client.setState(Client.ClientState.EATING);
            System.out.println("Notifying client " + client.getId() + " that food is ready at table " + client.getTableNumber());
        }
        return client;
    }

    private Client getClientById(int clientId) {
        for (Client client : kitchenOrders) {
            if (client.getId() == clientId) {
                return client;
            }
        }
        return null;
    }

    public synchronized int getAvailableTable() {
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).isAvailable()) {
                tables.get(i).setAvailable(false);
                return i;
            }
        }
        return -1;
    }

    public synchronized Table getTable(int id){ return tables.get(id); }

    public synchronized void setRouteTables(int id, List<Point2D> route){
        tables.get(id).setRoute(route);

    }

    public synchronized Queue<Client> getWaitingQueue() {
        return waitingQueue;
    }
}
