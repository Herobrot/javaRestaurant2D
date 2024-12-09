package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CookFactory extends AbstractCharacterFactory {

    @Override
    public Entity create(SpawnData data) {
        Texture texture = createCharacterSprite("chefLokingBack.png", 24, 24);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(texture)
                .build();
    }
}