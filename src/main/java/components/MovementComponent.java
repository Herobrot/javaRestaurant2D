package components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.Queue;
import java.util.LinkedList;
import java.util.function.Consumer;

public class MovementComponent extends Component {
    private final double speed;
    private Point2D targetPosition;
    private boolean isMoving = false;
    private final Queue<MovementTask> taskQueue = new LinkedList<>();
    private Consumer<Point2D> onPositionUpdate;
    private Runnable onDestinationReached;

    public MovementComponent(double speed) {
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMoving && targetPosition != null) {
            Point2D currentPos = entity.getPosition();
            Point2D direction = targetPosition.subtract(currentPos);

            double distance = direction.magnitude();
            if (distance < speed * tpf) {
                entity.setPosition(targetPosition);
                onTargetReached();
            } else {
                direction = direction.normalize().multiply(speed * tpf);
                entity.translate(direction.getX(), direction.getY());
                if (onPositionUpdate != null) {
                    onPositionUpdate.accept(entity.getPosition());
                }
            }
        }
    }

    public void moveTo(Point2D target, Runnable onComplete) {
        MovementTask task = new MovementTask(target, onComplete);
        taskQueue.add(task);
        if (!isMoving) {
            startNextMovement();
        }
    }

    public void moveToImmediate(Point2D target) {
        taskQueue.clear();
        targetPosition = target;
        isMoving = true;
    }

    private void startNextMovement() {
        MovementTask task = taskQueue.peek();
        if (task != null) {
            targetPosition = task.destination;
            isMoving = true;
        }
    }

    private void onTargetReached() {
        isMoving = false;
        MovementTask completedTask = taskQueue.poll();
        if (completedTask != null && completedTask.onComplete != null) {
            completedTask.onComplete.run();
        }

        if (onDestinationReached != null) {
            onDestinationReached.run();
        }

        startNextMovement();
    }

    public void stop() {
        isMoving = false;
        targetPosition = null;
        taskQueue.clear();
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setOnPositionUpdate(Consumer<Point2D> onPositionUpdate) {
        this.onPositionUpdate = onPositionUpdate;
    }

    public void setOnDestinationReached(Runnable onDestinationReached) {
        this.onDestinationReached = onDestinationReached;
    }

    private static class MovementTask {
        final Point2D destination;
        final Runnable onComplete;

        MovementTask(Point2D destination, Runnable onComplete) {
            this.destination = destination;
            this.onComplete = onComplete;
        }
    }
}