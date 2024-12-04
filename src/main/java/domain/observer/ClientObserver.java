package domain.observer;
import domain.entities.Client;

public class ClientObserver implements Observer {
    private Client client;

    public ClientObserver(Client client) {
        this.client = client;
    }

    @Override
    public void update(String message) {
        client.getComponent().moveClientOneStep(client);
    }
}

