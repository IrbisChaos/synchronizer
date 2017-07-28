// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.servlet;

import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import java.util.Date;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import java.io.IOException;
import javax.servlet.ServletException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServlet;

public class DownloadQueuesServlet extends HttpServlet
{
    private static final long serialVersionUID = -2944487320707431007L;
    private final Logger log;
    private final ContractService contractService;
    private final ConnectionService connectionService;
    private final QueueInService queueInService;
    private final QueueOutService queueOutService;
    private Map<Integer, Contract> contractCache;
    private Map<Integer, Connection> connectionCache;
    
    public DownloadQueuesServlet(final ContractService contractService, final ConnectionService connectionService, final QueueInService queueInService, final QueueOutService queueOutService) {
        this.log = Logger.getLogger((Class)this.getClass());
        this.contractCache = new HashMap<Integer, Contract>();
        this.connectionCache = new HashMap<Integer, Connection>();
        this.contractService = contractService;
        this.connectionService = connectionService;
        this.queueInService = queueInService;
        this.queueOutService = queueOutService;
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/download");
        resp.setHeader("Content-disposition", "attachment;filename=synchronizer-queues.zip");
        final OutputStream outputStream = (OutputStream)resp.getOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(outputStream);
        this.addQueueInToZipFile("IN", zos);
        this.addQueueInToZipFile("OUT", zos);
        zos.close();
    }
    
    private Contract findContract(final Integer id) {
        if (id == null) {
            return null;
        }
        if (!this.contractCache.containsKey(id)) {
            this.contractCache.put(id, this.contractService.find(id));
        }
        return this.contractCache.get(id);
    }
    
    private Connection findConnection(final Integer id) {
        if (!this.connectionCache.containsKey(id)) {
            this.connectionCache.put(id, this.connectionService.find(id));
        }
        return this.connectionCache.get(id);
    }
    
    private String convertQueueInToString(final QueueIn queueIn) {
        return this.convertQueueEntityToString(queueIn.getCreateDate(), queueIn.getIssueKey(), queueIn.getContractId(), queueIn.getJsonMsg(), queueIn.getStatus(), queueIn.getMsgType(), queueIn.getMatchQueueId());
    }
    
    private String convertQueueOutToString(final QueueOut queueOut) {
        return this.convertQueueEntityToString(queueOut.getCreateDate(), queueOut.getIssueId().toString(), queueOut.getContractId(), queueOut.getJsonMsg(), queueOut.getStatus(), queueOut.getMsgType(), queueOut.getMatchQueueId());
    }
    
    public String getQueueStatus(final int level) {
        if (level > QueueStatus.values().length - 1) {
            return new Integer(level).toString();
        }
        return QueueStatus.values()[level].toString();
    }
    
    private String convertQueueEntityToString(final Date createDate, final String issue, final Integer contractId, final String message, final Integer status, final Integer type, final Integer extra) {
        String result = "";
        result += createDate;
        result += "|";
        result += issue;
        result += "|";
        final Contract contract = this.findContract(contractId);
        if (contract != null) {
            final Connection connection = this.findConnection(contract.getConnectionId());
            if (connection != null) {
                result = result + contract.getContractName() + "(" + connection.getConnectionName() + ")" + "|";
            }
            else {
                result += "|";
            }
        }
        else {
            result += "|";
        }
        result += message;
        result += "|";
        result += this.getQueueStatus(status);
        result += "|";
        result += this.getMessageType(type);
        result += "|";
        result += extra;
        return result;
    }
    
    private String getMessageType(final Integer level) {
        if (level == null) {
            return "null";
        }
        if (level > MessageType.values().length - 1) {
            return new Integer(level).toString();
        }
        return MessageType.values()[level].toString();
    }
    
    private void addQueueInToZipFile(final String type, final ZipOutputStream zos) {
        try {
            final StringBuilder sb = new StringBuilder();
            if ("IN".equals(type)) {
                final List<QueueIn> allIn = this.queueInService.getAll();
                for (final QueueIn queueIn : allIn) {
                    sb.append(this.convertQueueInToString(queueIn));
                    sb.append(System.getProperty("line.separator"));
                }
            }
            else if ("OUT".equals(type)) {
                final List<QueueOut> allOut = this.queueOutService.getAll();
                for (final QueueOut queueOut : allOut) {
                    sb.append(this.convertQueueOutToString(queueOut));
                    sb.append(System.getProperty("line.separator"));
                }
            }
            final ZipEntry zipEntry = new ZipEntry("queue" + type + ".csv");
            zos.putNextEntry(zipEntry);
            zos.write(sb.toString().getBytes());
            zos.closeEntry();
        }
        catch (IOException e) {
            this.log.error((Object)e.getMessage());
        }
    }
}
