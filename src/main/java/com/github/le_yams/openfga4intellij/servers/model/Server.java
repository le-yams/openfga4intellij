package com.github.le_yams.openfga4intellij.servers.model;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {

    private static Logger logger = Logger.getInstance(Server.class);

    private String id;
    private String name;
    private AuthenticationMethod authenticationMethod = AuthenticationMethod.NONE;

    private SharedKeysMetadata sharedKeys = new SharedKeysMetadata();
    private OAuthMetadata oauth = new OAuthMetadata();

    public Server() {
        this("new server");
    }

    public Server(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String loadUrl() {
        var credentials = getCredentials("url");
        if (credentials == null) {
            return "";
        }
        return credentials.getPasswordAsString();
    }

    public void storeUrl(String url) {
        var attributes = getCredentialAttributes("url");
        PasswordSafe.getInstance().set(attributes, new Credentials(id, url));
    }

    public List<String> loadSharedKeys() {
        var keys = new ArrayList<String>();
        for (int i = 0; i < sharedKeys.getCount(); i++) {
            var credentials = getCredentials("sharedKey_" + i);
            var value = credentials == null ? "" : credentials.getPasswordAsString();

            logger.warn("reading shared key with index " + i + ": " + value);
            keys.add(value);
        }
        return keys;
    }

    public void storeSharedKeys(List<String> keys) {
        var currentCount = sharedKeys.getCount();
        var newCount = keys.size();
        for (int i = 0; i < newCount; i++) {
            var key = keys.get(i);
            var attributes = getCredentialAttributes("sharedKey_" + i);
            PasswordSafe.getInstance().set(attributes, new Credentials(id, key));
            logger.warn("setting shared key " + attributes.getServiceName());
        }
        for (int i = newCount; i < currentCount; i++) {
            var attributes = getCredentialAttributes("sharedKey_" + i);
            PasswordSafe.getInstance().set(attributes, null);
            logger.warn("deleting shared key " + attributes.getServiceName());
        }
        sharedKeys.setCount(newCount);
    }

    public Credentials getCredentials(String keySuffix) {
        CredentialAttributes attributes = getCredentialAttributes(keySuffix);
        return PasswordSafe.getInstance().get(attributes);
    }

    @NotNull
    private CredentialAttributes getCredentialAttributes(String keySuffix) {
        var key = id + "_" + keySuffix;
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("OpenFGAServer", key));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public SharedKeysMetadata getSharedKeys() {
        return sharedKeys;
    }

    public void setSharedKeys(SharedKeysMetadata sharedKeys) {
        this.sharedKeys = sharedKeys;
    }

    public OAuthMetadata getOauth() {
        return oauth;
    }

    public void setOauth(OAuthMetadata oauth) {
        this.oauth = oauth;
    }

    @Override
    public String toString() {
        return name;
    }
}
