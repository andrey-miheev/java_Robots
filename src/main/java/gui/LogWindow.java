package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LogWindow extends JInternalFrame implements LogChangeListener, StateWindows
{
    private final String COMPONENT_ID = "log";

    private LogWindowSource logSource;
    private TextArea logContent;
    private final ComponentStateHandler stateHandler;

    public LogWindow(LogWindowSource logSource, ComponentStateHandler stateHandler)
    {
        super("Протокол работы", true, true, true, true);
        this.logSource = logSource;
        this.stateHandler = stateHandler;
        this.logSource.registerListener(this);
        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }
    
    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
