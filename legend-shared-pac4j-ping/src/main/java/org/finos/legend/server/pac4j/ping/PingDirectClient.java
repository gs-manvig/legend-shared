/*
 *  Copyright 2021 Goldman Sachs
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.finos.legend.server.pac4j.ping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import org.finos.legend.server.pac4j.SerializableProfile;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.http.client.direct.DirectBearerAuthClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.authenticator.UserInfoOidcAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@SerializableProfile
public class PingDirectClient extends DirectBearerAuthClient
{
    private static final Logger logger = LoggerFactory.getLogger(PingDirectClient.class);

    @JsonProperty
    private String clientId;

    @JsonProperty
    private String secret;

    @JsonProperty
    private String discoveryUri;

    @JsonProperty
    private String scope;

    @JsonProperty
    private String proxyHost;

    @JsonProperty
    private int proxyPort;

    @JsonProperty
    private Map<String, String> customParams;

    @JsonProperty
    private String name;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    protected void clientInit()
    {
        OidcConfiguration config = new OidcConfiguration();
        config.setClientId(clientId);
        config.setScope(secret);
        config.setDiscoveryURI(discoveryUri);
        config.setCustomParams(customParams);

        DefaultResourceRetriever resourceRetriever =
                new DefaultResourceRetriever(config.getConnectTimeout(), config.getReadTimeout());
        if (proxyHost != null && !"".equals(proxyHost))
        {
            logger.info("Using proxy {}:{}", proxyHost, proxyPort);
            resourceRetriever.setProxy(
                    new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        }
        config.setResourceRetriever(resourceRetriever);

        if (scope == null || "".equals(scope))
        {
            scope = "openid profile";
        }
        config.setScope(scope);

        setAuthenticator(new UserInfoOidcAuthenticator(config));
        setProfileCreator(new AuthenticatorProfileCreator<>());
        super.clientInit();
    }
}
