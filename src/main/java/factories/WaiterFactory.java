package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import components.MovementComponent;
import utils.*;
import domain.entities.Waiter;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class WaiterFactory extends AbstractCharacterFactory {
    private static final String WAITER_SPRITE = "image/personas/Mesera.png";

    @Override
    public Entity create(SpawnData data) {
        ImageView imageView = createCharacterSprite(WAITER_SPRITE);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(new MovementComponent(GameConfig.WAITER_SPEED))
                .with(data.<Waiter>get("waiterComponent"))
                .build();
    }
}