/*
 * */
package com.synectiks.process.common.security.authservice.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.security.encryption.EncryptedValue;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class LDAPConnectorConfig {
    public abstract Optional<String> systemUsername();

    public abstract EncryptedValue systemPassword();

    public abstract ImmutableList<LDAPServer> serverList();

    public abstract LDAPTransportSecurity transportSecurity();

    public abstract boolean verifyCertificates();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public static Builder create() {
            return new AutoValue_LDAPConnectorConfig.Builder()
                    .systemPassword(EncryptedValue.createUnset());
        }

        public abstract Builder systemUsername(@Nullable String systemUsername);

        public abstract Builder systemPassword(EncryptedValue systemPassword);

        public abstract Builder serverList(List<LDAPServer> serverList);

        public abstract Builder transportSecurity(LDAPTransportSecurity transportSecurity);

        public abstract Builder verifyCertificates(boolean verifyCertificates);

        public abstract LDAPConnectorConfig build();
    }

    @AutoValue
    public abstract static class LDAPServer {
        public static final String FIELD_HOSTNAME = "hostname";
        public static final String FIELD_PORT = "port";

        @JsonProperty(FIELD_HOSTNAME)
        public abstract String hostname();

        @JsonProperty(FIELD_PORT)
        public abstract int port();

        public static LDAPServer create(String hostname, int port) {
            return new AutoValue_LDAPConnectorConfig_LDAPServer(hostname, port);
        }

        public static LDAPServer fromUrl(String url) {
            final URI uri = URI.create(url);
            return create(uri.getHost(), uri.getPort());
        }

        @Override
        public String toString() {
            return hostname() + ":" + port();
        }
    }
}
