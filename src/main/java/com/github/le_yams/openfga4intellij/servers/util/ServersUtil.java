package com.github.le_yams.openfga4intellij.servers.util;

import com.github.le_yams.openfga4intellij.servers.model.Oidc;
import com.github.le_yams.openfga4intellij.servers.model.Server;
import dev.openfga.sdk.api.client.ClientListStoresResponse;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientListStoresOptions;
import dev.openfga.sdk.api.configuration.Credentials;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.time.temporal.ChronoUnit.SECONDS;


public class ServersUtil {

    public static CompletableFuture<Integer> testConnection(Server server) throws ServerConnectionException {
        var apiUrl = server.loadUrl();
        return switch (server.getAuthenticationMethod()) {
            case OIDC -> testOidcConnection(server.loadOidc(), apiUrl); // openfga sdk oidc implementation doesn't work properly
            case NONE -> testListStoresWithOpenFgaClient(apiUrl);
            case API_TOKEN -> testListStoresWithOpenFgaClient(apiUrl, Optional.of(server.loadApiToken()));
        };
    }

    private static CompletableFuture<Integer> testListStoresWithOpenFgaClient(String apiUrl) throws ServerConnectionException {
        return testListStoresWithOpenFgaClient(apiUrl, Optional.empty());
    }

    private static CompletableFuture<Integer> testListStoresWithOpenFgaClient(String apiUrl, Optional<String> apiToken) throws ServerConnectionException {
        var config = new ClientConfiguration().apiUrl(apiUrl);
        apiToken.ifPresent(token ->
                config.credentials(new Credentials(new ApiToken(token))));

        try {
            var fgaClient = new OpenFgaClient(config);
            var options = new ClientListStoresOptions()
                    .pageSize(1);
            var stores = fgaClient.listStores(options);
            return stores.thenApply(ClientListStoresResponse::getStatusCode);
        } catch (Exception e) {
            throw new ServerConnectionException(e);
        }
    }

    private static CompletableFuture<Integer> testOidcConnection(Oidc oidc, String apiUrl) throws ServerConnectionException {
        try {
            var accessToken = OidcUtil.getAccessToken(oidc);
            return testListStoresWithAccessToken(apiUrl, accessToken);
        } catch (Exception e) {
            throw new ServerConnectionException(e);
        }
    }

    private static CompletableFuture<Integer> testListStoresWithAccessToken(String apiUrl, String accessToken) {
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(apiUrl).resolve("/stores?page_size=1"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .timeout(java.time.Duration.of(10, SECONDS))
                .build();

        return HttpClient.newBuilder().build()
                .sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(HttpResponse::statusCode);
    }

}
