package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Map;

/**
 * Окно отображения текущих параметров робота
 */
public class RobotStateWindow extends JInternalFrame implements StateWindows{
    private final RobotModel model;
    private final ComponentStateHandler stateHandler;

    private final JLabel xValue = new JLabel();
    private final JLabel yValue = new JLabel();
    private final JLabel dirValue = new JLabel();
    private final JLabel targetAngleValue = new JLabel();

    private final String COMPONENT_ID = "state";

    public RobotStateWindow(RobotModel model, ComponentStateHandler stateHandler){
        super("Параметры робота", true, true);
        this.stateHandler = stateHandler;
        this.model = model;

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("X:"));
        panel.add(xValue);
        panel.add(new JLabel("Y:"));
        panel.add(yValue);
        panel.add(new JLabel("Угол поворота робота:"));
        panel.add(dirValue);
        panel.add(new JLabel("Угол до цели:"));
        panel.add(targetAngleValue);

        getContentPane().add(panel, BorderLayout.CENTER);

        this.model.addPropertyChangeListener(this::onModelChanged);
        updateValues();
        pack();
    }

    /**
     * Метод вызывается при изменении PropertyChangeSupport в модели
     */
    private void onModelChanged(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::updateValues);
    }

    /**
     * Берет актуальные данные из модели и обновляет текст в JLabel
     */
    private void updateValues() {
        xValue.setText(String.format("%.2f", model.getX()));
        yValue.setText(String.format("%.2f", model.getY()));
        dirValue.setText(String.format("%.2f", model.getDirection()));
        targetAngleValue.setText(String.format("%.2f", model.getAngleToTarget()));
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    public Map<String, String> saveState() {
        return stateHandler.saveState(this);
    }

    @Override
    public void restoreState(Map<String, String> state) {
        stateHandler.restoreState(this, state);
    }

}
