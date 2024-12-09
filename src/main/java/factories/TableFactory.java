package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.texture.Texture;
import domain.entities.Table;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class TableFactory extends AbstractCharacterFactory {
    @Override
    public Entity create(SpawnData data) {
        Texture texture = createCharacterSprite("table.png", 22, 22);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(texture)
                .with(new Table(data.get("tableNumber"), new Point2D(data.getX(), data.getY())))
                .build();
    }
}