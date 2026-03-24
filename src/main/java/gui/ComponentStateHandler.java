package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Единый класс для сохранения и восстановления состояния любых окон
 */
public class ComponentStateHandler {
    private final String KEY_X = "x";
    private final String KEY_Y = "y";
    private final String KEY_WIDTH = "width";
    private final String KEY_HEIGHT = "height";
    private final String KEY_ICONIFIED = "iconified";
    private final String KEY_MAXIMIZED = "maximized";

    /**
     * Сохраняет состояние любого компонента
     */
    public Map<String, String> saveState(Component component){
        Map<String, String> state = new HashMap<>();

        try{
            state.put(KEY_X, String.valueOf(component.getX()));
            state.put(KEY_Y, String.valueOf(component.getY()));
            state.put(KEY_WIDTH, String.valueOf(component.getWidth()));
            state.put(KEY_HEIGHT, String.valueOf(component.getHeight()));

            if (component instanceof JInternalFrame){
                JInternalFrame frame = (JInternalFrame) component;
                state.put(KEY_ICONIFIED, String.valueOf(frame.isIcon()));
                state.put(KEY_MAXIMIZED, String.valueOf(frame.isMaximum()));
            } else if (component instanceof JFrame){
                JFrame frame = (JFrame) component;
                int extendedState = frame.getExtendedState();
                state.put(KEY_MAXIMIZED, String.valueOf((extendedState & JFrame.MAXIMIZED_BOTH) != 0));
                state.put(KEY_ICONIFIED, String.valueOf((extendedState & JFrame.ICONIFIED) != 0));

            }

        } catch (Exception _){

        }

        return state;
    }

    /**
     * Восстанавливает состояние любого компонента
     */
    public void restoreState(Component component, Map<String, String> state){
        if (state == null || state.isEmpty()){
            return;
        }
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            int x = parseInt(state.get(KEY_X), component.getX());
            int y = parseInt(state.get(KEY_Y), component.getY());
            int width = parseInt(state.get(KEY_WIDTH), component.getWidth());
            int height = parseInt(state.get(KEY_HEIGHT), component.getHeight());

            x = Math.max(0, Math.min(x, screenSize.width - 50));
            y = Math.max(0, Math.min(y, screenSize.height - 50));
            width = Math.max(100, Math.min(width, screenSize.width - x));
            height = Math.max(100, Math.min(height, screenSize.height - y));

            component.setBounds(x, y, width, height);

            boolean iconified = parseBoolean(state.get(KEY_ICONIFIED));
            boolean maximized = parseBoolean(state.get(KEY_MAXIMIZED));

            if (component instanceof JInternalFrame){
                JInternalFrame frame = (JInternalFrame) component;
                try{
                    if (frame.isMaximum()) frame.setMaximum(false);
                    if (frame.isIcon()) frame.setIcon(false);

                    if (maximized){
                        frame.setMaximum(true);
                    } else if(iconified){
                        frame.setIcon(true);
                    }
                } catch (Exception _){

                }
            } else if (component instanceof JFrame){
                JFrame frame = (JFrame) component;

                int extendedState = 0;
                if (maximized) extendedState |= JFrame.MAXIMIZED_BOTH;
                if (iconified) extendedState |= JFrame.ICONIFIED;
                frame.setExtendedState(extendedState);
            }
            
        } catch (Exception _){

        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String value){
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
