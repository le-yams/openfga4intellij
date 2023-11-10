package com.github.le_yams.openfga4intellij;

import com.github.le_yams.openfga4intellij.psi.OpenFGATypes;
import com.intellij.codeInsight.daemon.RainbowVisitor;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class OpenFGARainbowVisitor extends RainbowVisitor {

    public OpenFGARainbowVisitor() {
    }

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof OpenFGAFile;
    }

    @Override
    public void visit(@NotNull PsiElement element) {
        ASTNode node = element.getNode();
        IElementType elementType = node.getElementType();

        if (elementType.equals(OpenFGATypes.ALPHA_NUMERIC)) {
            PsiElement prevSibling = element.getPrevSibling();
            while (prevSibling != null) {
                IElementType prevSiblingType = prevSibling.getNode().getElementType();
                var guessedType = guessElementTypeFromPreviousSiblingType(prevSiblingType);
                if (guessedType != null) {
                    HighlightInfo info = getHighlightInfo(element, guessedType);
                    addInfo(info);
                    prevSibling = null;
                } else if (prevSiblingType.equals(OpenFGATypes.ALPHA_NUMERIC)) {
                    prevSibling = null;
                } else {
                    prevSibling = prevSibling.getPrevSibling();
                }
            }
        }
    }

    @Override
    public @NotNull HighlightVisitor clone() {
        return new OpenFGARainbowVisitor();
    }

    private IElementType guessElementTypeFromPreviousSiblingType(IElementType prevSiblingType) {
        if (prevSiblingType.equals(OpenFGATypes.L_SQUARE)) {
            return OpenFGATypes.TYPE_IDENTIFIER;
        } else if (prevSiblingType.equals(OpenFGATypes.TYPE)) {
            return OpenFGATypes.TYPE_IDENTIFIER;
        } else if (prevSiblingType.equals(OpenFGATypes.DEFINE)) {
            return OpenFGATypes.RELATION_NAME;
        } else if (prevSiblingType.equals(OpenFGATypes.FROM)) {
            return OpenFGATypes.RELATION_NAME;
        } else if (prevSiblingType.equals(OpenFGATypes.COLON)) {
            return OpenFGATypes.RELATION_NAME;
        } else if (prevSiblingType.equals(OpenFGATypes.OR)) {
            return OpenFGATypes.RELATION_NAME;
        }
        return null;
    }

    private HighlightInfo getHighlightInfo(PsiElement element, IElementType elementType) {
        if (elementType.equals(OpenFGATypes.TYPE_IDENTIFIER)) {
            return getHighlighter().getInfo(1, element, OpenFGASyntaxHighlighter.TYPE_IDENTIFIER);
        }
        if (elementType.equals(OpenFGATypes.RELATION_NAME)) {
            return getHighlighter().getInfo(2, element, OpenFGASyntaxHighlighter.RELATION_NAME);
        }
        return null;
    }
}