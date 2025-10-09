// SettingsDialog.java
package checkers;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class SettingsDialog extends JDialog {
    private GameSettings settings;
    private JComboBox<String> langCombo;
    private JComboBox<String> variantCombo;

    public SettingsDialog(JFrame parent, GameSettings settings) {
        super(parent, Messages.get("settings.title"), true);
        this.settings = settings;
        initializeComponents();
        layoutComponents();
        loadCurrentSettings();
        setSize(300, 180);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        langCombo = new JComboBox<>(new String[]{
                Messages.get("language.english"),
                Messages.get("language.russian")
        });
        variantCombo = new JComboBox<>(new String[]{
                Messages.get("variant.spanish"),
                Messages.get("variant.russian")
        });
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel(Messages.get("settings.language") + " "), gbc);

        gbc.gridx = 1;
        add(langCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel(Messages.get("settings.variant") + " "), gbc);

        gbc.gridx = 1;
        add(variantCombo, gbc);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton(Messages.get("settings.ok"));
        okButton.addActionListener(e -> applyAndClose());
        JButton cancelButton = new JButton(Messages.get("settings.cancel"));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void loadCurrentSettings() {
        if (settings.getLocale().getLanguage().equals("ru")) {
            langCombo.setSelectedIndex(1);
        } else {
            langCombo.setSelectedIndex(0);
        }
        variantCombo.setSelectedIndex(
                settings.getVariant() == GameSettings.Variant.RUSSIAN ? 1 : 0
        );
    }

    private void applyAndClose() {
        // Язык
        if (langCombo.getSelectedIndex() == 1) {
            settings.setLocale(new Locale("ru", "RU"));
        } else {
            settings.setLocale(Locale.ENGLISH);
        }
        // Вариант
        settings.setVariant(
                variantCombo.getSelectedIndex() == 1
                        ? GameSettings.Variant.RUSSIAN
                        : GameSettings.Variant.SPANISH
        );
        dispose();
    }
}