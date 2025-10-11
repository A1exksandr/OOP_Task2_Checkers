package checkers;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class SettingsDialog extends JDialog {
    private GameSettings settings;
    private Game game;

    public SettingsDialog(JFrame parent, GameSettings settings, Game game) {
        super(parent, "Настройки", true);
        this.settings = settings;
        this.game = game;
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Вариант игры
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Правила:"), gbc);
        String[] variants = {"Испанские", "Русские"};
        JComboBox<String> variantCombo = new JComboBox<>(variants);
        variantCombo.setSelectedIndex(settings.getVariant() == GameSettings.Variant.RUSSIAN ? 1 : 0);
        gbc.gridx = 1;
        add(variantCombo, gbc);

        // Размер клетки
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Размер клетки:"), gbc);
        JSpinner cellSizeSpinner = new JSpinner(new SpinnerNumberModel(settings.getCellSize(), 30, 100, 5));
        gbc.gridx = 1;
        add(cellSizeSpinner, gbc);

        // Кнопка сохранения истории
        JButton saveHistoryButton = new JButton("Сохранить историю");
        saveHistoryButton.addActionListener(e -> game.saveHistoryToFile());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(saveHistoryButton, gbc);

        // Кнопки OK/Отмена
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            settings.setVariant(variantCombo.getSelectedIndex() == 1 ? GameSettings.Variant.RUSSIAN : GameSettings.Variant.SPANISH);
            settings.setCellSize((Integer) cellSizeSpinner.getValue());
            dispose();
        });
        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridy = 3;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }
}