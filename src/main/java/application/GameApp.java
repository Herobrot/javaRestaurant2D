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
        settings.setVersion("1");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());
        customerStats = new CustomerStats();
        gameEngine = new RestaurantGameEngine(getGameWorld(), customerStats);
        setupGame();
    }

    private void setupGame() {
        getGameWorld().addEntity(gameEngine.createBackground());
        gameEngine.initializeGameComponents();
        gameEngine.startCustomerGeneration();
    }

    protected void onExit() {
        gameEngine.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}