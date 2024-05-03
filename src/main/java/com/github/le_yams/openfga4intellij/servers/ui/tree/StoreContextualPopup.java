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
        var menuItem = new JMenuItem("copy name");
        menuItem.addActionListener(e -> copyToClipboard(storeNode.getStore().getName()));
        return menuItem;
    }

    private JMenuItem copyIdMenuItem() {
        var menuItem = new JMenuItem("copy id");
        menuItem.addActionListener(e -> copyToClipboard(storeNode.getStore().getId()));
        return menuItem;
    }
}
