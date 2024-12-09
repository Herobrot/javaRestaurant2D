package factories;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;

public interface EntityFactory {
    Entity create(SpawnData data);
}