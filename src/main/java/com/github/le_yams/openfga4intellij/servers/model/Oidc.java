package com.github.le_yams.openfga4intellij.servers.model;

public record Oidc(
        String authority,
        String clientId,
        String clientSecret,
        String scope
) {
}
