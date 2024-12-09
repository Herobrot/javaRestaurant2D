package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import factories.AbstractCharacterFactory;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CookFactory extends AbstractCharacterFactory {
    private static final List<String> COOK_SPRITES = List.of(
            "image/personas/Cocinero.png",
            "image/personas/Cocinero2.png"
    );
    private final Random random = new Random();

    @Override
    public Entity create(SpawnData data) {
        String selectedPath = COOK_SPRITES.get(random.nextInt(COOK_SPRITES.size()));
        ImageView imageView = createCharacterSprite(selectedPath);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .build();
    }
}