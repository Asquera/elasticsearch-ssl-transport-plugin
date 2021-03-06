package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import javax.net.ssl.*;
import java.security.*;

public class ClientContext extends AbstractSslContext {
    @Inject
    public ClientContext(Settings settings, KeyManagementService kms, TrustManager trustManager) throws ElasticSearchException {
        super(settings, kms, trustManager);
    }

    public SSLContext context() {
        return this.context;
    }

    public boolean clientMode() {
        return true;
    }
}