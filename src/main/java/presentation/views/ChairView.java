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
        tableTexture.setFitWidth(200);  // Ajustar el ancho a 200 píxeles
        tableTexture.setFitHeight(200); // Ajustar la altura a 200 píxeles

        double adjustedX = x - 70; // Mover 10 píxeles a la izquierda
        double adjustedY = y - 90; // Mover 10 píxeles hacia arriba

        return FXGL.entityBuilder()
                .type(TypeGame.TABLE)
                .at(adjustedX, adjustedY)
                .viewWithBBox(tableTexture)
                .buildAndAttach();

    }


    @Spawns("customer")
    public static Entity createCustomerEntity(Client client) {
        var customerTexture = texture("toriLokingBack.png").copy();
        customerTexture.setFitWidth(300);  // Ajustar el ancho de la imagen
        customerTexture.setFitHeight(250); // Ajustar la altura de la imagen

        double adjustedX = 65 - 140; // Mover 20 píxeles a la izquierda
        double adjustedY = 80; // Mover 10 píxeles hacia abajo

        return FXGL.entityBuilder()
                .at(adjustedX, adjustedY)
                .type(TypeGame.Client)
                .viewWithBBox(customerTexture)
                .with("customer", client)
                .buildAndAttach();
    }

    @Spawns("waiter")
    public Entity createWaiter(SpawnData data) {
        var waiterTexture = texture("toriTwoLokingBack.png").copy();
        waiterTexture.setFitWidth(300);  // Ajustar el ancho de la imagen
        waiterTexture.setFitHeight(250); // Ajustar la altura de la imagen

        double adjustedX = data.getX() - 50; // Mover 50 píxeles a la izquierda
        double adjustedY = data.getY() + 20; // Mover 20 píxeles hacia abajo

        return FXGL.entityBuilder()
                .from(data)
                .at(adjustedX, adjustedY)
                .type(TypeGame.WAITER)
                .viewWithBBox(waiterTexture)
                .buildAndAttach();
    }

    @Spawns("chef")
    public Entity createChef(SpawnData data) {
        var chefTexture = texture("chefLokingBack.png").copy();
        chefTexture.setFitWidth(300);  // Ajustar el ancho de la imagen
        chefTexture.setFitHeight(250); // Ajustar la altura de la imagen

        double adjustedX = data.getX() - 100; // Mover 50 píxeles a la izquierda
        double adjustedY = data.getY() + 20; // Mover 50 píxeles hacia abajo

        return FXGL.entityBuilder()
                .from(data)
                .at(adjustedX, adjustedY)
                .type(TypeGame.CHEF)
                .viewWithBBox(chefTexture)
                .buildAndAttach();
    }

}

