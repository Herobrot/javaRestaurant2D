package domain.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.entities.Chef;
import javafx.geometry.Point2D;
import presentation.views.TypeGame;

public class ChefComponent extends Component{
    public static Direction calculateDirection(String direction){
        if ("Up".equals(direction)) return Direction.UP;
        if ("Down".equals(direction)) return Direction.DOWN;
        if ("Left".equals(direction)) return Direction.LEFT;
        if ("Right".equals(direction)) return Direction.RIGHT;
        throw new IllegalArgumentException("Error de direccion, se obtuvo: " + direction);
    }
    public static void moveChefTo(Chef chef, String direction, Point2D position) {
        chef.setDirection(calculateDirection(direction));
        updateTextureBasedOnDirection(calculateDirection(direction), chef);
        chef.setPosition(position);
        moveEntityToPosition(position, chef);
    }

    private static void updateTextureBasedOnDirection(Direction direction, Chef chef) {
        String textureFile = switch (direction) {
            case UP -> "chefLokingUp.png";
            case DOWN -> "chefLokingBack.png";
            case LEFT -> "chefLookingLeft.png";
            case RIGHT -> "chefLookingRight.png";
        };

        Texture newTexture = FXGL.texture(textureFile).copy();
        newTexture.setFitWidth(24);
        newTexture.setFitHeight(24);

        FXGL.getGameWorld().getEntitiesByType(TypeGame.CHEF).stream()
                .filter(entity -> entity.getInt("id") == chef.getId())
                .findFirst()
                .ifPresent(entity -> {
                    System.out.println("Entre en " + chef.getId() + " de actualizar textura");
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

    private static void moveEntityToPosition(Point2D position, Chef chef) {
        FXGL.getGameWorld().getEntitiesByType(TypeGame.CHEF).stream()
                .filter(entity -> entity.getInt("id") == chef.getId())
                .findFirst()
                .ifPresent(entity -> {
                    System.out.println("[CHEF] Entre en " + chef.getId() + " de actualizar posicion a: " + chef.getPosition());
                    entity.setPosition(position);
                });
    }

}
