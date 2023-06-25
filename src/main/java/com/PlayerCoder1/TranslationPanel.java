package com.PlayerCoder1;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TranslationPanel extends PluginPanel
{
    private final TranslationPlugin plugin;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public TranslationPanel(TranslationPlugin plugin)
    {
        super();
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);


        JList<String> messageList = new JList<>(listModel);
        messageList.setBackground(Color.LIGHT_GRAY);
        messageList.setFont(new Font("Arial", Font.PLAIN, 14));


        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateMessages()
    {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (String message : plugin.getLastMessages()) {
                listModel.addElement(message);
            }
        });
    }
}
