package com.github.le_yams.openfga4intellij.servers;

import com.github.le_yams.openfga4intellij.servers.model.Server;
import com.intellij.openapi.diagnostic.Logger;
import dev.openfga.sdk.api.client.ClientListStoresResponse;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.*;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

import java.util.concurrent.CompletableFuture;

public class ServersUtil {

    private static final Logger logger = Logger.getInstance(ServersUtil.class);

    public static CompletableFuture<Integer> testConnection(Server server) throws FgaInvalidParameterException {
        var apiUrl = server.loadUrl();
        logger.warn("testing connection to " + apiUrl + " with authentication method: " + server.getAuthenticationMethod());

        var config = new ClientConfiguration().apiUrl(apiUrl);
        switch (server.getAuthenticationMethod()) {
            case API_TOKEN -> {
                var token = server.loadApiToken();
                config = config.credentials(new Credentials(
                        new ApiToken(token)
                ));
            }
            case OIDC -> {
                var oidc = server.loadOidc();
                config = config.credentials(new Credentials(
                        new ClientCredentials()
                                .apiTokenIssuer(oidc.issuer())
                                .apiAudience(oidc.audience())
                                .clientId(oidc.clientId())
                                .clientSecret(oidc.clientSecret())
                ));
            }
        }

        var fgaClient = new OpenFgaClient(config);

        var options = new ClientListStoresOptions()
                .pageSize(1);

        var stores = fgaClient.listStores(options);

        return stores.thenApply(ClientListStoresResponse::getStatusCode);
    }
}
