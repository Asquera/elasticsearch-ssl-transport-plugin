package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.common.inject.Inject;

public class SslContexts {
    SslContext serverContext;
    SslContext clientContext;

    @Inject
    public SslContexts(ServerContext serverContext,
                       ClientContext clientContext) {
        this.serverContext = serverContext;
        this.clientContext = clientContext;
    }

    public SslContext server() {
        return this.serverContext;
    }

    public SslContext client() {
        return this.clientContext;
    }
}