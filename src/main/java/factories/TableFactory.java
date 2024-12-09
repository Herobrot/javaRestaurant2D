package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import utils.*;
import domain.entities.Table;
import utils.ImageCache;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class TableFactory implements EntityFactory {
    private static final String TABLE_SPRITE = "image/objetos/Mesa1Persona.png";

    @Override
    public Entity create(SpawnData data) {
        ImageView imageView = new ImageView(ImageCache.getImage(TABLE_SPRITE));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2.5);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2.5);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return entityBuilder()
                .at(data.getX(), data.getY())
                .viewWithBBox(imageView)
                .with(new Table(data.get("tableNumber"), new Point2D(data.getX(), data.getY())))
                .build();
    }
}