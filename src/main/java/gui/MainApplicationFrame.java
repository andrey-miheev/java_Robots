package gui;

import gui.controller.RobotController;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * Главное окно приложения
 */
public class MainApplicationFrame extends JFrame implements StateWindows
{
    private final String COMPONENT_ID = "main";

    private final JDesktopPane desktopPane = new JDesktopPane();
    private final StateProcessing stateProcessing;
    private final ComponentStateHandler stateHandler;

    private final LogWindow logWindow;
    private final GameWindow gameWindow;
    private final RobotStateWindow robotStateWindow;

    /**
     * Конструктор главного окна
     */
    public MainApplicationFrame() {
        this.stateProcessing = new StateProcessing();
        this.stateHandler = new ComponentStateHandler();
        RobotModel robotModel = new RobotModel();
        int inset = 50;
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        
        this.logWindow = createLogWindow();
        addWindow(this.logWindow);

        this.gameWindow = new GameWindow(robotModel, stateHandler);
        this.gameWindow.setSize(400,  400);
        addWindow(this.gameWindow);

        new RobotController(robotModel, gameWindow.getVisualizer());

        this.robotStateWindow = new RobotStateWindow(robotModel, stateHandler);
        addWindow(this.robotStateWindow);

        setJMenuBar(generateMenuBar());
        setupWindowClosingHandler();

        registerComponents();
        loadApplicationState();
    }

    /**
     * Обработчик события закрытия окна с показом диалога подтверждения
     */
    private void setupWindowClosingHandler(){
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    /**
     * Показывает диалог подтверждения выхода
     */
    private void confirmExit(){
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            saveApplicationState();
            System.exit(0);
        }
    }

    /**
     * Создает окно лога
     * Инициализирует окно с источником логов и добавляет тестовое сообщение
     */
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource(), stateHandler);
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Регестрирует компоненты для записи состояния
     */
    private void registerComponents(){
        stateProcessing.registerComponent(this);
        stateProcessing.registerComponent(logWindow);
        stateProcessing.registerComponent(gameWindow);
        stateProcessing.registerComponent(robotStateWindow);
    }

    /**
     * Загружает сохраненное состояние
     */
    private void loadApplicationState(){
        stateProcessing.restoreAllStates();
    }

    /**
     * Сохраняет состояние
     */
    private void saveApplicationState() {
        stateProcessing.saveAllStates();
    }

    /**
     * Добавляет внутреннее окно в рабочую область
     * @param frame внутреннее окно для добавления
     */
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создает главное меню
     */
    private JMenuBar generateMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }

    /**
     * Создает выпадающее меню "Файл"
     */
    private JMenu createFileMenu(){
        JMenu menu = new JMenu("Файл");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription(
                "Управление файлами и приложением");
        menu.add(createExitMenuItem());

        return menu;
    }

    /**
     * Создает пункт меню для выхода из приложения
     */
    private JMenuItem createExitMenuItem(){
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_DOWN_MASK));
        exitItem.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING)
            );
        });
        return exitItem;
    }

    /**
     * Создает меню выбора режима отображения
     */
    private JMenu createLookAndFeelMenu(){
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        menu.add(createSystemLookAndFeelItem());
        menu.add(createCrossPlatformLookAndFeelItem());

        return menu;
    }

    /**
     *  Создает пункт системная схема
     */
    private JMenuItem createSystemLookAndFeelItem(){
        JMenuItem item = new JMenuItem("Системная схема", KeyEvent.VK_S);
        item.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return item;
    }

    /**
     * Создает пункт универсальная схема
     */
    private JMenuItem createCrossPlatformLookAndFeelItem(){
        JMenuItem item = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        item.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return item;
    }

    /**
     * Создает меню тестовых команд
     */
    private JMenu createTestMenu(){
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        menu.add(createAddLogMessageItem());

        return menu;
    }

    /**
     * Создает пункт для добавления сообщения в лог
     */
    private JMenuItem createAddLogMessageItem(){
        JMenuItem item = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        item.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return item;
    }

    /**
     * Устанавливает LookAndFeel приложения
     * @param className полное имя класса
     */
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
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
