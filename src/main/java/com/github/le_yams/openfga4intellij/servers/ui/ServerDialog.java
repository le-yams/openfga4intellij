package com.github.le_yams.openfga4intellij.servers.ui;

import com.github.le_yams.openfga4intellij.servers.model.AuthenticationMethod;
import com.github.le_yams.openfga4intellij.servers.model.Oidc;
import com.github.le_yams.openfga4intellij.servers.model.Server;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class ServerDialog extends DialogWrapper {
    private final Server server;

    private final JBTextField nameField = new JBTextField();
    private final JBTextField urlField = new JBTextField();
    private final ComboBox<AuthenticationMethod> authenticationMethodField = new ComboBox<>(AuthenticationMethod.values());
    private final JBPasswordField apiTokenField = new JBPasswordField();
    private final JBTextField oidcClientIdField = new JBTextField();
    private final JBPasswordField oidcClientSecretField = new JBPasswordField();
    private final JBTextField oidcIssuerField = new JBTextField();
    private final JBTextField oidcAudienceField = new JBTextField();

    protected ServerDialog() {
        this(null);
    }

    public ServerDialog(@Nullable Server server) {
        super(true);
        this.server = server != null ? server : new Server();
        setTitle(server != null ? "Edit Server" : "Add Server");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        DialogPanel dialogPanel = new DialogPanel(new MigLayout("fillx,wrap 2", "[left]rel[grow,fill]"));

        dialogPanel.add(new JBLabel("Name"));
        dialogPanel.add(nameField);

        dialogPanel.add(new JBLabel("Url"));
        dialogPanel.add(urlField);

        dialogPanel.add(new JBLabel("Authentication method"));
        dialogPanel.add(authenticationMethodField, "grow 0");

        dialogPanel.add(createAuthenticationPanel(), "span, grow");

        loadModel();

        return dialogPanel;
    }

    @NotNull
    private JPanel createAuthenticationPanel() {
        var authPanel = new JPanel(new BorderLayout());
        var noAuthPanel = new JPanel();
        var apiTokenPanel = createApiTokenPanel();
        var oidcPanel = createOidcPanel();
        authenticationMethodField.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            var newInnerPanel = switch ((AuthenticationMethod) e.getItem()) {
                case NONE -> noAuthPanel;
                case API_TOKEN -> apiTokenPanel;
                case OIDC -> oidcPanel;
            };
            SwingUtilities.invokeLater(() -> {
                authPanel.removeAll();
                authPanel.add(newInnerPanel, BorderLayout.CENTER);
                pack();
            });
        });
        return authPanel;
    }

    private JPanel createApiTokenPanel() {
        var apiTokenPanel = new JPanel(new MigLayout("fillx,wrap 2", "[left]rel[grow,fill]"));

        apiTokenPanel.add(new JBLabel("Api token"));
        apiTokenPanel.add(apiTokenField);

        return apiTokenPanel;
    }

    private JPanel createOidcPanel() {
        var oidcPanel = new JPanel(new MigLayout("fillx,wrap 2", "[left]rel[grow,fill]"));

        oidcPanel.add(new JBLabel("Client id"));
        oidcPanel.add(oidcClientIdField);

        oidcPanel.add(new JBLabel("Client secret"));
        oidcPanel.add(oidcClientSecretField);

        oidcPanel.add(new JBLabel("Issuer"));
        oidcPanel.add(oidcIssuerField);

        oidcPanel.add(new JBLabel("Audience"));
        oidcPanel.add(oidcAudienceField);

        return oidcPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return nameField;
    }

    private void loadModel() {
        nameField.setText(server.getName());
        urlField.setText(server.loadUrl());
        authenticationMethodField.setSelectedItem(server.getAuthenticationMethod());
        apiTokenField.setText(server.loadApiToken());
        var oidc = server.loadOidc();
        oidcClientIdField.setText(oidc.clientId());
        oidcClientSecretField.setText(oidc.clientSecret());
        oidcIssuerField.setText(oidc.issuer());
        oidcAudienceField.setText(oidc.audience());

    }

    private Server updateModel() {
        return writeToModel(server);
    }

    private Server writeToModel(Server server) {
        server.setName(nameField.getText());
        server.storeUrl(urlField.getText());
        var authenticationMethod = authenticationMethodField.getItem();
        server.setAuthenticationMethod(authenticationMethod);
        switch (authenticationMethod) {
            case NONE -> {
            }
            case API_TOKEN -> server.storeApiToken(new String(apiTokenField.getPassword()));
            case OIDC -> server.storeOidc(new Oidc(
                    oidcClientIdField.getText(),
                    new String(oidcClientSecretField.getPassword()),
                    oidcIssuerField.getText(),
                    oidcAudienceField.getText()
            ));
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

