package domain.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.application.Platform;

public class CustomerStats {
    private final SimpleIntegerProperty customersWaitingTable;
    private final SimpleIntegerProperty customersWaitingFood;
    private final SimpleIntegerProperty customersEating;
    private final SimpleIntegerProperty customersAtTables;

    public CustomerStats() {
        customersWaitingTable = new SimpleIntegerProperty(0);
        customersWaitingFood = new SimpleIntegerProperty(0);
        customersEating = new SimpleIntegerProperty(0);
        customersAtTables = new SimpleIntegerProperty(0);
    }

    public void incrementWaitingForTable() {
        Platform.runLater(() -> customersWaitingTable.set(customersWaitingTable.get() + 1));
    }

    public void decrementWaitingForTable() {
        Platform.runLater(() -> {
            int currentValue = customersWaitingTable.get();
            if (currentValue > 0) {
                customersWaitingTable.set(currentValue - 1);
                customersAtTables.set(customersAtTables.get() + 1);
            }
        });
    }

    public void incrementWaitingForFood() {
        Platform.runLater(() -> customersWaitingFood.set(customersWaitingFood.get() + 1));
    }

    public void decrementWaitingForFood() {
        Platform.runLater(() -> customersWaitingFood.set(customersWaitingFood.get() - 1));
    }

    public void incrementEating() {
        Platform.runLater(() -> customersEating.set(customersEating.get() + 1));
    }

    public void decrementEating() {
        Platform.runLater(() -> {
            customersEating.set(Math.max(0, customersEating.get() - 1));
            customersAtTables.set(Math.max(0, customersAtTables.get() - 1));
        });
    }

    public SimpleIntegerProperty customersWaitingTableProperty() {
        return customersWaitingTable;
    }

    public SimpleIntegerProperty customersWaitingFoodProperty() {
        return customersWaitingFood;
    }

    public SimpleIntegerProperty customersEatingProperty() {
        return customersEating;
    }

    public SimpleIntegerProperty customersAtTablesProperty() {
        return customersAtTables;
    }
}