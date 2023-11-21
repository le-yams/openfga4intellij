package com.github.le_yams.openfga4intellij.servers.model;

public enum AuthenticationMethod {
    NONE,
    SHARED_KEYS,
    OAUTH;;

    @Override
    public String toString() {
        return switch (this) {
            case NONE -> "none";
            case SHARED_KEYS -> "shared keys";
            case OAUTH -> "OAuth";
        };
    }
}
