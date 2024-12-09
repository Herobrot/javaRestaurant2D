package factories;

import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

public abstract class AbstractCharacterFactory implements EntityFactory {
    protected Texture createCharacterSprite(String imagePath, int width, int height) {
        Texture texture = texture(imagePath).copy();
        texture.setFitWidth(width);
        texture.setFitHeight(height);
        texture.setSmooth(true);
        texture.setCache(true);
        return texture;
    }
}