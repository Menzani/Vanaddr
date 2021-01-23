package com.vanaddr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

class MainWindow {
    private final JTextField accountIdField;
    private final JTextField secretSeedField;
    private final JButton copyAccountIdButton;
    private final JButton copySecretSeedButton;

    MainWindow() {
        JCheckBox initialCharactersCheck = new JCheckBox("Match initial characters:");
        initialCharactersCheck.setFocusable(false);
        JTextFieldWithPlaceholder initialCharactersField = new JTextFieldWithPlaceholder("without the initial 'G'");
        initialCharactersField.setEnabled(false);
        JCheckBox finalCharactersCheck = new JCheckBox("Match final characters:");
        finalCharactersCheck.setFocusable(false);
        JTextField finalCharactersField = new JTextField();
        finalCharactersField.setEnabled(false);
        JButton searchButton = new JButton("Search");
        searchButton.setFocusable(false);
        searchButton.setEnabled(false);
        copyAccountIdButton = new JButton("Copy account ID");
        copyAccountIdButton.setFocusable(false);
        copyAccountIdButton.setEnabled(false);
        copySecretSeedButton = new JButton("Copy secret seed");
        copySecretSeedButton.setFocusable(false);
        copySecretSeedButton.setEnabled(false);
        JPanel controls = new JPanel();
        controls.setAlignmentX(Component.LEFT_ALIGNMENT);
        controls.add(searchButton);
        controls.add(copyAccountIdButton);
        controls.add(copySecretSeedButton);
        JLabel elapsedTime = new JLabel(" ");
        JPanel elapsedTimePanel = new JPanel();
        elapsedTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        elapsedTimePanel.add(elapsedTime);
        JLabel accountIdLabel = new JLabel("Account ID:");
        accountIdField = new JTextField(50);
        accountIdField.setEditable(false);
        JLabel secretSeedLabel = new JLabel("Secret seed:");
        secretSeedField = new JTextField(50);
        secretSeedField.setEditable(false);

        initialCharactersCheck.addItemListener(event -> {
            initialCharactersField.setEnabled(event.getStateChange() == ItemEvent.SELECTED);
        });
        finalCharactersCheck.addItemListener(event -> {
            finalCharactersField.setEnabled(event.getStateChange() == ItemEvent.SELECTED);
        });
        ItemListener characterChecksListener = event -> {
            searchButton.setEnabled(initialCharactersCheck.isSelected() || finalCharactersCheck.isSelected());
        };
        initialCharactersCheck.addItemListener(characterChecksListener);
        finalCharactersCheck.addItemListener(characterChecksListener);
        searchButton.addActionListener(event -> {
            searchButton.setEnabled(false);
            makeNonEditable(initialCharactersCheck);
            makeNonEditable(finalCharactersCheck);
            initialCharactersField.setFocusable(false);
            finalCharactersField.setFocusable(false);
            Cruncher.crunch(initialCharactersCheck.isSelected(), finalCharactersCheck.isSelected(), initialCharactersField.getText(), finalCharactersField.getText(), this);
            TimeElapsed.startTimer(elapsedTime);
        });
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        copyAccountIdButton.addActionListener(event -> {
            clipboard.setContents(new StringSelection(accountIdField.getText()), null);
        });
        copySecretSeedButton.addActionListener(event -> {
            clipboard.setContents(new StringSelection(secretSeedField.getText()), null);
        });

        JFrame frame = new JFrame("Vanaddr");
        try {
            final Class<?> clazz = getClass();
            frame.setIconImages(List.of(
                    ImageIO.read(clazz.getResourceAsStream("icon24.png")),
                    ImageIO.read(clazz.getResourceAsStream("icon48.png"))
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.add(initialCharactersCheck);
        frame.add(initialCharactersField);
        frame.add(finalCharactersCheck);
        frame.add(finalCharactersField);
        frame.add(controls);
        frame.add(elapsedTimePanel);
        frame.add(accountIdLabel);
        frame.add(accountIdField);
        frame.add(secretSeedLabel);
        frame.add(secretSeedField);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void makeNonEditable(JCheckBox checkBox) {
        for (MouseListener eventListener : checkBox.getListeners(MouseListener.class)) {
            checkBox.removeMouseListener(eventListener);
        }
    }

    void setResult(String accountId, String secretSeed) {
        SwingUtilities.invokeLater(() -> {
            accountIdField.setText(accountId);
            secretSeedField.setText(secretSeed);
            copyAccountIdButton.setEnabled(true);
            copySecretSeedButton.setEnabled(true);
        });
    }
}
