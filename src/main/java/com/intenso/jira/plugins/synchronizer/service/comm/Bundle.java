// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.Iterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.atlassian.jira.issue.attachment.Attachment;
import java.util.Map;
import java.util.List;

public class Bundle
{
    private List<String[]> messages;
    @JsonIgnore
    private Map<Integer, List<Attachment>> attachments;
    
    public Bundle() {
        this.messages = new ArrayList<String[]>();
        this.attachments = new HashMap<Integer, List<Attachment>>();
    }
    
    public List<String[]> getMessages() {
        return this.messages;
    }
    
    public void setMessages(final List<String[]> messages) {
        this.messages = messages;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final Bundle b = (Bundle)obj;
        if (this.messages == null || b.getMessages() == null) {
            return false;
        }
        if (this.messages.size() != b.getMessages().size()) {
            return false;
        }
        for (int i = 0; i < this.messages.size(); ++i) {
            final String a = this.messages.get(i)[0];
            final String aa = b.getMessages().get(i)[0];
            if (a == null || aa == null || !a.equals(aa)) {
                return false;
            }
        }
        return true;
    }
    
    public Map<Integer, List<Attachment>> getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final Map<Integer, List<Attachment>> attachments) {
        this.attachments = attachments;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Bundle{");
        sb.append("messages=[");
        if (this.messages != null) {
            for (final String[] message : this.messages) {
                sb.append(Arrays.toString(message) + ",");
            }
        }
        sb.append(']');
        sb.append('}');
        return sb.toString();
    }
}
