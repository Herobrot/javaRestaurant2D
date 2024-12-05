package domain.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.entities.Client;
import domain.observer.IClientObserver;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import presentation.views.TypeGame;

import java.util.ArrayList;
import java.util.List;

public class ClientComponent extends Component {
    private final List<IClientObserver> observers = new ArrayList<>();

    public void addObserver(IClientObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IClientObserver observer) {
        observers.remove(observer);
    }

    private void notifyPositionChanged(int clientId, Point2D newPosition) {
        for (IClientObserver observer : observers) {
            observer.onClientPositionChanged(clientId, newPosition);
        }
    }

    private void notifyDirectionChanged(int clientId, Direction newDirection) {
        for (IClientObserver observer : observers) {
            observer.onClientDirectionChanged(clientId, newDirection);
        }
    }

    private static Direction calculateDirection(Point2D from, Point2D to, Client.ClientState clientState) {
        Point2D delta = to.subtract(from).normalize();

        if (clientState == Client.ClientState.WAITING_FOR_FOOD || clientState == Client.ClientState.EATING) {
            return Direction.RIGHT;
        }

        if (delta.equals(new Point2D(0, -1))) return Direction.UP;
        if (delta.equals(new Point2D(0, 1))) return Direction.DOWN;
        if (delta.equals(new Point2D(-1, 0))) return Direction.LEFT;
        if (delta.equals(new Point2D(1, 0))) return Direction.RIGHT;

        throw new IllegalArgumentException("Dirección no válida: " + delta);
    }

    public void moveClientOneStep(Client client){
        if (client.getState() == Client.ClientState.WAITING_FOR_WAITER){
            var posTo = client.getPosition().add(0, -50);
            Direction direction = calculateDirection(client.getPosition(), posTo, client.getState());
            client.setDirection(direction);
            updateTextureBasedOnDirection(direction, client);

            moveEntityToPosition(posTo, client);
        }
    }
    public void moveClientAlongRoute(Client client, List<Point2D> route) {
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D current = route.get(i);
            Point2D next = route.get(i + 1);

            Direction direction = calculateDirection(current, next, client.getState());
            client.setDirection(direction);


            updateTextureBasedOnDirection(direction, client);
            client.setPosition(next);

            moveEntityToPosition(next, client);
            //El cliente va al primer nodo, dejando un espacio en la cola. Se pone el if
            // ya qué cuando avance para el siguiente nodo, terminaría haciendo que los otros observando en cola avancen
            // otro paso cuando aún no hay espacio.
            if(i==0){
                notifyDirectionChanged(client.getId(), direction);
                notifyPositionChanged(client.getId(), next);
            }
        }
    }

    private static void updateTextureBasedOnDirection(Direction direction, Client client) {
        String textureFile = switch (direction) {
            case UP -> "clientLookingUp.png";
            case DOWN -> "clientLookingDown.png";
            case LEFT -> "clientLookingLeft.png";
            case RIGHT -> "clientLookingRight.png";
        };

        Texture newTexture = FXGL.texture(textureFile).copy();
        newTexture.setFitWidth(24);
        newTexture.setFitHeight(24);

        FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(entity -> entity.getInt("id") == client.getId())
                .findFirst()
                .ifPresent(entity -> {
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

    private static void moveClientToTable(Client client) {
        Entity e = FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(entity -> entity.getInt("id") == client.getId())
                .findFirst()
                .get();

    }
}
