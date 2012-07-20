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

import org.elasticsearch.common.component.AbstractComponent;
import com.google.common.collect.ImmutableList;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.inject.Modules;
import org.elasticsearch.common.inject.SpawnModules;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.ElasticSearchException;

import java.io.FileInputStream;
import javax.net.ssl.*;
import java.security.*;
/**
 *
 */
public class KeyManagementService extends AbstractComponent {
    private final String algorithm = "SunX509";
    private KeyStore keyStore;
    private KeyManagerFactory keyManagerFactory;

    @Inject
    public KeyManagementService(Settings settings) {
        super(settings);
        String certfile = settings.get("transport.ssl.certfile", "config/" + nodeName() + ".pc12");
        String certpass = settings.get("transport.ssl.certpass", "");
        init(certfile, certpass);
    }

    protected void init(String certfile, String certpass) throws ElasticSearchException {
      FileInputStream fs = null;
      try {
          this.keyStore = KeyStore.getInstance("PKCS12");

          fs = new FileInputStream(certfile);
          keyStore.load(fs, certpass.toCharArray());
          fs.close();

          this.keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
          keyManagerFactory.init(keyStore, certpass.toCharArray());
      } catch (Exception e) {
          throw new ElasticSearchException("Error initializing Keystore", e);
      }
    }

    public KeyStore keyStore() {
        return keyStore;
    }

    public KeyManagerFactory keyManagerFactory() {
        return keyManagerFactory;
    }
}