// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import java.util.Map;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.AbstractHttpEntity;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import javax.servlet.http.HttpServletRequest;
import com.intenso.jira.plugins.synchronizer.entity.Connection;

public interface CommunicatorService
{
    Response send(final Connection p0, final Bundle p1, final boolean p2, final boolean p3, final boolean p4);
    
    Integer recognizeConnection(final HttpServletRequest p0);
    
    MessageType getMessageType(final HttpServletRequest p0);
    
    Response callInternalRest(final Connection p0, final CommunicatorServiceImpl.HttpRequestMethod p1, final String p2, final String p3);
    
    Response callInternalRest(final Connection p0, final CommunicatorServiceImpl.HttpRequestMethod p1, final String p2);
    
    Response callInternalRestAttachments(final Connection p0, final String p1, final List<AttachmentDTO> p2, final boolean p3) throws FileNotFoundException, IOException;
    
    Response callInternalRest(final String p0, final String p1, final CommunicatorServiceImpl.HttpRequestMethod p2, final String p3);
    
    Response callExternalRest(final String p0, final String p1, final CommunicatorServiceImpl.HttpRequestMethod p2, final String p3);
    
    Response callExternalRest(final String p0, final String p1, final String p2, final CommunicatorServiceImpl.HttpRequestMethod p3, final AbstractHttpEntity p4);
    
    Response callExternalRest(final String p0, final String p1, final String p2, final CommunicatorServiceImpl.HttpRequestMethod p3, final UrlEncodedFormEntity p4, final Map<String, String> p5);
    
    ResponseErrorsAware callExternalRest(final String p0, final String p1, final String p2, final CommunicatorServiceImpl.HttpRequestMethod p3, final Map<String, String> p4);
    
    Response callExternalRest(final String p0, final String p1, final String p2, final CommunicatorServiceImpl.HttpRequestMethod p3, final Map<String, String> p4, final String p5);
}
