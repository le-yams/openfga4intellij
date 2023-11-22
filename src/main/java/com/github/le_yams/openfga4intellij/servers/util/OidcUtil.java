package com.github.le_yams.openfga4intellij.servers.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.le_yams.openfga4intellij.servers.model.Oidc;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.grants.ClientCredentialsGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc5545.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.time.temporal.ChronoUnit.SECONDS;

class OidcUtil {

    private static final String DISCOVERY_DOCUMENT = "/.well-known/openid-configuration";

    static String getAccessToken(Oidc oidc) throws OidcException {
        var tokenEndpoint = getTokenEndpoint(oidc);
        return createAccessToken(tokenEndpoint, oidc);
    }

    private static String getTokenEndpoint(Oidc oidc) throws OidcException {
        var authorityUri = URI.create(oidc.authority());

        var documentUri = UriUtil.resolve(authorityUri, DISCOVERY_DOCUMENT);
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(documentUri)
                .header("accept", "application/json")
                .timeout(java.time.Duration.of(10, SECONDS))
                .build();

        var client = HttpClient
                .newBuilder()
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() > 299) {
                throw new OidcException("failed to retrieve discovery document at " + documentUri + " with http code" + response.statusCode());
            }

            var discoveryDocument = new ObjectMapper()
                    .readValue(response.body(), DiscoveryDocument.class);

            return discoveryDocument.getTokenEndpoint();
        } catch (IOException | InterruptedException e) {
            throw new OidcException("failed to retrieve discovery document at " + documentUri + ":" + e.getMessage(), e);
        }
    }

    private static String createAccessToken(String tokenEndpoint, Oidc oidc) throws OidcException {
        var provider = new BasicOAuth2AuthorizationProvider(
                URI.create("dummy"),
                URI.create(tokenEndpoint),
                new Duration(1, 0, 3600));

        var credentials = new BasicOAuth2ClientCredentials(
                oidc.clientId(), oidc.clientSecret());

        var client = new BasicOAuth2Client(
                provider,
                credentials,
                (URI) null);

        var executor = new HttpUrlConnectionExecutor();
        try {
            var token = new ClientCredentialsGrant(client, new BasicScope(oidc.scope())).accessToken(executor);
            return String.valueOf(token.accessToken());
        } catch (IOException | ProtocolError | ProtocolException e) {
            throw new OidcException("failed to retrieve access token: " + e.getMessage(), e);
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class DiscoveryDocument {

        @JsonProperty("token_endpoint")
        private String tokenEndpoint;

        public String getTokenEndpoint() {
            return tokenEndpoint;
        }

        public void setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
        }
    }

}
