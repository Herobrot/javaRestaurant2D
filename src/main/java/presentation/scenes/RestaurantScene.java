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

    private static final int RESTAURANT_CAPACITY = 10;
    private static final int NUM_WAITERS = 2;
    private static final int NUM_CHEFS = 3;
    private static final int WINDOW_WIDTH = 512;
    private static final int WINDOW_HEIGHT = 384;

    private RestaurantMonitor restaurantMonitor;
    private List<Entity> tables;
    private List<Entity> chairs;
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
        createTables();
        createChairs();
        waiters = createWaiters(NUM_WAITERS);
        chefs = createChefs(NUM_CHEFS);
        restaurantMonitor = new RestaurantMonitor(RESTAURANT_CAPACITY, waiters.get(0));
        generateInitialClients();
        startGameLoop();
        System.out.println("Game initialized successfully");
    }
    private List<Chef> createChefs(int numChefs) {
        List<Chef> chefsList = new ArrayList<>();
        for (int i = 0; i < numChefs; i++) {
            Chef chef = new Chef(i);  // Suponiendo que el constructor de Chef usa un ID
            chefsList.add(chef);
            FXGL.spawn("chef", 500, 300 + i * 50);
            System.out.println("Chef created: " + chef.getId());
        }
        return chefsList;
    }

    private void generateInitialClients() {
        System.out.println("Generating initial clients...");
        for (int i = 0; i < 5; i++) {
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
        for (int i = 0; i < numWaiters; i++) {
            Waiter waiter = new Waiter(i);
            waitersList.add(waiter);
            FXGL.spawn("waiter", 100, 300 + i * 50);
        }
        return waitersList;
    }

    private void startGameLoop() {
        FXGL.run(() -> updateGame(), Duration.seconds(0.5)); // Updates every 0.5 seconds
    }

    private void updateGame() {
        for (Waiter waiter : waiters) {
            if (!waiter.isAvailable()) {
                continue; // Skip if the waiter is not available
            }

            Client client = restaurantMonitor.getWaitingQueue().poll();
            if (client != null) {
                int tableNumber = restaurantMonitor.enterRestaurant(client);
                if (tableNumber != -1) {
                    handleClient(waiter, client, tableNumber);
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
                    .view("background.png")
                    .scale(1.50,1.40)
                    .buildAndAttach();
            System.out.println("Background created successfully");
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
        }
    }

    private void createTables() {
        System.out.println("Creating tables...");
        ChairView gameFactory = new ChairView();
        int startX = 131;
        int startY = 229;
        int spacing = 60;

        for (int i = 0; i < RESTAURANT_CAPACITY; i++) {
            int row = i / 4;
            int col = i % 4;
            Entity table = gameFactory.createTable(
                    startX + col * spacing,
                    startY + row * spacing);
            tables.add(table);
            FXGL.getGameWorld().addEntity(table);
        }
        System.out.println("Tables created: " + tables.size());
    }

    private void createChairs(){
        System.out.println("Creando sillas...");
        ChairView gameFactory = new ChairView();
        int startX = 111;
        int startY = 229;
        int spacing = 60;

        for (int i = 0; i < RESTAURANT_CAPACITY; i++) {
            System.out.println("Entre " + i+1 + " vez al for");
            int row = i / 4;
            int col = i % 4;
            Entity chair = gameFactory.createChair(
                    startX + col * spacing,
                    startY + row * spacing);
            System.out.println("Creé la entidad");
            chairs.add(chair);
            System.out.println("Agregue a la lista");
            FXGL.getGameWorld().addEntity(chair);
        }
        System.out.println("Sillas creadas: " + chairs.size());
    }
    private void initializeGameComponents() {
        System.out.println("Initializing game components...");
        tables = new ArrayList<>();
        chairs = new ArrayList<>();
        waiters = new ArrayList<>();
        chefs = new ArrayList<>();
        waitingClients = new ArrayList<>();
        FXGL.getGameWorld().addEntityFactory(new ChairView());
        System.out.println("Game components initialized");
    }
}
