package domain.entities;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
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

    public Customer(int id, RestaurantMonitor restaurantMonitor, OrderQueueMonitor orderQueueMonitor,
                    CustomerQueueMonitor customerQueueMonitor, CustomerStats customerStats, List<Entity> tables) {
        this.id = id;
        this.restaurantMonitor = restaurantMonitor;
        this.orderQueueMonitor = orderQueueMonitor;
        this.customerQueueMonitor = customerQueueMonitor;
        this.customerStats = customerStats;
        this.tables = tables;
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
            System.out.println("Cliente " + id + " recibiendo asignación de mesa " + tableNumber);
            this.tableNumber = tableNumber;
            state = CustomerState.MOVING_TO_TABLE;
            Point2D tablePos = calculateTablePosition(tableNumber);

            // Asignar cliente a la mesa antes de moverse
            for (Entity tableEntity : tables) {
                Table table = tableEntity.getComponent(Table.class);
                if (table != null && table.getNumber() == tableNumber) {
                    System.out.println("Cliente " + id + " asignado a mesa " + tableNumber);
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
                    System.out.println("Cliente " + id + " esperando al mesero en mesa " + tableNumber);
                    notifyWaiter();
                }
            });
        }
    }

    public void startEating() {
        synchronized (stateLock) {
            if (state != CustomerState.WAITING_FOR_FOOD) {
                System.out.println("Error: Cliente " + id + " intentando comer en estado incorrecto: " + state);
                return;
            }

            System.out.println("Cliente " + id + " comenzando a comer en mesa " + tableNumber);
            state = CustomerState.EATING;
            customerStats.decrementWaitingForFood();
            customerStats.incrementEating();

            // Thread para simular el tiempo de comida
            Thread eatingThread = new Thread(() -> {
                try {
                    long eatingTime = ThreadLocalRandom.current().nextLong(
                            GameConfig.MIN_EATING_TIME,
                            GameConfig.MAX_EATING_TIME
                    );
                    System.out.println("Cliente " + id + " comerá por " + eatingTime + "ms");
                    Thread.sleep(eatingTime);

                    synchronized (stateLock) {
                        if (state == CustomerState.EATING) {
                            System.out.println("Cliente " + id + " terminó de comer, procediendo a salir");
                            leaveRestaurant();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Cliente " + id + " interrumpido mientras comía");
                }
            });
            eatingThread.start();
        }
    }

    private void leaveRestaurant() {
        synchronized (stateLock) {
            if (tableNumber != -1) {
                // Aseguramos que las actualizaciones de UI ocurran en el hilo de JavaFX
                Platform.runLater(() -> customerStats.decrementEating());
                restaurantMonitor.releaseTable(tableNumber);

                // Liberar la mesa
                for (Entity tableEntity : tables) {
                    Table table = tableEntity.getComponent(Table.class);
                    if (table != null && table.getNumber() == tableNumber) {
                        System.out.println("Cliente " + id + " liberando mesa " + tableNumber);
                        table.release();
                        break;
                    }
                }
            }

            state = CustomerState.LEAVING;
            Point2D exitPos = new Point2D(GameConfig.ENTRANCE_X, GameConfig.ENTRANCE_Y);

            // Aseguramos que la eliminación de la entidad ocurra en el hilo de JavaFX
            movement.moveTo(exitPos, () ->
                    Platform.runLater(() -> {
                        synchronized (stateLock) {
                            System.out.println("Cliente " + id + " abandonando el restaurante");
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
            System.out.println("Cliente " + id + " notificando al mesero para ordenar");
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

}