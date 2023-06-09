package com.sendmail_in_bg.imagefailure;

/**
 * Created by arun on 8/4/17.
 */

import java.security.AccessController;
import java.security.Provider;

public final class JSSEProvider extends Provider {
    private static final long serialVersionUID = 1L;
    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController
                .doPrivileged(new java.security.PrivilegedAction<Void>() {
                    public Void run() {
                        put("SSLContext.TLS", "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                        put("Alg.Alias.SSLContext.TLSv1", "TLS");
                        put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                        put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                        return null;
                    }
                });
    }
}
