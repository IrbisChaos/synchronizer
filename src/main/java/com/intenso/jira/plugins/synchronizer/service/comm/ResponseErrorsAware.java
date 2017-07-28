// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.Objects;
import java.util.Map;

public class ResponseErrorsAware
{
    private final Response response;
    private final Exception exception;
    private final String url;
    private final String proxy;
    private final Map<String, String> headers;
    
    public ResponseErrorsAware(final Response response, final Exception exception, final String url, final String proxy, final Map<String, String> headers) {
        this.response = response;
        this.exception = exception;
        this.url = url;
        this.proxy = proxy;
        this.headers = headers;
    }
    
    public static ResponseErrorsAware with(final Response response) {
        Objects.requireNonNull(response);
        return new ResponseErrorsAware(response, null, null, null, null);
    }
    
    public static ResponseErrorsAware with(final Exception exception, final String url, final String proxy, final Map<String, String> headers) {
        Objects.requireNonNull(exception);
        Objects.requireNonNull(url);
        return new ResponseErrorsAware(null, exception, url, proxy, headers);
    }
    
    public Response getResponse() {
        return this.response;
    }
    
    public Exception getException() {
        return this.exception;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getProxy() {
        return this.proxy;
    }
    
    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
