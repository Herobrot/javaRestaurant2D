package domain.entities;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import components.MovementComponent;
import domain.entities.Customer;
import domain.entities.Table;
import domain.models.Order;
import domain.monitors.OrderQueueMonitor;
import domain.monitors.CustomerQueueMonitor;
import utils.GameConfig;
import javafx.geometry.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

public class Waiter extends Component {
    private static final AtomicInteger orderIdGenerator = new AtomicInteger(0);
    private final Object stateLock = new Object();
    private final OrderQueueMonitor orderQueueMonitor;
    private final CustomerQueueMonitor customerQueueMonitor;
    private final Point2D restPosition;
    private Point2D targetPosition;
    private boolean isMoving = false;
    private boolean isBusy = false;
    private WaiterState state = WaiterState.RESTING;
    private static final double SPEED = GameConfig.WAITER_SPEED;
    private final List<Entity> tables;
    private MovementComponent movement;

    public enum WaiterState {
        RESTING,
        MOVING_TO_TABLE,
        TAKING_ORDER,
        MOVING_TO_KITCHEN,
        DELIVERING_ORDER,
        RETURNING_TO_REST
    }

    public Waiter(int id, OrderQueueMonitor orderQueueMonitor, CustomerQueueMonitor customerQueueMonitor,
                  Point2D restPosition, List<Entity> tables) {
        this.orderQueueMonitor = orderQueueMonitor;
        this.customerQueueMonitor = customerQueueMonitor;
        this.restPosition = restPosition;
        this.tables = tables;
    }

    @Override
    public void onAdded() {
        movement = entity.getComponent(MovementComponent.class);
        entity.setPosition(restPosition);
        startWaiterBehavior();
    }

    private void startWaiterBehavior() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (stateLock) {
                        if (!isBusy) {
                            // Primero intentar entregar órdenes listas
                            if (!deliverReadyOrders()) {
                                // Si no hay órdenes para entregar, atender nuevos clientes
                                if (customerQueueMonitor.hasWaitingCustomers()) {
                                    CustomerQueueMonitor.CustomerRequest request = customerQueueMonitor.getNextCustomer();
                                    if (request != null) {
                                        isBusy = true;
                                        serveCustomer(request.customer, request.tableNumber);
                                    }
                                }
                            }
                        }
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private boolean deliverReadyOrders() throws InterruptedException {
        for (Entity tableEntity : tables) {
            Table table = tableEntity.getComponent(Table.class);
            if (table != null) {
                Order readyOrder = orderQueueMonitor.checkReadyOrder(table.getNumber());
                if (readyOrder != null) {
                    deliverOrder(readyOrder);
                    return true;
                }
            }
        }
        return false;
    }

    private void serveCustomer(Customer customer, int tableNumber) {
        state = WaiterState.MOVING_TO_TABLE;
        Point2D tablePos = calculateTablePosition(tableNumber);

        movement.moveTo(tablePos, () -> {
            state = WaiterState.TAKING_ORDER;
            Order order = new Order(orderIdGenerator.incrementAndGet(), tableNumber);

            Point2D kitchenPos = new Point2D(GameConfig.KITCHEN_X, GameConfig.KITCHEN_Y+50);
            movement.moveTo(kitchenPos, () -> {
                orderQueueMonitor.addOrder(order);
                synchronized (stateLock) {
                    state = WaiterState.RESTING;
                    isBusy = false;
                }
            });
        });
    }

    private void deliverOrder(Order order) {
        synchronized (stateLock) {
            isBusy = true;
            state = WaiterState.DELIVERING_ORDER;
            System.out.println("Mesero entregando orden a mesa " + order.getTableNumber());

            Point2D tablePos = calculateTablePosition(order.getTableNumber());
            movement.moveTo(tablePos, () -> {
                for (Entity tableEntity : tables) {
                    Table table = tableEntity.getComponent(Table.class);
                    if (table != null && table.getNumber() == order.getTableNumber()) {
                        Customer customer = table.getCurrentCustomer();
                        if (customer != null) {
                            System.out.println("Mesero encontró cliente en mesa " + order.getTableNumber());
                            customer.startEating();
                        } else {
                            System.out.println("Error: No se encontró cliente en mesa " + order.getTableNumber());
                        }
                        break;
                    }
                }
                state = WaiterState.RESTING;
                isBusy = false;
            });
        }
    }

    private Point2D calculateTablePosition(int tableNumber) {
        int row = tableNumber / GameConfig.TABLES_PER_ROW;
        int col = tableNumber % GameConfig.TABLES_PER_ROW;
        return new Point2D(
                GameConfig.TABLES_START_X + (col * GameConfig.TABLE_SPACING_X) + GameConfig.CUSTOMER_OFFSET_X,
                (GameConfig.TABLES_START_Y + (row * GameConfig.TABLE_SPACING_Y) + GameConfig.CUSTOMER_OFFSET_Y)-30
        );
    }

    // Método para que el camarero reciba la notificación de una orden lista
    public void notifyOrderReady(Order order) {
        synchronized (stateLock) {
            if (!isBusy) {
                System.out.println("La orden de la mesa " + order.getTableNumber() + " está lista para ser entregada.");
                deliverOrder(order);
            }
        }
    }
}
