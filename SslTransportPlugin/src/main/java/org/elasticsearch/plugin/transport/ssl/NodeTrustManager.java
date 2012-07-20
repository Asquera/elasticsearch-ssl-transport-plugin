package org.elasticsearch.plugin.transport.ssl;

import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;
import java.util.Enumeration;

public class NodeTrustManager extends AbstractComponent implements X509TrustManager {
    private final X509Certificate ours;

    @Inject
    public NodeTrustManager(Settings settings, KeyManagementService km) {
        super(settings);

        try {
            KeyStore ks = km.keyStore();
            Enumeration<String> e = ks.aliases();
            String alias = e.nextElement();

            if (e.hasMoreElements()) {
                throw new RuntimeException(
                  "More than one certificates in one keychain are currently not supported"
                );
            }

            this.ours = (X509Certificate)ks.getCertificate(alias);
            if (ours == null) {
                throw new RuntimeException("Unable to load certificate");
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException("Unable to load certificate", e);
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    /**
     * We don't trust certificates that are not by the same issuer or try to
     * pose as ourselves.
     */
    public void checkClientTrusted(
            X509Certificate[] chain, String authType) throws CertificateException {
        checkCertificates(chain, authType);
    }

    public void checkServerTrusted(
            X509Certificate[] chain, String authType) throws CertificateException {
        checkCertificates(chain, authType);
    }

    private void checkCertificates(
              X509Certificate[] chain, String authType) throws CertificateException {

        if (chain[0] == null) {
            throw new CertificateException("Refusing to connect: No certificate provided by client.");
        }

        if (!chain[0].getIssuerX500Principal().equals(ours.getIssuerX500Principal())) {
            throw new CertificateException("Refusing to connect: client certificate has different issuer");
        }

        if (chain[0].getSubjectX500Principal().equals(ours.getSubjectX500Principal())) {
            throw new CertificateException("Refusing to connect: client certificate it the same as the server certificate");
        }         
    }
}