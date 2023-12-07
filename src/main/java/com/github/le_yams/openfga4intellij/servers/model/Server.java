package com.github.le_yams.openfga4intellij.servers.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Server {

    private String id;
    private String name;
    private String url;
    private AuthenticationMethod authenticationMethod = AuthenticationMethod.NONE;
    private String apiToken;
    private Oidc oidc = Oidc.EMPTY;

    public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        if (authenticationMethod == null) {
            authenticationMethod = AuthenticationMethod.NONE;
        }
        this.authenticationMethod = authenticationMethod;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Server that = (Server) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
