package com.github.le_yams.openfga4intellij.servers.ui.tree;

import javax.swing.*;

public interface TreeNode {

    NodeType getType();

    String getText();

    default String getToolTipText() {
        return null;
    }

    default Icon getIcon() {
        return null;
    }

    default boolean is(NodeType nodeType) {
        return nodeType == getType();
    }
}