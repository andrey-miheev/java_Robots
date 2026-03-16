package gui;

import javax.swing.JFrame;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для сохранения и восстановления главного окна
 */
public class FrameStateHandler {
    private final String KEY_X = "x";
    private final String KEY_Y = "y";
    private final String KEY_WIDTH = "width";
    private final String KEY_HEIGHT = "height";
    private final String KEY_ICONIFIED = "iconified";
    private final String KEY_MAXIMIZED = "maximized";

    /**
     * Сохраняет состояние главного окна
     * @param frame главное окно
     * @return  словарь состояния окна
     */
    public Map<String, String> saveState(JFrame frame){
        Map<String, String> state = new HashMap<>();

        state.put(KEY_X, String.valueOf(frame.getX()));
        state.put(KEY_Y, String.valueOf(frame.getY()));
        state.put(KEY_WIDTH, String.valueOf(frame.getWidth()));
        state.put(KEY_HEIGHT, String.valueOf(frame.getHeight()));

        int extendedState = frame.getExtendedState();
        state.put(KEY_MAXIMIZED, String.valueOf((extendedState & JFrame.MAXIMIZED_BOTH) != 0));
        state.put(KEY_ICONIFIED, String.valueOf((extendedState & JFrame.ICONIFIED) != 0));

        return state;
    }

    /**
     * Восстанавливает состояние главного окна
     */
    public void restoreState(JFrame frame, Map<String, String> state) {
        if (state == null || state.isEmpty()) {
            return;
        }

        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int x = parseInt(state.get(KEY_X), frame.getX());
            int y = parseInt(state.get(KEY_Y), frame.getY());
            int width = parseInt(state.get(KEY_WIDTH), frame.getWidth());
            int height = parseInt(state.get(KEY_HEIGHT), frame.getHeight());

            x = Math.max(0, Math.min(x, screenSize.width - 100));
            y = Math.max(0, Math.min(y, screenSize.height - 100));
            width = Math.max(200, Math.min(width, screenSize.width - x));
            height = Math.max(200, Math.min(height, screenSize.height - y));

            frame.setBounds(x, y, width, height);

            boolean maximized = parseBoolean(state.get(KEY_MAXIMIZED), false);
            boolean iconified = parseBoolean(state.get(KEY_ICONIFIED), false);

            int extendedState = 0;
            if (maximized) {
                extendedState |= JFrame.MAXIMIZED_BOTH;
            }
            if (iconified) {
                extendedState |= JFrame.ICONIFIED;
            }

            frame.setExtendedState(extendedState);
        } catch (Exception _){

        }
    }

    private int parseInt(String value, int defaultValue){
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String value, Boolean defaultValue){
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
