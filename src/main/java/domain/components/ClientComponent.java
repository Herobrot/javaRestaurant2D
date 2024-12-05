package domain.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.entities.Client;
import domain.observer.IClientObserver;
import javafx.animation.Interpolator;
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
            Point2D posTo = client.getPosition().add(0, -50);
            Direction direction = calculateDirection(client.getPosition(), posTo, client.getState());
            client.setDirection(direction);
            updateTextureBasedOnDirection(direction, client);

            FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                    .filter(entity -> entity.getInt("id") == client.getId())
                    .findFirst()
                    .ifPresent(e -> {
                        FXGL.animationBuilder()
                                .onFinished(() -> updateTextureBasedOnDirection(Direction.UP, client))
                                .duration(Duration.seconds(0.2))
                                .interpolator(Interpolator.LINEAR)
                                .translate(e)
                                .from(client.getPosition())
                                .to(posTo)
                                .buildAndPlay();
                    });
        }
    }

    private void updateTextureBasedOnDirection(Direction direction, Client client) {
        String textureFile = switch (direction) {
            case UP -> "clientLookingUp.png";
            case DOWN -> "clientLookingDown.png";
            case LEFT -> "clientLookingLeft.png";
            case RIGHT -> "clientLookingRight.png";
        };

        Texture newTexture = FXGL.texture(textureFile).copy();
        newTexture.setFitWidth(24);
        newTexture.setFitHeight(24);
        notifyDirectionChanged(client.getId(), direction);
        FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(entity -> entity.getInt("id") == client.getId())
                .findFirst()
                .ifPresent(entity -> {
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

    public void moveClientToTable(Client client) {
        System.out.println("[CLIENTE] Me movi a la tabla");
        Point2D chairPosition = client.getRoute();
        Point2D clientPosition = client.getPosition();
        Point2D firstIntermediatePosition = new Point2D(clientPosition.getX(), chairPosition.getY()+20);
        Point2D secondIntermediatePosition = new Point2D(chairPosition.getX(), firstIntermediatePosition.getY());

        Entity e = FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(entity -> entity.getInt("id") == client.getId())
                .findFirst()
                .get();


        FXGL.animationBuilder()
                .duration(Duration.seconds(0.3))
                .interpolator(Interpolator.LINEAR)
                .onFinished(() -> {
                    System.out.println("[ANIMATION-CLIENT] Me voy hacia UP");
                    updateTextureBasedOnDirection(Direction.UP, client);
                    client.setPosition(firstIntermediatePosition);
                    notifyPositionChanged(client.getId(), firstIntermediatePosition);
                })
                .translate(e)
                .from(clientPosition)
                .to(firstIntermediatePosition)
                .buildAndPlay();
        FXGL.animationBuilder()
                .duration(Duration.seconds(0.3))
                .interpolator(Interpolator.LINEAR)
                .onFinished(() -> {
                    System.out.println("[ANIMATION-CLIENT] Me voy hacia LEFT");
                    updateTextureBasedOnDirection(Direction.LEFT, client);
                    client.setPosition(secondIntermediatePosition);
                    notifyPositionChanged(client.getId(), secondIntermediatePosition);
                })
                .translate(e)
                .from(firstIntermediatePosition)
                .to(secondIntermediatePosition)
                .buildAndPlay();
        FXGL.animationBuilder()
                .duration(Duration.seconds(0.1))
                .interpolator(Interpolator.LINEAR)
                .onFinished(() -> {
                    System.out.println("[ANIMATION-CLIENT] Me voy hacia UP");
                    updateTextureBasedOnDirection(Direction.UP, client);
                    client.setPosition(chairPosition);
                    notifyPositionChanged(client.getId(), chairPosition);
                })
                .translate(e)
                .from(firstIntermediatePosition)
                .to(chairPosition)
                .buildAndPlay();
        System.out.println("[ANIMATION-CLIENT] LLEGUE, Sentado de pana");
        updateTextureBasedOnDirection(Direction.RIGHT, client);
    }
}
