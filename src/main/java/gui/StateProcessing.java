package gui;

import java.io.*;
import java.util.*;

/**
 * Класс для управления состояниям приложения
 */
public class StateProcessing {
    private final String configPath;
    private final List<StateWindows> components;
    private final Map<String, String> fullState;

    public StateProcessing() {
        this.configPath = System.getProperty("user.home") + "/miheev/state.cfg";
        this.components = new ArrayList<>();
        this.fullState = new HashMap<>();
    }

    /**
     * Регистрирует компонент для сохранения состояния
     */
    public void registerComponent(StateWindows component) {
        components.add(component);
    }

    /**
     * Сохраняет состояние всех компонентов
     */
    public void saveAllStates() {
        for (StateWindows component : components) {
            String prefix = component.getComponentId() + ".";
            fullState.keySet().removeIf(key -> key.startsWith(prefix));
        }

        for (StateWindows component : components) {
            Map<String, String> componentState = component.saveState();
            Map<String, String> prefixedMap = new PrefixedMap(fullState, component.getComponentId(), false);
            prefixedMap.putAll(componentState);
        }

        writeToFile();
    }

    /**
     * Записывает состояние в файл
     */
    private void writeToFile() {
        File file = new File(configPath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("Не удалось создать файл: " + parentDir);
                return;
            }
        }

        Properties props = new Properties();
        props.putAll(fullState);

        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "Application State - Saved on " + new Date());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении состояния: " + e.getMessage());
        }
    }

    /**
     * Восстанавливает состояние всех компонентов
     */
    public void restoreAllStates() {
        readFromFile();

        for (StateWindows component : components) {
            Map<String, String> componentState = new PrefixedMap(fullState, component.getComponentId(), true);
            component.restoreState(componentState);
        }
    }

    /**
     * Читает состояние из файла
     */
    private void readFromFile() {
        File file = new File(configPath);

        if (!file.exists()) {
            System.out.println("Config file not found: " + configPath);
            return;
        }

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
            fullState.clear();
            for (String key : props.stringPropertyNames()) {
                fullState.put(key, props.getProperty(key));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке состояния: " + e.getMessage());
        }
    }
}
