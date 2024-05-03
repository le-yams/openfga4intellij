package com.github.le_yams.openfga4intellij.servers.util;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class UIUtil {

    public static void copyToClipboard(String text) {
        var stringSelection = new StringSelection(text);
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}