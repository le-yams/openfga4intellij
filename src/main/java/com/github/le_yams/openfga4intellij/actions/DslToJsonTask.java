package com.github.le_yams.openfga4intellij.actions;

import com.github.le_yams.openfga4intellij.Notifier;
import com.github.le_yams.openfga4intellij.settings.OpenFGASettingsState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class DslToJsonTask extends Task.Backgroundable {
    private final PsiFile dslFile;
    private final Path dslFilePath;
    private final Path targetPath;
    private Runnable onCancel;

    public static Optional<DslToJsonTask> create(@NotNull PsiFile dslFile, @NotNull Path dslFilePath) {
        var targetName = DslToJsonAction.computeJsonGeneratedFileName(dslFile);
        var targetPath = dslFilePath.getParent().resolve(targetName);

        if (Files.exists(targetPath)) {
            var title = "Replace Existing File?";
            var message = "The file " + targetPath + " already exists. Overrides it?";
            if (Messages.showYesNoDialog(dslFile.getProject(), message, title, null) == Messages.NO) {
                return Optional.empty();
            }
        }

        return Optional.of(new DslToJsonTask(dslFile, dslFilePath, targetPath));
    }


    private DslToJsonTask(@NotNull PsiFile dslFile, @NotNull Path dslFilePath, Path targetPath) {
        super(dslFile.getProject(), "Generating json model for " + dslFile.getName(), true);
        this.dslFile = dslFile;
        this.dslFilePath = dslFilePath;
        this.targetPath = targetPath;

    }

    @Override
    public void onCancel() {
        if (onCancel != null) {
            onCancel();
        }
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        progressIndicator.setIndeterminate(true);

        var processBuilder = new ProcessBuilder(
                OpenFGASettingsState.getInstance().cliPath,
                "model",
                "transform",
                "--file",
                dslFilePath.toString(),
                "--input-format",
                "fga"
        );

        try {
            var outputFile = File.createTempFile("openfga", "");
            var errorFile = File.createTempFile("openfga", "error");

            processBuilder.redirectError(errorFile);
            processBuilder.redirectOutput(outputFile);
            Process process = processBuilder.start();
            onCancel = process::destroy;
            process.onExit().thenAccept(exitedProcess -> {
                try {
                    if (exitedProcess.exitValue() == 0) {
                        if (!progressIndicator.isCanceled()) {
                            Files.copy(outputFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                            ApplicationManager.getApplication().invokeLater(
                                    () -> show(new GeneratedFile(dslFile.getProject(), targetPath)),
                                    ModalityState.NON_MODAL);
                        }
                    } else {
                        var errors = Files.readString(errorFile.toPath());
                        notifyError(dslFile, errors);
                    }
                } catch (IOException e) {
                    notifyError(dslFile, e);
                } finally {
                    outputFile.delete();
                    errorFile.delete();
                }

            });
        } catch (IOException e) {
            notifyError(dslFile, e);
        }
    }


    private static void notifyError(PsiFile psiFile, IOException e) {
        notifyError(psiFile, e.getMessage());
    }

    private static void notifyError(PsiFile psiFile, String message) {
        Notifier.notifyError(psiFile.getProject(), "Error generating json authorization model", message);
    }

    private void show(GeneratedFile generatedFile) {
        generatedFile.refreshInTreeView();
        generatedFile.openInEditor();
    }

    private final class GeneratedFile {
        private final Project project;
        private final VirtualFile virtualFile;

        private GeneratedFile(Project project, Path path) {
            this.project = project;
            virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(path);
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

    static class CancellationListener {

    }

}
