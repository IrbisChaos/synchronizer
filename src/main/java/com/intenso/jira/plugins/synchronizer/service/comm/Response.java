// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.io.Writer;
import org.apache.commons.io.IOUtils;
import java.io.StringWriter;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.Header;

public class Response
{
    private Integer status;
    private String json;
    private Header contentType;
    private byte[] bytes;
    
    public Response(final Integer status, final String description) {
        this.status = status;
        this.json = description;
    }
    
    public Response(final CloseableHttpResponse response) {
        this.status = response.getStatusLine().getStatusCode();
        try {
            final Header contentTypeHeader = response.getFirstHeader("Content-Type");
            if (contentTypeHeader != null && contentTypeHeader.getValue().contains("application/octet-stream")) {
                this.bytes = EntityUtils.toByteArray(response.getEntity());
            }
            else {
                final StringWriter sw = new StringWriter();
                IOUtils.copy(response.getEntity().getContent(), sw);
                this.json = sw.toString();
            }
            this.contentType = response.getEntity().getContentType();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public String getJson() {
        return this.json;
    }
    
    public void setJson(final String json) {
        this.json = json;
    }
    
    public Header getContentType() {
        return this.contentType;
    }
    
    public void setContentType(final Header contentType) {
        this.contentType = contentType;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }
}
