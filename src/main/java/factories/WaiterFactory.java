package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;
import components.MovementComponent;
import utils.*;
import domain.entities.Waiter;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class WaiterFactory extends AbstractCharacterFactory {
    @Override
    public Entity create(SpawnData data) {
        Texture texture = createCharacterSprite("waiterLookingDown.png", 24, 24);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(texture)
                .with(new MovementComponent(GameConfig.WAITER_SPEED))
                .with(data.<Waiter>get("waiterComponent"))
                .build();
    }
}