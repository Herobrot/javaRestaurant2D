package domain.monitors;

import domain.entities.Client;
import domain.entities.Order;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Monitor principal que maneja la sincronización del restaurante
 * Controla el acceso a las mesas y la cola de espera
 */
public class RestaurantMonitor {
    private final int capacity; // Capacidad total del restaurante
    private final boolean[] tables; // Estado de las mesas (true = ocupada)
    public final Queue<Client> waitingQueue; // Cola de espera de clientes
    private final KitchenMonitor orderBuffer; // Buffer de órdenes

    public RestaurantMonitor(int capacity) {
        this.capacity = capacity;
        this.tables = new boolean[capacity];
        this.waitingQueue = new LinkedList<>();
        this.orderBuffer = new KitchenMonitor();
    }

    /**
     * Cliente intentando entrar al restaurante
     *
     * @param Client Cliente que quiere entrar
     * @return número de mesa asignada o -1 si debe esperar
     */
    public synchronized int enterRestaurant(Client Client) {
        // Buscar mesa libre
        for (int i = 0; i < tables.length; i++) {
            if (!tables[i]) {
                tables[i] = true; // Ocupar mesa
                return i;
            }
        }
        // Si no hay mesas libres, añadir a la cola de espera
        waitingQueue.add(Client);
        return -1;
    }

    /**
     * Cliente saliendo del restaurante
     *
     * @param tableNumber Número de mesa que se desocupa
     */
    public synchronized void leaveRestaurant(int tableNumber) {
        tables[tableNumber] = false; // Liberar mesa

        // Si hay clientes esperando, asignar la mesa al siguiente
        if (!waitingQueue.isEmpty()) {
            Client nextClient = waitingQueue.poll();
            tables[tableNumber] = true;
            notifyClient(nextClient, tableNumber);
        }
    }

    /**
     * Notificar a un cliente que ya tiene mesa
     *
     * @param client    Cliente a notificar
     * @param tableNumber Número de mesa asignada
     */
    private void notifyClient(Client client, int tableNumber) {
        client.setTableNumber(tableNumber);
        client.setState(Client.ClientState.WAITING_FOR_WAITER);
    }

    /**
     * Obtener el buffer de órdenes
     */
    public KitchenMonitor getOrderBuffer() {
        return orderBuffer;
    }

    // Métodos adicionales

    /**
     * Agregar una orden al buffer.
     *
     * @param order Orden que se va a agregar.
     */
    public synchronized void addOrder(Order order) {
        orderBuffer.addOrder(order);
    }

    /**
     * Tomar una orden del buffer para cocinar.
     *
     * @return Orden para cocinar, o null si no hay.
     */
    public synchronized Order getOrderToCook() {
        return orderBuffer.getOrderToCook();
    }

    /**
     * Completar una orden que fue cocinada.
     *
     * @param order Orden completada.
     */
    public synchronized void completeOrder(Order order) {
        orderBuffer.completeOrder(order);
    }

    /**
     * Verificar si hay mesas disponibles.
     *
     * @return true si hay al menos una mesa disponible, false de lo contrario.
     */
    public synchronized boolean hasAvailableTables() {
        for (boolean table : tables) {
            if (!table) {
                return true;
            }
        }
        return false;
    }
    /**
     * Obtener la cola de espera de clientes.
     *
     * @return Cola de clientes que están esperando mesa.
     */
    public synchronized Queue<Client> getWaitingQueue() {
        return waitingQueue;
    }
}
