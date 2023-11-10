package com.github.le_yams.openfga4intellij.actions;

import com.github.le_yams.openfga4intellij.OpenFGALanguage;
import com.github.le_yams.openfga4intellij.settings.OpenFGASettingsState;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DslToJsonAction extends AnAction {

    private static final Logger logger = Logger.getInstance(DslToJsonAction.class);

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        var psiFile = event.getData(CommonDataKeys.PSI_FILE);
        var actionVisible = psiFile != null && psiFile.getLanguage().isKindOf(OpenFGALanguage.INSTANCE);
        event.getPresentation().setEnabledAndVisible(actionVisible);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var psiFile = event.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }

        var virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null) {
            return;
        }

        var settings = OpenFGASettingsState.getInstance();
        if (settings.cliPath == null || settings.cliPath.isEmpty()) {
            logger.warn("openfga cli path is not defined");
            return;
        }

        var fileName = psiFile.getName();
        var targetName = fileName.replaceAll("\\.(fga)|(openfga)$", ".json");

        var filePath = virtualFile.toNioPath();
        var target = filePath.getParent().resolve(targetName);

        if (Files.exists(target)) {
            var title = "Replace Existing File?";
            var message = "The file " + target + " already exists. Overrides it?";
            if (Messages.showYesNoDialog(psiFile.getProject(), message, title, null) == Messages.NO) {
                return;
            }
        }

        var processBuilder = new ProcessBuilder(
                settings.cliPath,
                "model",
                "transform",
                "--file",
                filePath.toString(),
                "--input-format",
                "fga"
        );

        try {
            var outputFile = File.createTempFile("openfga", "");
            var errorFile = File.createTempFile("openfga", "error");

            processBuilder.redirectError(errorFile);
            processBuilder.redirectOutput(outputFile);
            processBuilder.start().onExit().thenAccept(process -> {
                try {
                    if (process.exitValue() == 0) {
                        Files.copy(outputFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                        ApplicationManager.getApplication().invokeLater(
                                () -> show(new GeneratedFile(psiFile.getProject(), target)),
                                ModalityState.NON_MODAL);
                    } else {
                        var errors = Files.readString(errorFile.toPath());
                        logger.warn(errors);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private void show(GeneratedFile generatedFile) {
        generatedFile.refreshInTreeView();
        generatedFile.openInEditor();
    }

    private static final class GeneratedFile {
        private final Project project;
        private final VirtualFile virtualFile;

        private GeneratedFile(Project project, Path path) {
            this.project = project;
            virtualFile = LocalFileSystem.getInstance().findFileByNioFile(path);
        }

        private void openInEditor() {
            var editorManager = FileEditorManager.getInstance(project);
            var editors = editorManager.openFile(virtualFile, true);

            for (var editor : editors) {
                editor.getFile().refresh(false, false);
            }
        }

        private void refreshInTreeView() {
            virtualFile.getParent().refresh(false, false);
        }
    }

}