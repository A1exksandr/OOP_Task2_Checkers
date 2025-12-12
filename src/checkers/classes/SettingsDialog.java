package checkers.classes;

import checkers.game.Game;
import checkers.game.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class SettingsDialog extends JDialog {
    private GameSettings settings;
    private Game game;

    public SettingsDialog(JFrame parent, GameSettings settings, Game game) {
        super(parent, Messages.get("settings.title"), true);
        this.settings = settings;
        this.game = game;
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // === Язык ===
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel(Messages.get("settings.language")), gbc);

        String[] languageLabels = {
                Messages.get("language.russian"),
                Messages.get("language.english"),
                Messages.get("language.french")
        };
        String[] languageCodes = {"ru", "en", "fr"};
        JComboBox<String> languageCombo = new JComboBox<>(languageLabels);
        // Выбираем текущий язык
        Locale current = settings.getLocale();
        String langTag = current.getLanguage();
        if (langTag.equals("en")) {
            languageCombo.setSelectedIndex(1);
        } else if (langTag.equals("fr")) {
            languageCombo.setSelectedIndex(2);
        } else {
            languageCombo.setSelectedIndex(0); // ru по умолчанию
        }
        gbc.gridx = 1;
        add(languageCombo, gbc);

        // === Правила ===
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel(Messages.get("settings.variant")), gbc);

        String[] variantLabels = {
                Messages.get("variant.spanish"),
                Messages.get("variant.russian")
        };
        JComboBox<String> variantCombo = new JComboBox<>(variantLabels);
        variantCombo.setSelectedIndex(settings.getVariant() == GameSettings.Variant.RUSSIAN ? 1 : 0);
        gbc.gridx = 1;
        add(variantCombo, gbc);

        // === Размер клетки ===
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel(Messages.get("settings.cell_size")), gbc);
        JSpinner cellSizeSpinner = new JSpinner(new SpinnerNumberModel(settings.getCellSize(), 30, 100, 5));
        gbc.gridx = 1;
        add(cellSizeSpinner, gbc);

        // === Кнопка сохранения истории ===
        JButton saveHistoryButton = new JButton(Messages.get("button.save_history"));
        saveHistoryButton.addActionListener(e -> game.saveHistoryToFile());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(saveHistoryButton, gbc);

        // === Кнопки OK/Отмена ===
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton(Messages.get("settings.ok"));
        okButton.addActionListener(e -> {
            // Применяем язык
            int langIndex = languageCombo.getSelectedIndex();
            Locale newLocale;
            switch (langIndex) {
                case 1: newLocale = Locale.ENGLISH; break;
                case 2: newLocale = Locale.FRENCH; break;
                default: newLocale = new Locale("ru", "RU");
            }
            settings.setLocale(newLocale);

            // Применяем правила
            settings.setVariant(variantCombo.getSelectedIndex() == 1
                    ? GameSettings.Variant.RUSSIAN
                    : GameSettings.Variant.SPANISH);

            // Размер клетки
            settings.setCellSize((Integer) cellSizeSpinner.getValue());

            dispose();
        });

        JButton cancelButton = new JButton(Messages.get("settings.cancel"));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridy = 4; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }
}