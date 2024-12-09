package core;

import com.almasb.fxgl.app.scene.GameScene;
import domain.models.CustomerStats;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class UIManager {
    private final CustomerStats customerStats;

    public UIManager(CustomerStats customerStats) {
        this.customerStats = customerStats;
    }

    public void initializeUI(GameScene gameScene) {
        VBox statsBox = createStatsBox();
        gameScene.addUINode(statsBox);
    }

    private VBox createStatsBox() {
        VBox mainContainer = new VBox(5);
        mainContainer.setStyle(
                "-fx-background-color: rgba(139, 69, 19, 0.85);" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);" +
                        "-fx-border-color: rgba(160, 82, 45, 0.9);" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 1.5;"
        );
        mainContainer.setTranslateX(20);
        mainContainer.setTranslateY(20);

        HBox statsContainer = new HBox(15);

        String labelStyle = "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #E8E8E8; " +
                "-fx-padding: 2 0; " +
                "-fx-font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;";

        Label titleLabel = new Label("Estadísticas del Bar");
        titleLabel.setStyle(labelStyle + "-fx-font-size: 14px; -fx-padding: 0 0 5 0;");

        Label waitingTableLabel = createStatsLabel(
                customerStats.customersWaitingTableProperty(),
                "💺 %d",
                labelStyle
        );
        addTooltip(waitingTableLabel, "Clientes esperando mesa");

        Label waitingFoodLabel = createStatsLabel(
                customerStats.customersWaitingFoodProperty(),
                "🍽️ %d",
                labelStyle
        );
        addTooltip(waitingFoodLabel, "Clientes esperando comida");

        Label eatingLabel = createStatsLabel(
                customerStats.customersEatingProperty(),
                "🍴 %d",
                labelStyle
        );
        addTooltip(eatingLabel, "Clientes comiendo");

        Label atTablesLabel = createStatsLabel(
                customerStats.customersAtTablesProperty(),
                "👥 %d",
                labelStyle
        );
        addTooltip(atTablesLabel, "Total de clientes en mesas");

        statsContainer.getChildren().addAll(
                waitingTableLabel,
                waitingFoodLabel,
                eatingLabel,
                atTablesLabel
        );

        mainContainer.getChildren().addAll(titleLabel, statsContainer);

        return mainContainer;
    }

    private void addTooltip(Label label, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #4A4A4A; -fx-text-fill: white;");
        Tooltip.install(label, tooltip);
    }

    private Label createStatsLabel(javafx.beans.property.IntegerProperty property, String format, String style) {
        Label label = new Label();
        label.setTextFill(Color.BLACK);
        label.textProperty().bind(property.asString(format));
        label.setStyle(style);
        return label;
    }
}