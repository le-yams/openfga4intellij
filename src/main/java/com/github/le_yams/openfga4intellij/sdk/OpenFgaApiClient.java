package com.github.le_yams.openfga4intellij.sdk;

import com.github.le_yams.openfga4intellij.servers.model.AuthenticationMethod;
import com.github.le_yams.openfga4intellij.servers.model.Server;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ApiToken;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.Credentials;
import dev.openfga.sdk.api.model.Store;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OpenFgaApiClient {

    CompletableFuture<List<Store>> listStores();

    static OpenFgaApiClient ForServer(Server server) {
        if (server.getAuthenticationMethod() == AuthenticationMethod.OIDC) {
            throw new UnsupportedOperationException("oidc client not supported yet");
        }

        var apiUrl = server.getUrl();
        var configuration = new ClientConfiguration().apiUrl(apiUrl);

        var apiToken = server.getApiToken();
        if (apiToken != null) {
            configuration.credentials(new Credentials(new ApiToken(apiToken)));
        }
        try {
            var openFgaClient = new OpenFgaClient(configuration);
            return new SdkClient(openFgaClient);
        } catch (FgaInvalidParameterException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
