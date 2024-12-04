package domain.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.entities.Client;
import domain.entities.Waiter;
import javafx.geometry.Point2D;
import presentation.views.TypeGame;

public class WaiterComponent extends Component{
    public static Direction calculateDirection(String direction){
        if ("Up".equals(direction)) return Direction.UP;
        if ("Down".equals(direction)) return Direction.DOWN;
        if ("Left".equals(direction)) return Direction.LEFT;
        if ("Right".equals(direction)) return Direction.RIGHT;
        throw new IllegalArgumentException("Dirección no funca en waiterComponent");
    }
    public static void moveWaiterTo(Waiter waiter, String direction, Point2D position) {
        waiter.setDirection(calculateDirection(direction));
        updateTextureBasedOnDirection(calculateDirection(direction), waiter);
        moveEntityToPosition(position, waiter);
        waiter.setPosition(position);
    }

    private static void updateTextureBasedOnDirection(Direction direction, Waiter waiter) {
        String textureFile = switch (direction) {
            case UP -> "waiterLookingUp.png";
            case DOWN -> "waiterLookingDown.png";
            case LEFT -> "waiterLookingLeft.png";
            case RIGHT -> "waiterLookingRight.png";
        };

        Texture newTexture = FXGL.texture(textureFile).copy();
        newTexture.setFitWidth(24);
        newTexture.setFitHeight(24);

        FXGL.getGameWorld().getEntitiesByType(TypeGame.WAITER).stream()
                .filter(entity -> entity.getInt("id") == waiter.getId())
                .findFirst()
                .ifPresent(entity -> {
                    System.out.println("[MESERO] Entre en " + waiter.getId() + " de actualizar textura");
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

    private static void moveEntityToPosition(Point2D position, Waiter waiter) {
        FXGL.getGameWorld().getEntitiesByType(TypeGame.WAITER).stream()
                .filter(entity -> entity.getInt("id") == waiter.getId())
                .findFirst()
                .ifPresent(entity -> {
                    System.out.println("[MESERO] Entre en " + waiter.getId() + " de actualizar posicion");
                    entity.getTransformComponent().setPosition(position);
                });
    }

}
