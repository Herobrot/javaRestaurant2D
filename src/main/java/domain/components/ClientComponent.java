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
    private static Direction calculateDirection(Point2D from, Point2D to) {
        Point2D delta = to.subtract(from).normalize();
        System.out.println(delta);

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

    public static void moveClientAlongRoute(Client client, List<Point2D> route) {
        for (int i = 0; i < route.size() - 1; i++) {
            Point2D current = route.get(i);
            Point2D next = route.get(i + 1);

            Direction direction = calculateDirection(current, next);
            client.setDirection(direction);

            updateTextureBasedOnDirection(direction);

            client.setPosition(next);

            moveEntityToPosition(next);
        }
    }

    private static void updateTextureBasedOnDirection(Direction direction) {
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
                .filter(entity -> entity.getBoolean("customer"))
                .findFirst()
                .ifPresent(entity -> {
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

    private static void moveEntityToPosition(Point2D position) {
        FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(entity -> entity.getObject("client"))
                .findFirst()
                .ifPresent(entity -> entity.setPosition(position));
    }

}
