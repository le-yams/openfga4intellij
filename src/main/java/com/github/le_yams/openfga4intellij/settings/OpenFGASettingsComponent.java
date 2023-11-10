package com.github.le_yams.openfga4intellij.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OpenFGASettingsComponent {

    private final JPanel myMainPanel;
    private final TextFieldWithBrowseButton cliPathText = new TextFieldWithBrowseButton();

    public OpenFGASettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("OpenFGA cli path: "), cliPathText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
//        cliPathText.setEditable(false);
        cliPathText.addBrowseFolderListener("Select", "Select OpenFGA cli path", null, new FileChooserDescriptor(
                true, false, false, false, false, false
        ));
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return cliPathText;
    }

    @NotNull
    public String getCliPathText() {
        return cliPathText.getText();
    }

    public void setCliPathText(@NotNull String newText) {
        cliPathText.setText(newText);



    }
}
