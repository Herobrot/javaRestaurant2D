package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import components.MovementComponent;
import utils.*;
import domain.entities.Customer;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class CustomerFactory extends AbstractCharacterFactory {

    @Override
    public Entity create(SpawnData data) {
        var customerEntity = createCharacterSprite("clientLookingUp.png", 24, 24);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(customerEntity)
                .with(new MovementComponent(GameConfig.CUSTOMER_SPEED))
                .with(data.<Customer>get("customerComponent"))
                .build();
    }
}