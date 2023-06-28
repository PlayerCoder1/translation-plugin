package com.PlayerCoder1;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TranslationPanel extends PluginPanel {
    private final TranslationPlugin plugin;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JComboBox<String> languageComboBox;

    public TranslationPanel(TranslationPlugin plugin) {
        super();
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));


        languageComboBox = new JComboBox<>(new String[]{"ES", "FR", "IT", "NL"});
        languageComboBox.addActionListener(e -> {

            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            plugin.setTargetLanguage(selectedLanguage);
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.add(new JLabel("Target Language: "));
        topPanel.add(languageComboBox);

        add(topPanel, BorderLayout.NORTH);

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
