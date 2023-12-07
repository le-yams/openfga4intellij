package com.github.le_yams.openfga4intellij.servers.ui;

import com.github.le_yams.openfga4intellij.servers.service.OpenFGAServers;
import com.github.le_yams.openfga4intellij.servers.ui.tree.*;
import com.github.le_yams.openfga4intellij.util.notifications.ToolWindowNotifier;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Optional;

import static com.github.le_yams.openfga4intellij.servers.ui.tree.NodeType.SERVER_NODE;

public class ServersTreePanel extends JPanel {

    private final ToolWindow toolWindow;
    private final ToolWindowNotifier toolWindowNotifier;
    private final OpenFGAServers servers;
    private final Tree tree;
    private final RootNode root;
    private final OpenFgaTreeModel treeModel;


    public ServersTreePanel(ToolWindow toolWindow, OpenFGAServers servers) {
        super(new BorderLayout());
        this.toolWindow = toolWindow;
        this.toolWindowNotifier = new ToolWindowNotifier(toolWindow);
        this.servers = servers;
        treeModel = new OpenFgaTreeModel(toolWindowNotifier);
        tree = new Tree(treeModel);
        root = treeModel.getRootNode();
        tree.setExpandableItemsEnabled(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new CellRenderer());
        tree.addTreeWillExpandListener(treeModel);

        var toolbarDecorator = ToolbarDecorator.createDecorator(tree);

        toolbarDecorator.setAddAction(this::addAction);

        toolbarDecorator.setEditActionUpdater(updater -> getSelectedServerNode().isPresent());
        toolbarDecorator.setEditAction(this::editAction);

        toolbarDecorator.setRemoveActionUpdater(updater -> getSelectedServerNode().isPresent());
        toolbarDecorator.setRemoveAction(this::removeAction);

        toolbarDecorator.setMoveDownAction(this::moveDownAction);
        toolbarDecorator.setMoveUpAction(this::moveUpAction);

        add(toolbarDecorator.createPanel(), BorderLayout.CENTER);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int selectedRow = tree.getRowForLocation(event.getX(), event.getY());
                TreePath selectedPath = tree.getPathForLocation(event.getX(), event.getY());
                if (selectedPath == null) {
                    return;
                }
                var node = (TreeNode) selectedPath.getLastPathComponent();
                if (SwingUtilities.isRightMouseButton(event)) {
                    switch (node.getType()) {

                        case ROOT_NODE -> {
                        }
                        case SERVER_NODE -> {
                            var serverNode = (ServerNode) node;
                            tree.setSelectionRow(selectedRow);
                            var popupMenu = new ServerContextualPopup(treeModel, serverNode);
                            popupMenu.show(event.getComponent(), event.getX(), event.getY());
                        }
                        case STORE_NODE -> {
                            var storeNode = (StoreNode) node;
                            tree.setSelectionRow(selectedRow);
                            var popupMenu = new StoreContextualPopup(treeModel, storeNode);
                            popupMenu.show(event.getComponent(), event.getX(), event.getY());
                        }
                    }
                }
            }
        });
    }

    private void addAction(AnActionButton anActionButton) {
        ServerDialog.showAddServerDialog(toolWindow).ifPresent(server -> {
            servers.add(server);
            var newNode = new ServerNode(server, toolWindowNotifier);
            root.add(newNode);
            var treePath = new TreePath(newNode.getPath());
            tree.setSelectionPath(treePath);
            tree.scrollPathToVisible(treePath);
            tree.updateUI();
        });
    }

    private void editAction(AnActionButton anActionButton) {
        getSelectedServerNode()
                .flatMap(node -> ServerDialog.showEditServerDialog(toolWindow, node.getServer()))
                .ifPresent(server -> {
                    servers.update(server);
                    tree.updateUI();
                });
    }

    private void removeAction(AnActionButton anActionButton) {
        var selectedNode = getSelectedServerNode();
        selectedNode.ifPresent(node -> {
            var server = node.getServer();

            var title = "Confirm OpenFGA Server Deletion";
            var message = "Deleting the OpenFGA server '" + server.getName() + "' is not reversible. Do you confirm the server deletion?";
            Messages.showYesNoDialog(this, message, title, null);

            servers.remove(server);
            var childIndex = root.getIndex(node);
            var pathToSelect = new TreePath(root.getPath());
            if (root.getChildCount() != 0) {
                var indexToSelect = childIndex >= root.getChildCount() ? root.getChildCount() - 1 : childIndex;
                var serverNode = (ServerNode) root.getChildAt(indexToSelect);
                pathToSelect = new TreePath(serverNode.getPath());
            }
            tree.setSelectionPath(pathToSelect);
            tree.updateUI();
        });
    }

    private void moveDownAction(AnActionButton anActionButton) {
        getSelectedServerNode().ifPresent(node -> {
            var indexToSelect = servers.moveDown(node.getServer());
            root.loadChildren(treeModel);
            var serverNode = (ServerNode) root.getChildAt(indexToSelect);
            var pathToSelect = new TreePath(serverNode.getPath());
            tree.setSelectionPath(pathToSelect);
            tree.updateUI();
        });
    }

    private void moveUpAction(AnActionButton anActionButton) {
        getSelectedServerNode().ifPresent(node -> {
            var indexToSelect = servers.moveUp(node.getServer());
            root.loadChildren(treeModel);
            var serverNode = (ServerNode) root.getChildAt(indexToSelect);
            var pathToSelect = new TreePath(serverNode.getPath());
            tree.setSelectionPath(pathToSelect);
            tree.updateUI();
        });
    }

    @NotNull
    private Optional<ServerNode> getSelectedServerNode() {
        return Arrays
                .stream(tree.getSelectedNodes(TreeNode.class, node -> node.is(SERVER_NODE)))
                .findFirst()
                .map(ServerNode.class::cast);
    }
}
