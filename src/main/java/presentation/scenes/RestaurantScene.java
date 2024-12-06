package presentation.scenes;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import domain.components.ChefComponent;
import domain.components.ClientComponent;
import domain.components.WaiterComponent;
import domain.entities.*;
import domain.observer.ClientObserver;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import domain.monitors.RestaurantMonitor;
import presentation.views.ChairView;
import utils.IClientLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RestaurantScene extends GameApplication {

    private static final int RESTAURANT_CAPACITY = 10;
    private static final int NUM_WAITERS = 2;
    private static final int NUM_CHEFS = 3;
    private static final int WINDOW_WIDTH = 512;
    private static final int WINDOW_HEIGHT = 384;

    private RestaurantMonitor restaurantMonitor;
    private List<Entity> tables;
    private Recepcionist receptionist;
    private List<Chair> chairs;
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
        waiters = createWaiters(NUM_WAITERS);
        chefs = createChefs(NUM_CHEFS);
        restaurantMonitor = new RestaurantMonitor(RESTAURANT_CAPACITY, waiters.get(0));
        createTables();
        createChairs();
        createRecepcionist();
        generateInitialClients();
        startGameLoop();
        System.out.println("Game initialized successfully");
    }
    private void createRecepcionist(){
        Point2D position = new Point2D(290, 340);
        SpawnData spawnData = new SpawnData(position);
        receptionist = new Recepcionist(position);
        FXGL.spawn("receptionist", spawnData);
    }
    private List<Chef> createChefs(int numChefs) {
        List<Chef> chefsList = new ArrayList<>();
        for (int i = 0; i < numChefs; i++) {
            Chef chef = new Chef(i);
            chef.setPosition(new Point2D(220 + i * 30, 100));
            chefsList.add(chef);
            SpawnData spawnData = new SpawnData(220 + i * 30, 100)
                    .put("id", i);

            FXGL.spawn("chef", spawnData);
            System.out.println("Chef created: " + chef.getId());
        }
        return chefsList;
    }

    private void generateInitialClients() {
        System.out.println("Generating initial clients...");
        for (int i = 0; i < 25; i++) {
            Client newClient = new Client(i);
            waitingClients.add(newClient);
            ClientComponent clientComponent = new ClientComponent();
            IClientLogger clientLogger = new IClientLogger();
            ClientObserver clientObserver = new ClientObserver(newClient);
            restaurantMonitor.addObserver(clientObserver);
            clientComponent.addObserver(clientLogger);
            newClient.setComponent(clientComponent);
            boolean added = restaurantMonitor.getWaitingQueue().offer(newClient);
            if (added) {
                SpawnData spawnData = new SpawnData(310, 340 + i * 25)
                        .put("id", i);
                FXGL.spawn("customer", spawnData);
                newClient.setPosition(new Point2D(310, 340 + i * 25));
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
            waiter.setPosition(new Point2D(390 + i * 25, 330));
            waitersList.add(waiter);
            SpawnData spawnData = new SpawnData(390 + i * 25, 330)
                    .put("id", i);

            FXGL.spawn("waiter", spawnData);
        }
        return waitersList;
    }

    private void startGameLoop() {
        FXGL.run(() -> updateGame(), Duration.seconds(5));
    }

    private void updateGame() {
        for (Waiter waiter : waiters) {
            if (!waiter.isAvailable()) {
                continue;
            }

            if (receptionist.isAvailable()) {
                Client client = restaurantMonitor.getWaitingQueue().poll();
                if (client != null) {
                    int tableNumber = restaurantMonitor.enterRestaurant(client); /*Aquí lo hace instantaneo
                    Debería de cambiarse la lógica a como en el handleClient, donde si existe
                    un pausado de 3 segundos en cada acción.
                    */
                    System.out.println("Client " + waiter.getId() + " entered restaurant " + tableNumber);
                    if (tableNumber != -1) {
                        receptionist.attendCustomer(client);
                        System.out.println("Client " + client.getId() + " seated at table " + tableNumber);
                        restaurantMonitor.notifyObservers("Cliente " + client.getId() + " fue sentado en la mesa " + tableNumber);
                        client.getComponent().moveClientOneStep(client);
                        System.out.println("[CLIENTE] Me sente en la [SILLA]: "+ client.getPosition());
                        receptionist.finishService();
                        handleClient(waiter, client, tableNumber);
                    } else {
                        restaurantMonitor.notifyObservers("Cliente " + client.getId() + " fue puesto en espera.");
                        System.out.println("No tables available for client " + client.getId() + ". Adding to waiting list.");
                    }
                }
            }
        }
    }

    private void handleClient(Waiter waiter, Client client, int tableNumber) {
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Waiter " + waiter.getId() + " handling client " + client.getId() + " at table " + tableNumber);
                WaiterComponent.moveWaiterTo(waiter, "Left", client.getPosition().add(50, 0)); //Se mueve a los de la cola
                System.out.println("Estoy en el primer asyncrono");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRunAsync(() -> {
            try {
                pause(3); // Pause 3 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRunAsync(() -> {
            try {
                waiter.attendCustomer(client);
                System.out.println("Requesting order from client " + client.getId());
                System.out.println("Estoy en el SEGUNDO asyncrono");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRunAsync(() -> {
            try {
                pause(3); // Pause 3 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRunAsync(() -> {
            try {
                WaiterComponent.moveWaiterTo(waiter, "Up", new Point2D(250, 150));
                System.out.println("[MESERO RESTAURANTE] Estoy en: " + waiter.getPosition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRunAsync(() -> {
            try {
                pause(3); // Pause 3 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).thenRunAsync(() -> {
            try {
                Chef chef = findAvailableChef();
                Order order = waiter.serveClient(client, tableNumber, restaurantMonitor);
                if (chef != null) {
                    chef.startCooking(order);
                    ChefComponent.moveChefTo(chef, "Up", chef.getPosition().add(-25, -25));
                    chef.cook(restaurantMonitor);
                    ChefComponent.moveChefTo(chef, "Down", chef.getPosition().add(25, 25));
                    waiter.takeOrder(restaurantMonitor);
                    WaiterComponent.moveWaiterTo(waiter, "Left", client.getPosition().add(20, 0)); //Aquí SI se pone frente a los que se sentaron
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Método para simular pausas (sin bloquear)
    private void pause(int seconds) {
        try {
            System.out.println("[PAUSA] ME PAUSE EN (" + seconds + ")");
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Pause interrupted: " + e.getMessage());
        }
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
            double colX = startX + col * spacing;
            double rowY = startY + row * spacing;
            Entity table = gameFactory.createTable(
                    colX,
                    rowY);
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
            int row = i / 4;
            int col = i % 4;
            double x = startX + col * spacing;
            double y = startY + row * spacing;
            Chair chair = new Chair(i, x, y);
            chairs.add(chair);
            SpawnData spawnData = new SpawnData(x,y)
                    .put("id", i);
            FXGL.spawn("chair", spawnData);
            restaurantMonitor.getTable(i).setChair(chair);
            System.out.println("[SILLA CREADA] Mi posicion es: " + restaurantMonitor.getTable(i).getChair().getPosition());
        }
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
