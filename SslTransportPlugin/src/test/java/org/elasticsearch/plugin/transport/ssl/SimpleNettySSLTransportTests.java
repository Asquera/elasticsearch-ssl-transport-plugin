/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.test.unit.transport.AbstractSimpleTransportTests;
import org.elasticsearch.transport.ConnectTransportException;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.transport.netty.*;

import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import javax.net.ssl.*;
import java.security.*;

import org.testng.annotations.Test;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

@Test
public class SimpleNettySSLTransportTests extends AbstractSimpleTransportTests {
    @Override
    protected void build() {
        try {
            NettyTransport transport1 = buildTransport("A", "ca/node1.pkcs12");
            NettyTransport transport2 = buildTransport("B", "ca/node2.pkcs12");

            serviceA = new TransportService(settingsBuilder().put("name", "A").build(), transport1, threadPool).start();
            serviceANode = new DiscoveryNode("A", serviceA.boundAddress().publishAddress());

            serviceB = new TransportService(settingsBuilder().put("name", "B").build(), transport2, threadPool).start();
            serviceBNode = new DiscoveryNode("B", serviceB.boundAddress().publishAddress());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error setting up tests.", e);
        }
    }

    private NettyTransport buildTransport(String name, String keyfile) throws Exception {
        Settings settings = settingsBuilder().put("transport.ssl.certfile", keyfile).put("transport.ssl.certpass", "elasticsearch").put("name", name).build();

        ModulesBuilder modules = new ModulesBuilder();
        modules.add(new SettingsModule(settings));
        modules.add(new KeyManagementModule(settings));
        modules.add(new TrustModule(settings));
        modules.add(new SslContextsModule(settings));
        modules.add(new SslTransportModule(settings));
        modules.add(new NettyTransportModule(settings));
        
        //modules.add(new TransportModule(settings));
        Injector injector = modules.createInjector();
        
        //NodeTrustManager trustManager = injector.getInstance(NodeTrustManager.class);
        NettyTransport transport = injector.getInstance(NettyTransport.class);

        return transport;
    }
    

    @Override
    public void testHelloWorld() {
        super.testHelloWorld();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void testVoidMessageCompressed() {
        super.testVoidMessageCompressed();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Test
    public void testConnectException() {
        try {
            serviceA.connectToNode(new DiscoveryNode("C", new InetSocketTransportAddress("localhost", 9876)));
            assert false;
        } catch (ConnectTransportException e) {
//            e.printStackTrace();
            // all is well
        }
    }
}