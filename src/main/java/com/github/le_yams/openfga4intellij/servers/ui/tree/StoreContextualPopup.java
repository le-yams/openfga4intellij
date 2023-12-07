package com.github.le_yams.openfga4intellij.servers.ui.tree;

import javax.swing.*;

import static com.github.le_yams.openfga4intellij.servers.util.UIUtil.copyToClipboard;

public class StoreContextualPopup extends JPopupMenu {

    private final StoreNode storeNode;
    private final OpenFgaTreeModel model;

    public StoreContextualPopup(OpenFgaTreeModel model, StoreNode storeNode) {
        this.model = model;
        this.storeNode = storeNode;

        add(copyIdMenuItem());
        add(copyNameMenuItem());
    }

    private JMenuItem copyNameMenuItem() {
        var refreshMenuItem = new JMenuItem("copy name");
        refreshMenuItem.addActionListener(e -> copyToClipboard(storeNode.getStore().getName()));
        return refreshMenuItem;
    }

    private JMenuItem copyIdMenuItem() {
        var refreshMenuItem = new JMenuItem("copy id");
        refreshMenuItem.addActionListener(e -> copyToClipboard(storeNode.getStore().getId()));
        return refreshMenuItem;
    }
}
