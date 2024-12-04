package utils;

import domain.components.services.Direction;
import domain.observer.IClientObserver;
import javafx.geometry.Point2D;

public class IClientLogger implements IClientObserver {

    @Override
    public void onClientPositionChanged(int clientId, Point2D newPosition) {
        System.out.println("Cliente " + clientId + " se movió a la posición: " + newPosition);
    }

    @Override
    public void onClientDirectionChanged(int clientId, Direction newDirection) {
        System.out.println("Cliente " + clientId + " cambió de dirección a: " + newDirection);
    }
}