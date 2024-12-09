package factories;

import com.almasb.fxgl.entity.Entity;
import utils.*;
import utils.ImageCache;
import javafx.scene.image.ImageView;

public abstract class AbstractCharacterFactory implements EntityFactory {
    protected ImageView createCharacterSprite(String imagePath) {
        ImageView imageView = new ImageView(ImageCache.getImage(imagePath));
        imageView.setFitWidth(GameConfig.SPRITE_SIZE * 2);
        imageView.setFitHeight(GameConfig.SPRITE_SIZE * 2);
        imageView.setSmooth(true);
        imageView.setCache(true);
        return imageView;
    }
}