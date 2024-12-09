package domain.observer;


import javafx.geometry.Point2D;

public interface IClientObserver {
    void onClientPositionChanged(int clientId, Point2D newPosition);
}
