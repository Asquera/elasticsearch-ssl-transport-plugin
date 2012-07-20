package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.transport.netty.NettyTransport;
import org.elasticsearch.transport.netty.MessageChannelHandler;
import org.elasticsearch.common.netty.OpenChannelsHandler;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.netty.channel.ChannelPipelineFactory;
import org.elasticsearch.common.netty.channel.ChannelPipeline;
import org.elasticsearch.common.netty.channel.Channels;
import org.elasticsearch.common.netty.channel.ChannelHandler;
import org.elasticsearch.common.netty.handler.ssl.SslHandler;

import org.elasticsearch.common.netty.PipelineFactories;
import javax.net.ssl.*;
import java.security.*;

public class SslPipelineFactories extends PipelineFactories {
    private final SslContexts sslContexts;

    @Inject()
    public SslPipelineFactories(SslContexts sslContexts) {
        super();
        this.sslContexts = sslContexts;
    }

    public ChannelPipelineFactory serverPipelineFactory(final NettyTransport transport, final OpenChannelsHandler serverOpenChannels, final ESLogger logger) throws ElasticSearchException {
        return new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("openChannels", serverOpenChannels);
                pipeline.addLast("ssl", handler(sslContexts.server()));
                pipeline.addLast("dispatcher", new MessageChannelHandler(transport, logger));
                return pipeline;
            }
        };
    }

    public ChannelPipelineFactory clientPipelineFactory(final NettyTransport transport, final ESLogger logger) throws ElasticSearchException {
        return new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("ssl", handler(sslContexts.client()));
                pipeline.addLast("dispatcher", new MessageChannelHandler(transport, logger));
                return pipeline;
            }
        };
    }

    private ChannelHandler handler(SslContext context) throws ElasticSearchException {
        SslHandler handler = new SslHandler(context.engine());
        handler.setIssueHandshake(true);
        return handler;
    }

    //class ServerPipelineFactory implements ChannelPipelineFactory {
    //    private final NettyTransport transport;
    //    private final OpenChannelsHandler serverOpenChannels;
    //    private final ESLogger logger;
    //    private final SslContexts sslContexts;
    //    
    //    public ServerPipelineFactory(NettyTransport transport, SslContexts contexts, OpenChannelsHandler serverOpenChannels, ESLogger logger) {
    //        this.transport = transport;
    //        this.serverOpenChannels = serverOpenChannels;
    //        this.logger = logger;
    //        this.sslContexts = contexts;
    //    }
    //}
    //
    //class ClientPipelineFactory implements ChannelPipelineFactory {
    //    private final NettyTransport transport;
    //    private final ESLogger logger;
    //    private final SslContexts sslContexts;
    //
    //    public ClientPipelineFactory(NettyTransport transport, SslContexts contexts, ESLogger logger) {
    //        this.transport = transport;
    //        this.logger = logger;
    //        this.sslContexts = contexts;
    //    }
    //
    //    
    //}
}