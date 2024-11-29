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
    private Waiter receptionist;

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
        createTables();
        waiters = createWaiters(NUM_WAITERS);
        chefs = createChefs(NUM_CHEFS);
        restaurantMonitor = new RestaurantMonitor(RESTAURANT_CAPACITY, waiters.get(0));
        receptionist = new Waiter(0);
        generateInitialClients();
        startGameLoop();
        System.out.println("Game initialized successfully");
    }
    private List<Chef> createChefs(int numChefs) {
        List<Chef> chefsList = new ArrayList<>();
        for (int i = 0; i < numChefs; i++) {
            Chef chef = new Chef(i);
            chefsList.add(chef);
            FXGL.spawn("chef", 500, 300 + i * 50);
            System.out.println("Chef created: " + chef.getId());
        }
        return chefsList;
    }

    private void generateInitialClients() {
        System.out.println("Generating initial clients...");
        for (int i = 0; i < 25; i++) {
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

    private List<Waiter> createWaiters(int numWaiters) {
        List<Waiter> waitersList = new ArrayList<>();
        for (int i = 1; i < numWaiters+1; i++) {
            Waiter waiter = new Waiter(i);
            waitersList.add(waiter);
            FXGL.spawn("waiter", 100, 300 + i * 50);
        }
        return waitersList;
    }

    private void startGameLoop() {
        FXGL.run(() -> updateGame(), Duration.seconds(0.5));
    }

    private void updateGame() {
        for (Waiter waiter : waiters) {
            if (!waiter.isAvailable()) {
                continue;
            }

            if (receptionist.isAvailable()) {
                Client client = restaurantMonitor.getWaitingQueue().poll();
                if (client != null) {
                    int tableNumber = restaurantMonitor.enterRestaurant(client);
                    System.out.println("Waiter " + waiter.getId() + " entered restaurant " + tableNumber);
                    if (tableNumber != -1) {
                        receptionist.attendCustomer(client);
                        System.out.println("Client " + client.getId() + " seated at table " + tableNumber);
                        handleClient(receptionist, client, tableNumber);
                    } else {
                        System.out.println("No tables available for client " + client.getId() + ". Adding to waiting list.");
                    }
                }
            }
        }
    }

    private void handleClient(Waiter waiter, Client client, int tableNumber) {
        System.out.println("Waiter " + waiter.getId() + " handling client " + client.getId() + " at table " + tableNumber);
        waiter.attendCustomer(client);
        System.out.println("Requesting order from client " + client.getId());
        Chef chef = findAvailableChef();
        Order order = waiter.serveClient(client, tableNumber, restaurantMonitor);
        if(chef != null){
            chef.startCooking(order);
            chef.cook(restaurantMonitor);
        }
        waiter.takeOrder(restaurantMonitor);
    }
    private Chef findAvailableChef() {
        for (Chef chef : chefs) {
            if (chef.isAvailable()) {
                return chef;
            }
        }
        return null;
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
