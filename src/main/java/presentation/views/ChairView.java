package presentation.views;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import domain.entities.Client;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.texture;

public class ChairView implements EntityFactory {
    @Spawns("table")
    public Entity createTable(double x, double y) {
        var tableTexture = texture("table.png").copy();
        tableTexture.setFitWidth(22);  // Ajustar el ancho a 200 píxeles
        tableTexture.setFitHeight(22); // Ajustar la altura a 200 píxeles

        double adjustedX = x - 35; // Mover 10 píxeles a la izquierda
        double adjustedY = y - 45; // Mover 10 píxeles hacia arriba

        return FXGL.entityBuilder()
                .type(TypeGame.TABLE)
                .at(adjustedX, adjustedY)
                .viewWithBBox(tableTexture)
                .collidable()
                .build();
    }

    @Spawns("chair")
    public Entity createChair(double x, double y) {
        var chairTexture = texture("chairToRight.png").copy();
        chairTexture.setFitWidth(13);
        chairTexture.setFitHeight(24);

        double adjustedX = x - 30;
        double adjustedY = y - 50;

        return FXGL.entityBuilder()
                .type(TypeGame.CHAIR)
                .at(adjustedX, adjustedY)
                .viewWithBBox(chairTexture)
                .build();
    }
    public static Point2D getChairPosition(int chairIndex) {
        Entity chairEntity = FXGL.getGameWorld().getEntitiesByType(TypeGame.CHAIR).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Silla no encontrada"));

        return chairEntity.getPosition();
    }
    public static void moveCustomerToChair(Client client, int chairIndex) {
        // Obtener la posición de la silla
        Point2D chairPosition = getChairPosition(chairIndex);

        // Buscar la entidad asociada al cliente
        Entity customerEntity = FXGL.getGameWorld().getEntitiesByType(TypeGame.Client).stream()
                .filter(e -> e.getObject("customer").equals(client))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));

        // Mover al cliente a la posición de la silla
        customerEntity.setPosition(chairPosition);
    }
    @Spawns("customer")
    public static Entity createCustomerEntity(Client client) {
        var customerTexture = texture("clientLookingUp.png").copy();
        customerTexture.setFitWidth(240);
        customerTexture.setFitHeight(240);

        double adjustedX = 70;
        double adjustedY = 70;

        return FXGL.entityBuilder()
                .type(TypeGame.Client)
                .at(adjustedX, adjustedY)
                .viewWithBBox(customerTexture)
                .with("customer", client)
                .collidable()
                .build();
    }

    @Spawns("waiter")
    public Entity createWaiter(SpawnData data) {
        var waiterTexture = texture("waiterLookingLeft.png").copy();
        waiterTexture.setFitWidth(24);
        waiterTexture.setFitHeight(24);

        double adjustedX = data.getX();
        double adjustedY = data.getY();

        return FXGL.entityBuilder()
                .from(data)
                .at(adjustedX, adjustedY)
                .type(TypeGame.WAITER)
                .viewWithBBox(waiterTexture)
                .collidable()
                .build();
    }

    @Spawns("chef")
    public Entity createChef(SpawnData data) {
        var chefTexture = texture("chefLokingBack.png").copy();
        chefTexture.setFitWidth(24);
        chefTexture.setFitHeight(24);

        double adjustedX = data.getX();
        double adjustedY = data.getY();

        return FXGL.entityBuilder()
                .from(data)
                .at(adjustedX, adjustedY)
                .type(TypeGame.CHEF)
                .viewWithBBox(chefTexture)
                .collidable()
                .build();
    }

}

