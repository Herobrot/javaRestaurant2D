package presentation.events;

import domain.entities.Client;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;

import java.util.List;

public class ClientMoveToTableEvent extends Event {
    public static final EventType<ClientMoveToTableEvent> CLIENT_MOVE = new EventType<>(ANY, "CLIENT_MOVE");

    private final Client client;
    private final List<Point2D> route;

    public ClientMoveToTableEvent(Client client, List<Point2D> route) {
        super(CLIENT_MOVE);
        this.client = client;
        this.route = route;
    }

    public Client getClient() {
        return client;
    }

    public List<Point2D> getRoute() {
        return route;
    }
}
