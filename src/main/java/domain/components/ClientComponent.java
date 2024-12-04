package domain.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.entities.Client;
import javafx.geometry.Point2D;
import presentation.views.TypeGame;

import java.util.List;

public class ClientComponent extends Component{
    private static Direction calculateDirection(Point2D from, Point2D to, Client.ClientState clientState) {
        Point2D delta = to.subtract(from).normalize();
        System.out.println(delta);

        if (clientState == Client.ClientState.WAITING_FOR_FOOD || clientState == Client.ClientState.EATING) {
            System.out.println("Sentado de pana");
            return Direction.RIGHT;
        }

        if (delta.equals(new Point2D(0, -1))) {
            System.out.println("Arriba");
            return Direction.UP;
        };
        if (delta.equals(new Point2D(0, 1))) {
            System.out.println("Abajo");
            return Direction.DOWN;
        };
        if (delta.equals(new Point2D(-1, 0))) return Direction.LEFT;
        if (delta.equals(new Point2D(1, 0))) return Direction.RIGHT;

        throw new IllegalArgumentException("Dirección no válida: " + delta);
    }

    public static void moveClientAlongRoute(Client client, List<Point2D> route, Client.ClientState clientState) {
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D current = route.get(i);
            Point2D next = route.get(i + 1);

            Direction direction = calculateDirection(current, next, clientState);
            client.setDirection(direction);

            updateTextureBasedOnDirection(direction, client);

            client.setPosition(next);

            moveEntityToPosition(next, client);
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
                    System.out.println("Entre en " + client.getId() + " de actualizar textura");
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

    private static void moveEntityToPosition(Point2D position, Client client) {
        FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(entity -> entity.getInt("id") == client.getId())
                .findFirst()
                .ifPresent(entity -> {
                    System.out.println("Entre en " + client.getId() + " de actualizar posicion");
                    entity.setPosition(position);
                });
    }

}
