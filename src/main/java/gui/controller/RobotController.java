package gui.controller;

import gui.GameVisualizer;
import gui.RobotModel;

import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Контроллер
 * Запускает таймер обновления модели
 * Слушает события мыши на View и передает их в Model
 */
public class RobotController {
    private final int UPDATE_INTERVAL = 3;

    public RobotController(RobotModel model, GameVisualizer view){
        Timer timer = new Timer(UPDATE_INTERVAL, e -> {
            model.update(UPDATE_INTERVAL);
        });
        timer.start();

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint());
            }
        });
    }
}