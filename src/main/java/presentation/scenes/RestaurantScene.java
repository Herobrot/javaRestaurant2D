package presentation.scenes;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import domain.entities.Client;
import domain.entities.Waiter;
import domain.entities.Chef;
import domain.entities.Order;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import domain.monitors.RestaurantMonitor;
import presentation.views.ChairView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantScene extends GameApplication {

    private static final int RESTAURANT_CAPACITY = 20;
    private static final int NUM_WAITERS = 2;
    private static final int NUM_CHEFS = 3;
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;

    private RestaurantMonitor restaurantMonitor;
    private List<Entity> tables;
    private List<Waiter> waiters;
    private List<Chef> chefs;
    private List<Client> waitingClients;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WINDOW_WIDTH);
        settings.setHeight(WINDOW_HEIGHT);
        settings.setTitle("Restaurant Simulator");
        settings.setVersion("1.0");
        settings.setManualResizeEnabled(true);
        settings.setScaleAffectedOnResize(true);
        settings.setPreserveResizeRatio(true);
    }

    @Override
    protected void initGame() {
        initializeGameComponents();
        createBackground();
        createKitchenAndWaitingArea();
        restaurantMonitor = new RestaurantMonitor(RESTAURANT_CAPACITY);
        createTables();
        createStaff();
        startClientGenerator();
        updateGame();
    }

    private void updateGame() {
        FXGL.run(() -> {
            for (Waiter waiter : waiters) {
                if (waiter.isAvailable() && !restaurantMonitor.getWaitingQueue().isEmpty()) {
                    Client Client = restaurantMonitor.getWaitingQueue().poll();
                    int tableNumber = restaurantMonitor.enterRestaurant(Client);
                    if (tableNumber != -1) {
                        handleClient(waiter, Client, tableNumber);
                    }
                }
            }

            for (Chef chef : chefs) {
                Order orderToCook = restaurantMonitor.getOrderBuffer().getOrderToCook();
                if (orderToCook != null) {
                    handleChef(chef, orderToCook);
                }
            }
        }, Duration.seconds(0.1)); // Ejecuta la actualización cada 0.1 segundos
    }

    private void handleClient(Waiter waiter, Client Client, int tableNumber) {
        // Simula el tiempo de servicio usando FXGL.runOnce
        FXGL.runOnce(() -> {
            int randomId = (int) (FXGL.random() * 1000);
            Order order = new Order(randomId * 1000 , Client.getId());
            restaurantMonitor.getOrderBuffer().addOrder(order);
            System.out.println("Mesero " + waiter.getId() + " tomó la orden del cliente " + Client.getId() + " en la mesa " + tableNumber);
        }, Duration.seconds(1)); // Simula un tiempo de servicio de 1 segundo
    }


    private void handleChef(Chef chef, Order order) {
        FXGL.runOnce(() -> {
            restaurantMonitor.getOrderBuffer().completeOrder(order);
            System.out.println("Chef " + chef.getId() + " ha completado la orden: " + order.getOrderId());
        }, Duration.seconds(2));
    }

    private void initializeGameComponents() {
        tables = new ArrayList<>();
        waiters = new ArrayList<>();
        chefs = new ArrayList<>();
        waitingClients = new ArrayList<>();
        FXGL.getGameWorld().addEntityFactory(new ChairView());
    }

    private void createBackground() {
        FXGL.entityBuilder()
                .at(0, 0)
                .view("background.png")
                .buildAndAttach();
    }

    private void createKitchenAndWaitingArea() {
        // Área de cocina
        FXGL.entityBuilder()
                .at(WINDOW_WIDTH - 180, 250)
                .view(new Rectangle(150, 200, Color.rgb(255, 255, 224, 0.5)))
                .buildAndAttach();

        // Área de espera
        FXGL.entityBuilder()
                .at(50, 150)
                .view(new Rectangle(150, 200, Color.rgb(173, 216, 230, 0.5)))
                .buildAndAttach();
    }

    private void createTables() {
        ChairView gameFactory = new ChairView();
        int startX = 262;
        int startY = 279;
        int spacing = 120;

        for (int i = 0; i < RESTAURANT_CAPACITY; i++) {
            int row = i / 5;
            int col = i % 5;
            Entity table = gameFactory.createTable(
                    startX + col * spacing,
                    startY + row * spacing);
            tables.add(table);
            FXGL.getGameWorld().addEntity(table);
        }
    }

    private void createStaff() {
        // Crear meseros
        for (int i = 0; i < NUM_WAITERS; i++) {
            Waiter waiter = new Waiter(i);
            waiters.add(waiter);
            FXGL.spawn("waiter", 100, 300 + i * 50);
        }

        // Crear cocineros
        for (int i = 0; i < NUM_CHEFS; i++) {
            Chef chef = new Chef(i);
            chefs.add(chef);
            FXGL.spawn("chef", WINDOW_WIDTH - 150, 100 + i * 50);
        }
    }

    private void startClientGenerator() {
        FXGL.run(() -> {
            if (waitingClients.size() < RESTAURANT_CAPACITY * 2) {
                Client newClient = new Client(waitingClients.size());
                waitingClients.add(newClient);
                ChairView.createCustomerEntity(newClient);
            }
        }, Duration.seconds(getPoissonRandomTime()));
    }

    private double getPoissonRandomTime() {
        double lambda = 2.0;
        return -Math.log(1.0 - Math.random()) / lambda;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
