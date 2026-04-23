package gui;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Модель робота
 * хранит состояние - его координаты, направление, положение цели
 * уведомляет слушателей об изменениях
 */
public class RobotModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;

    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;
    
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    public void setTargetPosition(Point p){
        this.targetPositionX = p.x;
        this.targetPositionY = p.y;
    }

    public void update(int duration){
        double dist = Math.sqrt(Math.pow(targetPositionX - robotPositionX, 2) + Math.pow(targetPositionY - robotPositionY, 2));
        if (dist < 0.5) return;

        double angleToTarget = Math.atan2(targetPositionY - robotPositionY, targetPositionX - robotPositionX);

        double angleDiff = normalizeAngle(angleToTarget - robotDirection);

        double angularVelocity = 0;
        if (angleDiff > 0) {
            angularVelocity = Math.min(MAX_ANGULAR_VELOCITY, angleDiff);
        } else if (angleDiff < 0) {
            angularVelocity = Math.max(-MAX_ANGULAR_VELOCITY, angleDiff);
        }

        double newDirection = normalizeAngle(robotDirection + angularVelocity * duration);
        robotPositionX += MAX_VELOCITY * duration * Math.cos(newDirection);
        robotPositionY += MAX_VELOCITY * duration * Math.sin(newDirection);
        robotDirection = newDirection;
        
        support.firePropertyChange("robot_moved", null, this);
    }

    private double normalizeAngle(double angle) {
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Возвращает значение координаты робота по Х
     * @return robotPositionX
     */
    public double getX() { return robotPositionX; }

    /**
     * Возвращает значение координаты робота по Y
     * @return robotPositionY
     */
    public double getY() { return robotPositionY; }

    /**
     * Возвращает значение направления робота
     * @return robotDirection
     */
    public double getDirection() { return robotDirection; }

    /**
     * Возвращает значение координаты цели по Х
     * @return targetPositionX
     */
    public int getTargetX() { return targetPositionX; }

    /**
     * Возвращает значение координаты цели по Y
     * @return targetPositionY
     */
    public int getTargetY() { return targetPositionY; }
}
