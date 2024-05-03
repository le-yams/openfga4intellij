package com.github.le_yams.openfga4intellij.servers.ui.tree;

import com.github.le_yams.openfga4intellij.servers.model.Server;
import com.github.le_yams.openfga4intellij.servers.service.OpenFGAServers;
import com.github.le_yams.openfga4intellij.util.notifications.ToolWindowNotifier;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.List;

public class RootNode extends DefaultMutableTreeNode implements TreeNode {

    private final ToolWindowNotifier toolWindowNotifier;

    public RootNode(ToolWindowNotifier toolWindowNotifier) {
        super("OpenFGA Servers", true);
        this.toolWindowNotifier = toolWindowNotifier;
    }

    @Override
    public NodeType getType() {
        return NodeType.ROOT_NODE;
    }

    @Override
    public String getText() {
        return String.valueOf(getUserObject());
    }

    private List<Server> getServers() {
        return OpenFGAServers.getInstance().getServers();
    }

    public void reloadChildren(OpenFgaTreeModel model) {
        removeAllChildren();
        for (Server server : getServers()) {
            MutableTreeNode newChild = new ServerNode(server, toolWindowNotifier);
            model.insertNodeInto(newChild, this, ((MutableTreeNode) this).getChildCount());

        }
    }
}
