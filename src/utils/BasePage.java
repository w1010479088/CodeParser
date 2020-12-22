package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class BasePage extends JFrame {
    private JTextArea mLoggerView;
    private StringBuilder builder = new StringBuilder();

    protected abstract void action(String path);

    protected abstract String title();

    public void showUI() {
        init();
        configUI();
    }

    protected void log(String content) {
        builder.append(content);
        builder.append("\n");
        mLoggerView.setText(builder.toString());
    }

    private void init() {
        setLocation(200, 100);
        setSize(500, 300);
        setVisible(true);
        setTitle(title());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }

    private void configUI() {
        JTextField inputView = new JTextField();
        inputView.setBackground(Color.GREEN);
        add(BorderLayout.NORTH, inputView);
        inputView.addActionListener(actionEvent -> {
            String path = inputView.getText();
            rename(path);
        });
        mLoggerView = new JTextArea();
        add(BorderLayout.CENTER, new JScrollPane(mLoggerView));
    }

    private void rename(String path) {
        if (TextUtil.isEmpty(path)) {
            log("文件夹为空");
        } else {
            builder.setLength(0);
            action(path);
        }
    }
}
