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
        System.out.println("Saving all states...");

        for (StateWindows component : components) {
            String prefix = component.getComponentId() + ".";
            fullState.keySet().removeIf(key -> key.startsWith(prefix));

            System.out.println("Removed prefix: " + prefix);
        }

        for (StateWindows component : components) {
            Map<String, String> componentState = component.saveState();
            System.out.println("Saving state for " + component.getComponentId() + ": " + componentState); //Откладка
            Map<String, String> prefixedMap = new PrefixedMap(fullState, component.getComponentId(), false);
            prefixedMap.putAll(componentState);
        }

        System.out.println("Full state before save: " + fullState);//Откладка
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
                System.err.println("Не удалось создать директорию: " + parentDir);
                return;
            }
        }

        Properties props = new Properties();
        props.putAll(fullState);

        try (OutputStream out = new FileOutputStream(file)) {
            props.store(out, "Application State - Saved on " + new Date());
            System.out.println("State saved to: " + configPath); // Отладка
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении состояния: " + e.getMessage());
        }
    }

    /**
     * Восстанавливает состояние всех компонентов
     */
    public void restoreAllStates() {
        System.out.println("=== RESTORING ALL STATES ===");//Откладка
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
        System.out.println("Read from File");
        File file = new File(configPath);

        if (!file.exists()) {
            System.out.println("Config file not found: " + configPath); // Отладка
            return;
        }

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
            fullState.clear();
            for (String key : props.stringPropertyNames()) {
                fullState.put(key, props.getProperty(key));
            }
            System.out.println("State loaded from: " + configPath); // Отладка
            System.out.println("Loaded " + fullState.size() + " entries"); // Отладка
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке состояния: " + e.getMessage());
        }
    }
}
