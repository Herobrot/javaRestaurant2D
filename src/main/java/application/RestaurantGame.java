package application;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import components.BackgroundComponent;
import utils.GameConfig;
import domain.entities.*;
import domain.models.CustomerStats;
import domain.monitors.*;
import utils.PoissonDistribution;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RestaurantGame {
    private final GameWorld gameWorld;
    private final CustomerStats customerStats;
    private final RestaurantMonitor restaurantMonitor;
    private final OrderQueueMonitor orderQueueMonitor;
    private final CustomerQueueMonitor customerQueueMonitor;
    private final List<Entity> tables;
    private final List<Thread> cookThreads;
    private ScheduledExecutorService customerSpawner;
    private int customerIdCounter;
    private final PoissonDistribution poissonDistribution;
    private Entity receptionistEntity;

    public RestaurantGame(GameWorld gameWorld, CustomerStats customerStats) {
        this.gameWorld = gameWorld;
        this.customerStats = customerStats;
        this.restaurantMonitor = new RestaurantMonitor();
        this.orderQueueMonitor = new OrderQueueMonitor();
        this.customerQueueMonitor = new CustomerQueueMonitor();
        this.tables = new ArrayList<>();
        this.cookThreads = new ArrayList<>();
        this.poissonDistribution = new PoissonDistribution(1);
        this.customerIdCounter = 0;
    }

    public Entity createBackground() {
        InputStream is = getClass().getResourceAsStream("/image/background.png");
        Image backgroundImage = new Image(is);
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(GameConfig.WINDOW_WIDTH);
        backgroundImageView.setFitHeight(GameConfig.WINDOW_HEIGHT);
        backgroundImageView.setPreserveRatio(false);
    
        AnchorPane root = new AnchorPane();
        AnchorPane.setTopAnchor(backgroundImageView, 0.0);
        AnchorPane.setLeftAnchor(backgroundImageView, 0.0);
        root.getChildren().add(backgroundImageView);
        root.setPrefSize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
    
        Entity backgroundEntity = new Entity();
        backgroundEntity.addComponent(new BackgroundComponent(root));
    
        return backgroundEntity;
    }

    public void initializeGameComponents() {
        initializeTables();
        initializeReceptionist();
        initializeWaiters();
        initializeCooks();
    }

    private void initializeTables() {
        for (int i = 0; i < GameConfig.TOTAL_TABLES; i++) {
            int row = i / GameConfig.TABLES_PER_ROW;
            int col = i % GameConfig.TABLES_PER_ROW;
            double x = GameConfig.TABLES_START_X + (col * GameConfig.TABLE_SPACING_X);
            double y = GameConfig.TABLES_START_Y + (row * GameConfig.TABLE_SPACING_Y);
            SpawnData data = new SpawnData(x, y);
            data.put("tableNumber",Integer.valueOf(i));
            Entity table = gameWorld.spawn("table", data);
            tables.add(table);
        }
    }


    private void initializeReceptionist() {
        Point2D receptionistPos = new Point2D(GameConfig.RECEPTIONIST_X, GameConfig.RECEPTIONIST_Y);
        Receptionist receptionistComponent = new Receptionist(
                restaurantMonitor,
                receptionistPos,
                customerStats
        );

        SpawnData data = new SpawnData(receptionistPos.getX(), receptionistPos.getY());
        data.put("receptionistComponent", receptionistComponent);
        receptionistEntity = gameWorld.spawn("receptionist", data);
    }

    private void initializeWaiters() {
        for (int i = 0; i < GameConfig.TOTAL_WAITERS; i++) {
            Point2D startPos = new Point2D(
                    GameConfig.WAITER_X,
                    GameConfig.WAITER_Y - ((i + 1) * 1.5)
            );

            Waiter waiter = new Waiter(
                    i,
                    orderQueueMonitor,
                    customerQueueMonitor,
                    startPos,
                    tables
            );

            SpawnData data = new SpawnData(startPos.getX(), startPos.getY());
            data.put("waiterComponent", waiter);
            gameWorld.spawn("waiter", data);
        }
    }

    private void initializeCooks() {
        for (int i = 0; i < GameConfig.TOTAL_COOKS; i++) {
            Cook cook = new Cook(i, orderQueueMonitor);
            SpawnData data = new SpawnData(
                    GameConfig.KITCHEN_X + 30*i,
                    GameConfig.KITCHEN_Y
            );
            data.put("cookComponent", cook);
            Entity cookEntity = gameWorld.spawn("cook", data);

            Thread cookThread = new Thread(cook);
            cookThreads.add(cookThread);
            cookThread.start();
        }
    }

    public void startCustomerGeneration() {
        customerSpawner = Executors.newSingleThreadScheduledExecutor();
        customerSpawner.scheduleAtFixedRate(() -> {
            try {
                generateNewCustomers();
            } catch (Exception e) {
                System.err.println("Error en generador de clientes: " + e.getMessage());
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    private void generateNewCustomers() {
        int numCustomers = poissonDistribution.nextInt();
        for (int i = 0; i < numCustomers; i++) {
            final int currentId = customerIdCounter++;
            runOnce(() -> spawnCustomer(currentId),
                    Duration.seconds(poissonDistribution.nextArrivalTime())
            );
        }
    }

    private void spawnCustomer(int id) {
        Customer customer = new Customer(
                id,
                restaurantMonitor,
                orderQueueMonitor,
                customerQueueMonitor,
                customerStats,
                tables
        );
        SpawnData data = new SpawnData(GameConfig.ENTRANCE_X, GameConfig.ENTRANCE_Y);
        data.put("customerComponent", customer);
        gameWorld.spawn("customer", data);
    }

    public void shutdown() {
        if (customerSpawner != null) {
            customerSpawner.shutdownNow();
        }
        for (Thread cookThread : cookThreads) {
            cookThread.interrupt();
        }
    }
}