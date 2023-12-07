package com.github.le_yams.openfga4intellij.servers.ui.tree;

import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.github.le_yams.openfga4intellij.servers.util.UIUtil.copyToClipboard;

public class ServerContextualPopup extends JPopupMenu {

    private final ServerNode serverNode;
    private final OpenFgaTreeModel model;

    public ServerContextualPopup(OpenFgaTreeModel model, ServerNode serverNode) {
        this.model = model;
        this.serverNode = serverNode;

        add(refreshMenuItem());
        addSeparator();
        add(copyNameMenuItem());
        add(copyUrlMenuItem());
    }

    private JMenuItem refreshMenuItem() {
        var refreshMenuItem = new JMenuItem("refresh", AllIcons.Actions.Refresh);
        refreshMenuItem.addActionListener(this::refresh);
        return refreshMenuItem;
    }

    private void refresh(ActionEvent event) {
        serverNode.forceNextReload();
        serverNode.loadChildren(model);
    }

    private JMenuItem copyNameMenuItem() {
        var refreshMenuItem = new JMenuItem("copy name");
        refreshMenuItem.addActionListener(e -> copyToClipboard(serverNode.getServer().getName()));
        return refreshMenuItem;
    }

    private JMenuItem copyUrlMenuItem() {
        var refreshMenuItem = new JMenuItem("copy url");
        refreshMenuItem.addActionListener(e -> copyToClipboard(serverNode.getServer().getUrl()));
        return refreshMenuItem;
    }
}
