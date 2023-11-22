package com.github.le_yams.openfga4intellij.servers.util;

public class OidcException extends Exception {

    public OidcException(String message) {
        super(message);
    }

    public OidcException(String message, Throwable cause) {
        super(message, cause);
    }
}
