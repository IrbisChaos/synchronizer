// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service.comm;

import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import java.util.List;

public interface BundleQueueService
{
    Bundle createBundle(final Integer p0, final List<QueueOut> p1, final boolean p2);
    
    Map<Integer, List<QueueOut>> splitQueueByConnection(final List<QueueOut> p0);
    
    List<IncomingLogT> saveIncomingBundle(final Integer p0, final Bundle p1, final MessageType p2);
    
    List<Long> bundleAttachments(final Bundle p0);
}
