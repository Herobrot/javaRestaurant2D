package application;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import utils.GameConfig;
import core.RestaurantGameEngine;
import domain.models.CustomerStats;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameApp extends GameApplication {
    private RestaurantGameEngine gameEngine;

    private CustomerStats customerStats;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(GameConfig.WINDOW_WIDTH);
        settings.setHeight(GameConfig.WINDOW_HEIGHT);
        settings.setTitle("restaurante");
        settings.setVersion("777");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());

        // Inicializar componentes principales
        customerStats = new CustomerStats();

        // Inicializar el motor del juego
        gameEngine = new RestaurantGameEngine(getGameWorld(), customerStats);

        // Inicializar el administrador de UI


        // Iniciar el juego
        setupGame();
    }

    private void setupGame() {
        // Crear el fondo
        getGameWorld().addEntity(gameEngine.createBackground());

        // Inicializar componentes del juego
        gameEngine.initializeGameComponents();
        

        // Iniciar la generación de clientes
        gameEngine.startCustomerGeneration();
    }

    protected void onExit() {
        gameEngine.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}