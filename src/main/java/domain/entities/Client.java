package domain.entities;

/**
 * Clase que representa a un cliente en el restaurante
 * Cada cliente tiene un ID único y diferentes estados posibles
 */
public class Client {
    // Enumeración para los posibles estados de un cliente
    public enum ClientState {
        WAITING_FOR_TABLE, // Esperando mesa
        WAITING_FOR_WAITER, // Esperando ser atendido
        ORDERING, // Ordenando comida
        WAITING_FOR_FOOD, // Esperando la comida
        EATING, // Comiendo
        LEAVING // Saliendo del restaurante
    }

    private final int id; // Identificador único del cliente
    private ClientState state; // Estado actual del cliente
    private long arrivalTime; // Tiempo de llegada al restaurante
    private int tableNumber; // Número de mesa asignada
    private Order currentOrder; // Orden actual del cliente

    // Constructor
    public Client(int id) {
        this.id = id;
        this.state = ClientState.WAITING_FOR_TABLE;
        this.arrivalTime = System.currentTimeMillis();
    }

    // Métodos para obtener y modificar el estado del cliente
    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    // Otros métodos getter y setter
    public int getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }
}