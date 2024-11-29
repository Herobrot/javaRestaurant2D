package presentation.views;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import domain.entities.Client;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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


    @Spawns("customer")
    public static Entity createCustomerEntity(Client client) {
        var customerTexture = texture("clientLookingUp.png").copy();
        customerTexture.setFitWidth(24);  // Ajustar el ancho de la imagen
        customerTexture.setFitHeight(24); // Ajustar la altura de la imagen

        double adjustedX = 330;
        double adjustedY = 360;

        return FXGL.entityBuilder()
                .at(adjustedX, adjustedY)
                .type(TypeGame.Client)
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
        chefTexture.setFitWidth(24);  // Ajustar el ancho de la imagen
        chefTexture.setFitHeight(24); // Ajustar la altura de la imagen

        double adjustedX = data.getX(); // Mover 50 píxeles a la izquierda
        double adjustedY = data.getY(); // Mover 50 píxeles hacia abajo

        return FXGL.entityBuilder()
                .from(data)
                .at(adjustedX, adjustedY)
                .type(TypeGame.CHEF)
                .viewWithBBox(chefTexture)
                .collidable()
                .build();
    }

}

