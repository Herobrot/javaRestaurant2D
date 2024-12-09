package domain.entities;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import components.MovementComponent;
import domain.models.CustomerStats;
import domain.monitors.RestaurantMonitor;
import domain.monitors.OrderQueueMonitor;
import domain.monitors.CustomerQueueMonitor;
import utils.GameConfig;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public class Customer extends Component {
    private final RestaurantMonitor restaurantMonitor;
    private final OrderQueueMonitor orderQueueMonitor;
    private final CustomerQueueMonitor customerQueueMonitor;
    private final CustomerStats customerStats;
    private final List<Entity> tables;
    private int tableNumber = -1;
    private Point2D targetPosition;
    private boolean isMoving = false;
    private boolean isWaitingForTable = false;
    private CustomerState state = CustomerState.ENTERING;
    private static final double SPEED = GameConfig.CUSTOMER_SPEED;
    private final int id;
    private final Object stateLock = new Object();
    private MovementComponent movement;
    private final Texture textureLeft;
    private final Texture textureUp;
    private final Texture textureDown;
    private final Texture textureRight;

    public enum CustomerState {
        ENTERING,
        WAITING_FOR_RECEPTIONIST,
        TALKING_TO_RECEPTIONIST,
        WAITING_FOR_TABLE,
        MOVING_TO_TABLE,
        WAITING_FOR_WAITER,
        WAITING_FOR_FOOD,
        EATING,
        LEAVING
    }
    public enum DirectionsState{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Customer(int id, RestaurantMonitor restaurantMonitor, OrderQueueMonitor orderQueueMonitor,
                    CustomerQueueMonitor customerQueueMonitor, CustomerStats customerStats, List<Entity> tables) {
        this.id = id;
        this.restaurantMonitor = restaurantMonitor;
        this.orderQueueMonitor = orderQueueMonitor;
        this.customerQueueMonitor = customerQueueMonitor;
        this.customerStats = customerStats;
        this.tables = tables;
        this.textureLeft = spriteBuilder("clientLookingLeft.png");
        this.textureDown = spriteBuilder("clientLookingDown.png");
        this.textureUp = spriteBuilder("clientLookingUp.png");
        this.textureRight = spriteBuilder("clientLookingRight.png");
    }

    @Override
    public void onAdded() {
        movement = entity.getComponent(MovementComponent.class);
        entity.setPosition(GameConfig.ENTRANCE_X, GameConfig.ENTRANCE_Y);
        moveToReceptionist();
    }

    private void moveToReceptionist() {
        synchronized (stateLock) {
            state = CustomerState.ENTERING;
            Point2D receptionistPos = new Point2D(
                    GameConfig.RECEPTIONIST_X - GameConfig.SPRITE_SIZE * 2,
                    GameConfig.RECEPTIONIST_Y
            );

            movement.moveTo(receptionistPos, () -> {
                synchronized (stateLock) {
                    state = CustomerState.WAITING_FOR_RECEPTIONIST;
                    Entity receptionistEntity = findReceptionist();
                    if (receptionistEntity != null) {
                        Receptionist receptionist = receptionistEntity.getComponent(Receptionist.class);
                        receptionist.addCustomerToQueue(this);
                    }
                }
            });
        }
    }

    public void assignTable(int tableNumber) {
        synchronized (stateLock) {
            System.out.println("Customer " + id + " receiving table assignment " + tableNumber);
            this.tableNumber = tableNumber;
            state = CustomerState.MOVING_TO_TABLE;
            Point2D tablePos = calculateTablePosition(tableNumber);

            // Assign customer to table before moving
            for (Entity tableEntity : tables) {
                Table table = tableEntity.getComponent(Table.class);
                if (table != null && table.getNumber() == tableNumber) {
                    System.out.println("Customer " + id + " assigned to table " + tableNumber);
                    table.setCurrentCustomer(this);
                    break;
                }
            }

            movement.moveTo(tablePos, () -> {
                synchronized (stateLock) {
                    if (isWaitingForTable) {
                        customerStats.decrementWaitingForTable();
                        isWaitingForTable = false;
                    }
                    state = CustomerState.WAITING_FOR_WAITER;
                    System.out.println("Customer " + id + " waiting for the waiter at table " + tableNumber);
                    notifyWaiter();
                }
            });
        }
    }

    public void startEating() {
        synchronized (stateLock) {
            if (state != CustomerState.WAITING_FOR_FOOD) {
                System.out.println("Error: Customer " + id + " attempting to eat in an incorrect state: " + state);
                return;
            }

            System.out.println("Customer " + id + " starting to eat at table " + tableNumber);
            state = CustomerState.EATING;
            customerStats.decrementWaitingForFood();
            customerStats.incrementEating();

            // Thread to simulate meal time
            Thread eatingThread = new Thread(() -> {
                try {
                    long eatingTime = ThreadLocalRandom.current().nextLong(
                            GameConfig.MIN_EATING_TIME,
                            GameConfig.MAX_EATING_TIME
                    );
                    System.out.println("Customer " + id + " will eat for " + eatingTime + "ms");
                    Thread.sleep(eatingTime);

                    synchronized (stateLock) {
                        if (state == CustomerState.EATING) {
                            System.out.println("Customer " + id + " finished eating, proceeding to leave");
                            updateTexture(DirectionsState.DOWN);
                            leaveRestaurant();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Customer " + id + " was interrupted while eating");
                }
            });
            eatingThread.start();
        }
    }

    private void leaveRestaurant() {
        synchronized (stateLock) {
            if (tableNumber != -1) {
                // Ensure that UI updates occur in the JavaFX thread.
                Platform.runLater(() -> customerStats.decrementEating());
                restaurantMonitor.releaseTable(tableNumber);

                // Freeing the table
                for (Entity tableEntity : tables) {
                    Table table = tableEntity.getComponent(Table.class);
                    if (table != null && table.getNumber() == tableNumber) {
                        System.out.println("Customer " + id + " freeing table " + tableNumber);
                        table.release();
                        break;
                    }
                }
            }

            state = CustomerState.LEAVING;
            Point2D exitPos = new Point2D(GameConfig.ENTRANCE_X, GameConfig.ENTRANCE_Y);

            // We ensure that entity deletion occurs in the JavaFX thread.
            movement.moveTo(exitPos, () ->
                    Platform.runLater(() -> {
                        synchronized (stateLock) {
                            System.out.println("Customer " + id + " leaving the restaurant");
                            entity.removeFromWorld();
                        }
                    })
            );
        }
    }


    @Override
    public void onUpdate(double tpf) {
        if (isMoving && targetPosition != null) {
            Point2D currentPos = entity.getPosition();
            Point2D direction = targetPosition.subtract(currentPos);

            if (direction.magnitude() < SPEED * tpf) {
                entity.setPosition(targetPosition);
                onTargetReached();
                return;
            }

            direction = direction.normalize().multiply(SPEED * tpf);
            entity.translate(direction.getX(), direction.getY());
        }
    }

    private void onTargetReached() {
        isMoving = false;
        targetPosition = null;

        switch (state) {
            case ENTERING:
                synchronized (stateLock) {
                    state = CustomerState.WAITING_FOR_RECEPTIONIST;
                    Entity receptionistEntity = findReceptionist();
                    if (receptionistEntity != null) {
                        Receptionist receptionist = receptionistEntity.getComponent(Receptionist.class);
                        receptionist.addCustomerToQueue(this);
                    }
                }
                break;
            case MOVING_TO_TABLE:
                synchronized (stateLock) {
                    if (isWaitingForTable) {
                        customerStats.decrementWaitingForTable();
                        isWaitingForTable = false;
                    }
                    customerStats.incrementWaitingForFood();
                    state = CustomerState.WAITING_FOR_WAITER;
                    notifyWaiter();
                }
                break;
            case LEAVING:
                synchronized (stateLock) {
                    customerStats.decrementEating();
                    entity.removeFromWorld();
                }
                break;
        }
    }

    private Entity findReceptionist() {
        for (Entity entity : entity.getWorld().getEntitiesByComponent(Receptionist.class)) {
            return entity;
        }
        return null;
    }

    public void waitForTable() {
        synchronized (stateLock) {
            state = CustomerState.WAITING_FOR_TABLE;
            isWaitingForTable = true;
        }
    }

    private void notifyWaiter() {
        synchronized (stateLock) {
            System.out.println("Customer " + id + " notifying the waiter to order");
            state = CustomerState.WAITING_FOR_FOOD;
            customerStats.incrementWaitingForFood();
            customerQueueMonitor.addCustomer(this, tableNumber);
        }
    }

    private void moveToTable() {
        targetPosition = calculateTablePosition(tableNumber);
        isMoving = true;

        for (Entity tableEntity : tables) {
            Table table = tableEntity.getComponent(Table.class);
            if (table != null && table.getNumber() == tableNumber) {
                table.setCurrentCustomer(this);
                break;
            }
        }
    }

    private Point2D calculateTablePosition(int tableNumber) {
        int row = tableNumber / GameConfig.TABLES_PER_ROW;
        int col = tableNumber % GameConfig.TABLES_PER_ROW;
        return new Point2D(
                GameConfig.TABLES_START_X + (col * GameConfig.TABLE_SPACING_X) + GameConfig.CUSTOMER_OFFSET_X,
                GameConfig.TABLES_START_Y + (row * GameConfig.TABLE_SPACING_Y) + GameConfig.CUSTOMER_OFFSET_Y
        );
    }


    public CustomerState getState() {
        synchronized (stateLock) {
            return state;
        }
    }

    public int getTableNumber() {
        return tableNumber;
    }

    private Texture spriteBuilder(String imagePath){
        Texture texture = texture(imagePath).copy();
        texture.setFitWidth(24);
        texture.setFitHeight(24);
        texture.setSmooth(true);
        texture.setCache(true);
        return texture;
    }

    private void updateTexture(DirectionsState direction) {
        Platform.runLater(() -> {
            synchronized (stateLock) {
                switch (direction) {
                    case UP:
                        entity.getViewComponent().clearChildren();
                        entity.getViewComponent().addChild(this.textureUp);
                        break;
                    case LEFT:
                        entity.getViewComponent().clearChildren();
                        entity.getViewComponent().addChild(this.textureLeft);
                        break;
                    case RIGHT:
                        entity.getViewComponent().clearChildren();
                        entity.getViewComponent().addChild(this.textureRight);
                        break;
                    case DOWN:
                        entity.getViewComponent().clearChildren();
                        entity.getViewComponent().addChild(this.textureDown);
                        break;
                }
            }
        });
    }

}