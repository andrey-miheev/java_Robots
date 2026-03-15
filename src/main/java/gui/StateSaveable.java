package gui;
import java.util.Map;

/**
 * Интерфейс с методами "сохранить состояние" и "восстановить состояние"
 */
public interface StateSaveable {
    /**
     * Сохраняет состояние компонента в виде словаря
     * @return словарь с состоянием компонента
     */
    Map<String, String> saveState();

    /**
     * Восстанавливает состояние компонента из словаря
     * @param state словарь с состоянием
     */
    void restoreState(Map<String, String> state);
}
