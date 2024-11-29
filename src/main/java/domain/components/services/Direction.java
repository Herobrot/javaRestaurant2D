package domain.components.services;

import javafx.geometry.Point2D;

public enum Direction {
    UP(new Point2D(0, -1)),
    DOWN(new Point2D(0, 1)),
    LEFT(new Point2D(-1, 0)),
    RIGHT(new Point2D(1, 0));

    public final Point2D vector;

    Direction(Point2D vector) {
        this.vector = vector;
    }
}
