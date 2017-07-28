// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.filter;

import java.io.IOException;
import com.atlassian.jira.user.ApplicationUser;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.security.Principal;
import com.atlassian.jira.component.ComponentAccessor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import javax.servlet.Filter;

public class InternalLoginFilter extends AbstractLoginFilter implements Filter
{
    private ExtendedLogger log;
    public static final String APP_LOGIN_HEADER = "X-JIRA-SYNC-APP-CONNECTION";
    public static final String SYNCHRONIZER_CALL = "X-JIRA-SYNC-REQ";
    
    public InternalLoginFilter() {
        this.log = ExtendedLoggerFactory.getLogger(this.getClass());
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        String username = this.getUsername(request);
        Boolean synchronizerTask = Boolean.FALSE;
        if (((HttpServletRequest)request).getHeader("X-JIRA-SYNC-REQ") != null && ((HttpServletRequest)request).getHeader("X-JIRA-SYNC-REQ").equals("1")) {
            synchronizerTask = Boolean.TRUE;
        }
        if (username == null) {
            final String[] basicAuthCredentials = this.getBasicAuthCredentials((HttpServletRequest)request);
            if (basicAuthCredentials != null && basicAuthCredentials.length == 2 && basicAuthCredentials[0] != null) {
                final ApplicationUser userObj = ComponentAccessor.getUserManager().getUserByKey(basicAuthCredentials[0]);
                try {
                    this.crowdServiceAuthenticate((Principal)userObj, basicAuthCredentials[1]);
                    username = basicAuthCredentials[0];
                }
                catch (Exception e) {
                    this.log.debug(ExtendedLoggerMessageType.COMM, "syn034", e.getMessage(), basicAuthCredentials[0]);
                }
            }
        }
        if (username != null) {
            final ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);
            if (user != null) {
                this.putPrincipalInSessionContext((HttpServletRequest)request, (Principal)user);
            }
            else {
                this.log.warn(ExtendedLoggerMessageType.COMM, "syn036", username);
            }
        }
        chain.doFilter(request, response);
        if (synchronizerTask) {
            this.removePrincipalFromSessionContext((HttpServletRequest)request);
        }
    }
    
    private String getUsername(final ServletRequest request) {
        final String username = ((HttpServletRequest)request).getHeader("X-JIRA-SYNC-APP-CONNECTION");
        return username;
    }
    
    public void destroy() {
    }
}
