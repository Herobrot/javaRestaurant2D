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

        System.out.println("Settings initialized");
    }

    @Override
    protected void initGame() {
        System.out.println("Initializing game...");
        initializeGameComponents();
        createBackground();
        createKitchenAndWaitingArea();
        restaurantMonitor = new RestaurantMonitor(RESTAURANT_CAPACITY);
        createTables();
        createStaff();

        generateInitialClients();

        startClientGenerator();
        startGameLoop();
        System.out.println("Game initialized successfully");
    }

    private void generateInitialClients() {
        System.out.println("Generating initial clients...");
        for (int i = 0; i < 5; i++) { // Genera 5 clientes iniciales
            Client newClient = new Client(i);
            waitingClients.add(newClient);
            boolean added = restaurantMonitor.getWaitingQueue().offer(newClient);
            if (added) {
                ChairView.createCustomerEntity(newClient);
                System.out.println("Initial client added: " + newClient.getId());
            } else {
                System.out.println("Failed to add initial client " + newClient.getId() + " to waiting queue");
            }
        }
    }

    private void startGameLoop() {
        FXGL.run(() -> updateGame(), Duration.seconds(0.5)); // Actualiza cada 0.5 segundos
    }

    private void updateGame() {
        // Asignar clientes a meseros disponibles
        for (Waiter waiter : waiters) {
            if (waiter.isAvailable()) {
                Client client = restaurantMonitor.getWaitingQueue().poll();
                if (client != null) {
                    int tableNumber = restaurantMonitor.enterRestaurant(client);
                    if (tableNumber != -1) {
                        handleClient(waiter, client, tableNumber);
                    }
                }
            }
        }

        // Verificar si los chefs tienen órdenes que cocinar
        for (Chef chef : chefs) {
            Order orderToCook = restaurantMonitor.getOrderBuffer().getOrderToCook();
            if (orderToCook != null) {
                handleChef(chef, orderToCook);
            } else {
                System.out.println("Chef " + chef.getId() + " no encontró órdenes para cocinar.");
            }
        }
    }


    private void handleClient(Waiter waiter, Client client, int tableNumber) {
        System.out.println("Waiter " + waiter.getId() + " handling client " + client.getId() + " at table " + tableNumber);
            System.out.println("Request Order to Client" + client.getId());
            int randomId = (int) (FXGL.random() * 1000);
            Order order = new Order(randomId * 1000, client.getId());
            restaurantMonitor.getOrderBuffer().addOrder(order);
            System.out.println("Order created: " + order.getOrderId() + " by client " + client.getId());

    }

    private void handleChef(Chef chef, Order order) {
        System.out.println("Chef " + chef.getId() + " preparing order " + order.getOrderId());

            restaurantMonitor.getOrderBuffer().completeOrder(order);
            System.out.println("Order completed: " + order.getOrderId() + " by chef " + chef.getId());
        // Simulate 2 seconds cooking time
    }

    private void startClientGenerator() {
        System.out.println("Starting client generator...");

            System.out.println("Checking client generator conditions...");
            if (waitingClients.size() < RESTAURANT_CAPACITY * 2 && restaurantMonitor.getWaitingQueue().size() < RESTAURANT_CAPACITY) {
                Client newClient = new Client(waitingClients.size());
                waitingClients.add(newClient);
                boolean added = restaurantMonitor.getWaitingQueue().offer(newClient);
                if (added) {
                    ChairView.createCustomerEntity(newClient);
                    System.out.println("New client generated: " + newClient.getId());
                } else {
                    System.out.println("Failed to add client " + newClient.getId() + " to waiting queue");
                }
            } else {
                System.out.println("Client generator skipped (queue full or waiting clients maxed out)");
            }

    }

    private double getPoissonRandomTime() {
        double lambda = 1.0; // Reducido para generar clientes más rápido
        return -Math.log(1.0 - Math.random()) / lambda;
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void createBackground() {
        System.out.println("Creating background...");
        try {
            FXGL.entityBuilder()
                    .at(0, 0)
                    .view("guiaFondo.png")
                    .buildAndAttach();
            System.out.println("Background created successfully");
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
        }
    }

    private void createKitchenAndWaitingArea() {
        System.out.println("Creating kitchen and waiting area...");
        FXGL.entityBuilder()
                .at(WINDOW_WIDTH - 180, 250)
                .view(new Rectangle(150, 200, Color.rgb(255, 255, 224, 0.5)))
                .buildAndAttach();

        FXGL.entityBuilder()
                .at(50, 150)
                .view(new Rectangle(150, 200, Color.rgb(173, 216, 230, 0.5)))
                .buildAndAttach();

        System.out.println("Kitchen and waiting area created");
    }

    private void createTables() {
        System.out.println("Creating tables...");
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
        System.out.println("Tables created: " + tables.size());
    }

    private void createStaff() {
        System.out.println("Creating staff...");
        for (int i = 0; i < NUM_WAITERS; i++) {
            Waiter waiter = new Waiter(i);
            waiters.add(waiter);
            FXGL.spawn("waiter", 100, 300 + i * 50);
        }
        System.out.println("Waiters created: " + waiters.size());

        for (int i = 0; i < NUM_CHEFS; i++) {
            Chef chef = new Chef(i);
            chefs.add(chef);
            FXGL.spawn("chef", WINDOW_WIDTH - 150, 100 + i * 50);
        }
        System.out.println("Chefs created: " + chefs.size());
    }
    private void initializeGameComponents() {
        System.out.println("Initializing game components...");
        tables = new ArrayList<>();
        waiters = new ArrayList<>();
        chefs = new ArrayList<>();
        waitingClients = new ArrayList<>();
        FXGL.getGameWorld().addEntityFactory(new ChairView());
        System.out.println("Game components initialized");
    }
}
