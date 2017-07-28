// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.filter;

import org.apache.commons.codec.binary.Base64;
import com.atlassian.crowd.exception.FailedAuthenticationException;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.crowd.embedded.api.CrowdService;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.Filter;

public abstract class AbstractLoginFilter implements Filter
{
    protected void putPrincipalInSessionContext(final HttpServletRequest httpServletRequest, final Principal principal) {
        HttpSession httpSession;
        try {
            httpSession = httpServletRequest.getSession();
        }
        catch (Exception e) {
            httpSession = httpServletRequest.getSession(false);
        }
        if (httpSession != null) {
            httpSession.setAttribute("seraph_defaultauthenticator_user", (Object)principal);
            httpSession.setAttribute("seraph_defaultauthenticator_logged_out_user", (Object)null);
        }
    }
    
    protected void removePrincipalFromSessionContext(final HttpServletRequest httpServletRequest) {
        HttpSession httpSession;
        try {
            httpSession = httpServletRequest.getSession();
        }
        catch (Exception e) {
            httpSession = httpServletRequest.getSession(false);
        }
        if (httpSession != null) {
            httpSession.setAttribute("seraph_defaultauthenticator_user", (Object)null);
            httpSession.setAttribute("seraph_defaultauthenticator_logged_out_user", (Object)Boolean.TRUE);
        }
    }
    
    protected void crowdServiceAuthenticate(final Principal user, final String password) throws FailedAuthenticationException {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader origCCL = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.getClass().getClassLoader());
            ((CrowdService)ComponentAccessor.getComponent((Class)CrowdService.class)).authenticate(user.getName(), password);
        }
        finally {
            currentThread.setContextClassLoader(origCCL);
        }
    }
    
    protected String[] getBasicAuthCredentials(final HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.substring(0, 5).equalsIgnoreCase("Basic")) {
            final String base64Token = authHeader.substring(6);
            final String token = new String(Base64.decodeBase64(base64Token.getBytes()));
            final int delim = token.indexOf(":");
            if (delim != -1) {
                final String name = token.substring(0, delim);
                final String password = token.substring(delim + 1);
                return new String[] { name, password };
            }
        }
        return null;
    }
}
