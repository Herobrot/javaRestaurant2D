package domain.observer;

import domain.components.services.Direction;
import javafx.geometry.Point2D;

public interface IClientObserver {
    void onClientPositionChanged(int clientId, Point2D newPosition);
    void onClientDirectionChanged(int clientId, Direction newDirection);
}
