// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import javax.ws.rs.GET;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import java.util.Date;
import java.util.Calendar;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/healthBoard")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class HealthBoardResource
{
    @GET
    public Response get() {
        final Map<String, Object> result = new HashMap<String, Object>();
        int status = 2;
        final Map<String, Integer> map = new HashMap<String, Integer>();
        final QueueOutService queueOutService = (QueueOutService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueOutService.class);
        final List<QueueOut> queueOuts = queueOutService.getAll();
        for (final QueueOut queueOut : queueOuts) {
            if (queueOut.getStatus() == 2) {
                final Calendar cal = Calendar.getInstance();
                cal.setTime(queueOut.getUpdateDate());
                cal.add(12, 5);
                if (!cal.getTime().before(new Date())) {
                    continue;
                }
                final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
                final Connection connection = connectionService.get(queueOut.getConnectionId());
                connection.getConnectionName();
                final ContractService contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
                final Contract contract = contractService.get(queueOut.getContractId());
                contract.getContractName();
                final String name = connection.getConnectionName() + ": " + contract.getContractName();
                Integer count = map.get(name);
                if (count == null) {
                    count = 0;
                }
                map.put(name, count + 1);
            }
        }
        if (!map.isEmpty()) {
            status = 0;
        }
        result.put("queueOutsSizeException", queueOuts.size());
        result.put("status", status);
        result.put("notProcessed", map);
        return Response.ok((Object)result).build();
    }
}
