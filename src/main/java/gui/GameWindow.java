package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateWindows
{
    private final String COMPONENT_ID = "game";

    private final GameVisualizer gameVisualizer;
    private final ComponentStateHandler stateHandler;

    public GameWindow(RobotModel model,ComponentStateHandler stateHandler)
    {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer(model);
        this.stateHandler = stateHandler;
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        pack();
    }

    public GameVisualizer getVisualizer() {
        return gameVisualizer;
    }

    @Override
    public Map<String, String> saveState() {
        return stateHandler.saveState(this);
    }

    @Override
    public void restoreState(Map<String, String> state) {
        stateHandler.restoreState(this, state);
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
