// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.filter;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Enumeration;
import org.apache.http.Header;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import javax.servlet.http.HttpServletRequestWrapper;

final class MutableHttpServletRequest extends HttpServletRequestWrapper
{
    private final Map<String, String> customHeaders;
    
    public MutableHttpServletRequest(final HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<String, String>();
    }
    
    public void putHeader(final Header header) {
        this.customHeaders.put(header.getName(), header.getValue());
    }
    
    public void putHeader(final String name, final String value) {
        this.customHeaders.put(name, value);
    }
    
    public String getHeader(final String name) {
        final String headerValue = this.customHeaders.get(name);
        if (headerValue != null) {
            return headerValue;
        }
        return ((HttpServletRequest)this.getRequest()).getHeader(name);
    }
    
    public Enumeration<String> getHeaderNames() {
        final Set<String> set = new HashSet<String>(this.customHeaders.keySet());
        final Enumeration<String> e = (Enumeration<String>)((HttpServletRequest)this.getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            final String n = e.nextElement();
            set.add(n);
        }
        return Collections.enumeration(set);
    }
}
