package com.github.le_yams.openfga4intellij.servers.ui;

import com.github.le_yams.openfga4intellij.servers.model.AuthenticationMethod;
import com.github.le_yams.openfga4intellij.servers.model.Server;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ServerDialog extends DialogWrapper {

    private static Logger logger = Logger.getInstance(ServerDialog.class);
    private Server server;
    private final JBTextField nameField = new JBTextField();
    private final JBTextField urlField = new JBTextField();
    private final ComboBox<AuthenticationMethod> authenticationMethodField = new ComboBox<>(AuthenticationMethod.values());
//    private final AddEditDeleteListPanel<String> sharedKeysList = new AddEditDeleteListPanel<>("Shared keys", new ArrayList<>()) {
    private final AddEditDeleteStringListPanel sharedKeysList = new AddEditDeleteStringListPanel("Shared keys", "key value");
    private final JComponent oauthPanel = new JLabel("not supported yet");

    protected ServerDialog() {
        this(null);
    }

    public ServerDialog(@Nullable Server server) {
        super(true);
        this.server = server != null ? server : new Server();
        setTitle(server != null ? "Edit server" : "Add server");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        var dialogPanel = new JPanel(new MigLayout("fillx,wrap 2", "[left]rel[grow,fill]"));

        dialogPanel.add(new JBLabel("name"));
        dialogPanel.add(nameField);

        dialogPanel.add(new JBLabel("url"));
        dialogPanel.add(urlField);

        dialogPanel.add(new JBLabel("authentication method"));
        dialogPanel.add(authenticationMethodField, "grow 0");

        sharedKeysList.setMinimumSize(sharedKeysList.getPreferredSize());

        var authPanelLayout = new CardLayout();
        var authPanel = new JPanel(authPanelLayout);
        authPanel.add(new JPanel(), AuthenticationMethod.NONE.toString());
        authPanel.add(sharedKeysList, AuthenticationMethod.SHARED_KEYS.toString());
        authPanel.add(oauthPanel, AuthenticationMethod.OAUTH.toString());
        dialogPanel.add(authPanel, "span, grow");
        authenticationMethodField.addItemListener(e -> {
            authPanelLayout.show(authPanel, e.getItem().toString());
        });




        var urlFieldSize = urlField.getPreferredSize();
        urlFieldSize.getWidth();
        urlFieldSize.setSize(urlFieldSize.getWidth() * 3, urlFieldSize.getHeight());
        dialogPanel.setPreferredSize(new Dimension(400, 200));
        loadModel();

        return dialogPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return nameField;
    }

    private void loadModel() {
        nameField.setText(server.getName());
        urlField.setText(server.loadUrl());
        authenticationMethodField.setSelectedItem(server.getAuthenticationMethod());
        logger.warn(server + " storing shared keys: " + sharedKeysList.getItems());
        sharedKeysList.setItems(server.loadSharedKeys());
    }

    private Server updateModel() {
        server.setName(nameField.getText());
        server.storeUrl(urlField.getText());
        var authenticationMethod = authenticationMethodField.getItem();
        server.setAuthenticationMethod(authenticationMethod);
        switch (authenticationMethod) {
            case NONE -> {}
            case SHARED_KEYS -> {
                server.storeSharedKeys(sharedKeysList.getItems());
                logger.warn(server + " storing shared keys: " + sharedKeysList.getItems());
            }
            case OAUTH -> {}
        }
        return server;
    }

    public static Server showAddServerDialog() {
        var dialog = new ServerDialog();
        return dialog.showAndGet() ? dialog.updateModel() : null;
    }

    public static Server showEditServerDialog(Server server) {
        var dialog = new ServerDialog(server);
        return dialog.showAndGet() ? dialog.updateModel() : null;
    }
}
