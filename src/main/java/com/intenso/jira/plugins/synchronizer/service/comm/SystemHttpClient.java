// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.concurrent.TimeUnit;
import org.apache.http.config.SocketConfig;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import java.security.KeyStore;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.SSLContextBuilder;
import com.atlassian.jira.component.ComponentAccessor;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.Set;
import org.apache.http.impl.client.CloseableHttpClient;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;

public class SystemHttpClient
{
    private static final Integer SOCKET_TIMEOUT;
    private final ExtendedLogger logger;
    private final Map<String, CloseableHttpClient> proxiedHttpClients;
    private final CloseableHttpClient defaultHttpClient;
    private final Set<PoolingHttpClientConnectionManager> connectionManagers;
    private final IdleConnectionMonitorThread monitorThread;
    
    public SystemHttpClient() {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.proxiedHttpClients = new HashMap<String, CloseableHttpClient>();
        this.connectionManagers = (Set<PoolingHttpClientConnectionManager>)ConcurrentHashMap.newKeySet();
        this.defaultHttpClient = this.createDefaultHttpClient();
        this.logger.debug(ExtendedLoggerMessageType.COMM, "Created default HttpClient");
        (this.monitorThread = new IdleConnectionMonitorThread(this.connectionManagers)).start();
        this.logger.debug(ExtendedLoggerMessageType.COMM, "IdleConnectionMonitorThread started");
    }
    
    public CloseableHttpResponse execute(final HttpRequestBase request, final String proxy) throws IOException {
        final CloseableHttpClient client = this.getClient(proxy);
        return client.execute((HttpUriRequest)request);
    }
    
    public CloseableHttpResponse execute(final HttpRequestBase request) throws IOException {
        return this.defaultHttpClient.execute((HttpUriRequest)request);
    }
    
    public void close() {
        this.logger.debug(ExtendedLoggerMessageType.COMM, "Closing HttpClients");
        final Set<Map.Entry<String, CloseableHttpClient>> entries = this.proxiedHttpClients.entrySet();
        for (final Map.Entry<String, CloseableHttpClient> entry : entries) {
            IOUtils.closeQuietly(entry.getValue());
            this.logger.debug(ExtendedLoggerMessageType.COMM, "Closed client for proxy " + entry.getKey());
        }
        IOUtils.closeQuietly(this.defaultHttpClient);
        this.logger.debug(ExtendedLoggerMessageType.COMM, "Closed default HttpClient");
        this.monitorThread.shutdown();
        this.logger.debug(ExtendedLoggerMessageType.COMM, "IdleConnectionMonitorThread shutdown: " + this.monitorThread.isAlive());
    }
    
    private CloseableHttpClient getClient(final String proxyUrl) {
        if (StringUtils.isBlank((CharSequence)proxyUrl)) {
            return this.defaultHttpClient;
        }
        synchronized (this) {
            if (!this.proxiedHttpClients.containsKey(proxyUrl)) {
                final CloseableHttpClient proxiedHttpClient = this.createProxiedHttpClient(proxyUrl);
                this.proxiedHttpClients.put(proxyUrl, proxiedHttpClient);
                return proxiedHttpClient;
            }
            return this.proxiedHttpClients.get(proxyUrl);
        }
    }
    
    private CloseableHttpClient createProxiedHttpClient(final String proxyUrl) {
        final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(this.getProxyHost(proxyUrl));
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(this.createPoolingConManager()).setRedirectStrategy(new LaxRedirectStrategy()).setRoutePlanner(routePlanner).build();
        final String flag = ComponentAccessor.getApplicationProperties().getDefaultString("sync.ssl.validation");
        if (flag != null && flag.equals("false")) {
            try {
                final SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
                httpclient = HttpClients.custom().setConnectionManager(this.createPoolingConManager()).setRedirectStrategy(new LaxRedirectStrategy()).setRoutePlanner(routePlanner).setSSLSocketFactory(sslsf).build();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return httpclient;
    }
    
    private CloseableHttpClient createDefaultHttpClient() {
        return HttpClients.custom().setConnectionManager(this.createPoolingConManager()).setRedirectStrategy(new LaxRedirectStrategy()).build();
    }
    
    private HttpHost getProxyHost(String proxyUrl) {
        if (StringUtils.isNotBlank((CharSequence)proxyUrl)) {
            proxyUrl = proxyUrl.replace("http://", "").replace("https://", "");
            final String host = proxyUrl.substring(0, proxyUrl.lastIndexOf(":"));
            final int port = Integer.parseInt(proxyUrl.substring(proxyUrl.lastIndexOf(":") + 1));
            final HttpHost proxy = new HttpHost(host, port);
            return proxy;
        }
        return null;
    }
    
    private PoolingHttpClientConnectionManager createPoolingConManager() {
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(30);
        cm.setDefaultMaxPerRoute(5);
        cm.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(SystemHttpClient.SOCKET_TIMEOUT).build());
        this.connectionManagers.add(cm);
        return cm;
    }
    
    static {
        SOCKET_TIMEOUT = 300000;
    }
    
    public static class IdleConnectionMonitorThread extends Thread
    {
        private final ExtendedLogger logger;
        private final Set<PoolingHttpClientConnectionManager> connectionManagers;
        private volatile boolean shutdown;
        
        public IdleConnectionMonitorThread(final Set<PoolingHttpClientConnectionManager> connectionManagers) {
            this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
            this.connectionManagers = connectionManagers;
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            try {
                while (!this.shutdown) {
                    synchronized (this) {
                        this.wait(5000L);
                        for (final PoolingHttpClientConnectionManager connectionManager : this.connectionManagers) {
                            connectionManager.closeExpiredConnections();
                            connectionManager.closeIdleConnections(180L, TimeUnit.SECONDS);
                        }
                    }
                }
            }
            catch (InterruptedException ex) {
                this.shutdown();
            }
            this.logger.debug(ExtendedLoggerMessageType.COMM, "IdleConnectionMonitorThread is shut down");
        }
        
        public void shutdown() {
            this.shutdown = true;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
}
