package domain.observer;
import domain.entities.Customer;

public class ClientObserver implements Observer {
    private Customer client;

    public ClientObserver(Customer client) {
        this.client = client;
    }

    @Override
    public void update(String message) {
        System.out.println("Client " + client.getTableNumber() + " received message: " + message);
    }
}

