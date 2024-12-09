package utils;

import domain.observer.IClientObserver;
import javafx.geometry.Point2D;

public class IClientLogger implements IClientObserver {

    @Override
    public void onClientPositionChanged(int clientId, Point2D newPosition) {
        System.out.println("Cliente " + clientId + " se movió a la posición: " + newPosition);
    }

}