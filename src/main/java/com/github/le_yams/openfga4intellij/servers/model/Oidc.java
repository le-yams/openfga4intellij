package com.github.le_yams.openfga4intellij.servers.model;

public record Oidc(
        String clientId,
        String clientSecret,
        String issuer,
        String audience
) {
}
