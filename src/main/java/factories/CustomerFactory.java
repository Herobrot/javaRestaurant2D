package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import components.MovementComponent;
import utils.*;
import domain.entities.Customer;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CustomerFactory extends AbstractCharacterFactory {
    private static final List<String> CUSTOMER_SPRITES = List.of(
            "image/personas/Persona.png",
            "image/personas/Persona2.png",
            "image/personas/Persona3.png",
            "image/personas/Persona4.png"
    );
    private final Random random = new Random();

    @Override
    public Entity create(SpawnData data) {
        String selectedPath = CUSTOMER_SPRITES.get(random.nextInt(CUSTOMER_SPRITES.size()));
        ImageView imageView = createCharacterSprite(selectedPath);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(new MovementComponent(GameConfig.CUSTOMER_SPEED))
                .with(data.<Customer>get("customerComponent"))
                .build();
    }
}