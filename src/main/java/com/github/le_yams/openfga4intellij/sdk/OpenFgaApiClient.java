package com.github.le_yams.openfga4intellij.sdk;

import com.github.le_yams.openfga4intellij.servers.model.Server;
import com.github.le_yams.openfga4intellij.servers.util.ServersUtil;
import dev.openfga.sdk.api.model.Store;
import dev.openfga.sdk.errors.FgaInvalidParameterException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OpenFgaApiClient {

    CompletableFuture<List<Store>> listStores();

    static OpenFgaApiClient ForServer(Server server) {
        try {
            var openFgaClient = ServersUtil.createClient(server);
            return new SdkClient(openFgaClient);
        } catch (FgaInvalidParameterException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
