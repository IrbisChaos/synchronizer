// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.Objects;

public class RemoteAuthenticationKeyResult
{
    private final Boolean keyValid;
    private final Boolean connectionValid;
    private final String connectionErrorMessage;
    private final String connectionErrorStackTrace;
    
    private RemoteAuthenticationKeyResult(final boolean isKeyValid, final boolean isConnectionValid, final String connectionErrorMessage, final String connectionErrorStackTrace) {
        this.keyValid = isKeyValid;
        this.connectionValid = isConnectionValid;
        this.connectionErrorMessage = connectionErrorMessage;
        this.connectionErrorStackTrace = connectionErrorStackTrace;
    }
    
    public static RemoteAuthenticationKeyResult validKey() {
        return new RemoteAuthenticationKeyResult(true, true, null, null);
    }
    
    public static RemoteAuthenticationKeyResult invalidKey() {
        return new RemoteAuthenticationKeyResult(false, true, null, null);
    }
    
    public static RemoteAuthenticationKeyResult connectionError(final Exception exception) {
        Objects.requireNonNull(exception);
        return new RemoteAuthenticationKeyResult(false, false, exception.getMessage(), ExceptionUtils.getStackTrace((Throwable)exception));
    }
    
    public static RemoteAuthenticationKeyResult connectionError(final Exception exception, final String connectionErrorMessage) {
        Objects.requireNonNull(connectionErrorMessage);
        Objects.requireNonNull(exception);
        return new RemoteAuthenticationKeyResult(false, false, connectionErrorMessage, ExceptionUtils.getStackTrace((Throwable)exception));
    }
    
    public static RemoteAuthenticationKeyResult connectionError(final String connectionErrorMessage) {
        Objects.requireNonNull(connectionErrorMessage);
        return new RemoteAuthenticationKeyResult(false, false, connectionErrorMessage, null);
    }
    
    public static RemoteAuthenticationKeyResult connectionError(final ResponseErrorsAware response) {
        return connectionError(response.getException(), response.getException() + "; URL: " + response.getUrl() + " proxy: " + response.getProxy());
    }
    
    public Boolean getKeyValid() {
        return this.keyValid;
    }
    
    public Boolean getConnectionValid() {
        return this.connectionValid;
    }
    
    public String getConnectionErrorMessage() {
        return this.connectionErrorMessage;
    }
    
    public String getConnectionErrorStackTrace() {
        return this.connectionErrorStackTrace;
    }
}
