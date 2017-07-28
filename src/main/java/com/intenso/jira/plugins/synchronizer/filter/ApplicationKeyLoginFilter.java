// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.filter;

import java.io.IOException;
import com.atlassian.jira.user.ApplicationUser;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import java.security.Principal;
import com.intenso.jira.plugins.synchronizer.utils.CryptoUtils;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.Consts;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import javax.servlet.Filter;

public class ApplicationKeyLoginFilter extends AbstractLoginFilter implements Filter
{
    ExtendedLogger log;
    public static final String APP_KEY_HEADER = "X-JIRA-SYNC-APPKEY";
    
    public ApplicationKeyLoginFilter() {
        this.log = ExtendedLoggerFactory.getLogger(this.getClass());
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void destroy() {
    }
    
    String getCredentialsCharset(final MutableHttpServletRequest request) {
        String charset = request.getParameter("http.auth.credential-charset");
        if (charset == null) {
            charset = Consts.UTF_8.name();
        }
        return charset;
    }
    
    private Header createAuthHeader(final String username, final String password, final MutableHttpServletRequest mutableRequest) {
        final Base64 base64codec = new Base64(0);
        final StringBuilder tmp = new StringBuilder();
        tmp.append(username);
        tmp.append(":");
        tmp.append((password == null) ? "null" : password);
        final byte[] bytes = EncodingUtils.getBytes(tmp.toString(), this.getCredentialsCharset(mutableRequest));
        byte[] base64password = new byte[0];
        base64password = base64codec.encode(bytes);
        final CharArrayBuffer buffer = new CharArrayBuffer(32);
        buffer.append("Authorization");
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        String username = null;
        String password = null;
        final String appKey = ((HttpServletRequest)request).getHeader("X-JIRA-SYNC-APPKEY");
        final HttpServletRequest req = (HttpServletRequest)request;
        final MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(req);
        if (appKey != null && !appKey.isEmpty()) {
            final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
            final Connection connection = connectionService.findByAppKey(appKey);
            if (connection != null) {
                username = connection.getUsername();
                password = CryptoUtils.decrypt("com.intenso.jira.synchronizer", connection.getPassword());
            }
            if (password != null && !password.isEmpty() && username != null) {
                mutableRequest.putHeader(this.createAuthHeader(username, password, mutableRequest));
                final ApplicationUser userObj = ComponentAccessor.getUserManager().getUserByName(username);
                try {
                    this.crowdServiceAuthenticate((Principal)userObj, password);
                }
                catch (Exception e) {
                    this.log.error(ExtendedLoggerMessageType.COMM, "ApplicationKeyLoginFilter Basic Authentication failed " + e.getMessage());
                }
            }
            if (username != null) {
                final ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);
                if (user != null) {
                    this.putPrincipalInSessionContext((HttpServletRequest)mutableRequest, (Principal)user);
                }
            }
        }
        chain.doFilter((ServletRequest)mutableRequest, response);
        this.removePrincipalFromSessionContext((HttpServletRequest)mutableRequest);
    }
}
