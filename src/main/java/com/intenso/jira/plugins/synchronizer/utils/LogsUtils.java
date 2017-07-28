// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.rest.model.IncomingLogT;
import java.util.List;

public class LogsUtils
{
    public static boolean logHasError(final List<IncomingLogT> lst) {
        for (final IncomingLogT ilt : lst) {
            if ((ilt.getError() == null || ilt.getError().isEmpty()) && ilt.getQueueIn() == null) {
                return true;
            }
        }
        return false;
    }
}
