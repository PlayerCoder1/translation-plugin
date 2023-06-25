package com.PlayerCoder1;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TranslationPanel extends PluginPanel {
    private final TranslationPlugin plugin;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public TranslationPanel(TranslationPlugin plugin) {
        super();
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        JList<String> messageList = new JList<>(listModel);
        messageList.setBackground(new Color(30, 30, 30));
        messageList.setForeground(Color.WHITE);
        messageList.setFont(new Font("Arial", Font.PLAIN, 12));


        messageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(10, 10, 10, 10));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateMessages() {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (String message : plugin.getLastMessages()) {
                listModel.addElement(message);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 500);
    }
}
