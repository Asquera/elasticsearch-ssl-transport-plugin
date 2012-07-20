package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;
import javax.net.ssl.*;
import java.security.*;

public abstract class AbstractSslContext extends AbstractComponent implements SslContext {
    protected SSLContext context;
    
    public AbstractSslContext(Settings settings, KeyManagementService kms, TrustManager trustManager) {
        super(settings);

        try {
            KeyManagerFactory kmf = kms.keyManagerFactory();
            final TrustManager[] trustManagers = new TrustManager[] { trustManager };
            
            this.context = SSLContext.getInstance("TLS");
            this.context.init(kmf.getKeyManagers(), trustManagers, null);
        } catch (Exception e) {
            throw new ElasticSearchException("Error initializing SSL context.", e);
        }
    }
    
    public SSLEngine engine() throws ElasticSearchException {
        try {
            SSLEngine engine = context().createSSLEngine();
            engine.setUseClientMode(clientMode());
            engine.setWantClientAuth(wantClientAuth());
            engine.setNeedClientAuth(needClientAuth());
            return engine;
        } catch (Exception e) {
            throw new ElasticSearchException("Error initializing SSL context.", e);
        }
    }

    public SSLContext context() {
        return this.context;
    }

    public boolean wantClientAuth() {
        return true; /* at the moment, we are secure by force */
    }

    public boolean needClientAuth() {
        return true; /* at the moment, we are secure by force */
    }
}