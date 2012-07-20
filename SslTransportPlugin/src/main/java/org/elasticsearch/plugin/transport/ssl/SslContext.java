package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import javax.net.ssl.*;
import java.security.*;

public interface SslContext {
    /**
     * Returns the SSLContext provided by this component. This can be
     * a client or a server context.
     */
    public SSLContext context();

    /**
     * Returns a completely build SSLEngine to avoid consumers building it
     * by themselves.
     */
    public SSLEngine engine();

    /**
     * Flag to determine whether this context uses client mode or not.
     */
    public boolean clientMode();

    public boolean wantClientAuth();
    
    public boolean needClientAuth();
}