package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Класс Представления для игрового поля
 * Отвечает только за отрисовку состояния модели
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener
{
    private final RobotModel model;

    public GameVisualizer(RobotModel model)
    {
        this.model = model;
        this.model.addPropertyChangeListener(this);
        setDoubleBuffered(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, (int) model.getX(), (int) model.getY(), model.getDirection()
        );
        drawTarget(g2d, model.getTargetX(), model.getTargetY());
    }

    /**
     * Рисует закрашенный овал, центрированный в указанной точке
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует контур овала, центрированного в указанной точке
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует тело робота
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform old = g.getTransform();
        AffineTransform t = (AffineTransform) old.clone();
        t.rotate(direction, x, y);

        g.setTransform(t);

        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x  + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x  + 10, y, 5, 5);
        g.setTransform(old);
    }

    /**
     * Рисует цель на поле
     */
    private void drawTarget(Graphics2D g, int x, int y)
    {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
