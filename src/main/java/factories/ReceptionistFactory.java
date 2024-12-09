package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import domain.entities.Receptionist;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class ReceptionistFactory extends AbstractCharacterFactory {
    private static final String RECEPTIONIST_SPRITE = "image/personas/Recepcionista.png";

    @Override
    public Entity create(SpawnData data) {
        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(createCharacterSprite(RECEPTIONIST_SPRITE))
                .with(data.<Receptionist>get("receptionistComponent"))
                .build();
    }
}