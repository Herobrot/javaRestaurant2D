package domain.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import domain.components.services.Direction;
import domain.entities.Recepcionist;
import presentation.views.TypeGame;

public class RecepcionistComponent extends Component {
    public void updateTextureBasedOnDirection(Direction direction, Recepcionist recepcionist) {
        String textureFile = switch (direction) {
            case UP -> "waiterLookingUp.png";
            case DOWN -> "waiterLookingDown.png";
            case LEFT -> "waiterLookingLeft.png";
            case RIGHT -> "waiterLookingRight.png";
        };

        Texture newTexture = FXGL.texture(textureFile).copy();
        newTexture.setFitWidth(24);
        newTexture.setFitHeight(24);

        FXGL.getGameWorld().getEntitiesByType(TypeGame.RECEPCIONIST).stream()
                .findFirst()
                .ifPresent(entity -> {
                    System.out.println("[RECEPCIONISTA] Entre en actualizar textura");
                    recepcionist.setTexture(newTexture);
                    entity.getViewComponent().clearChildren();
                    entity.getViewComponent().addChild(newTexture);
                });
    }

}
