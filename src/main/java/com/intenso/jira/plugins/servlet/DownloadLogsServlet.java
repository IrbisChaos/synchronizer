// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.servlet;

import com.intenso.jira.plugins.synchronizer.entity.QueueLogLevel;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.QueueLog;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueLogService;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import java.util.zip.ZipEntry;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
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
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServlet;

public class DownloadLogsServlet extends HttpServlet
{
    private static final long serialVersionUID = -2944487320707431007L;
    private static final String UTF_8 = "UTF-8";
    private final Logger log;
    private final ContractService contractService;
    private final ConnectionService connectionService;
    private Map<Integer, Contract> contractCache;
    private Map<Integer, Connection> connectionCache;
    
    public DownloadLogsServlet(final ContractService contractService, final ConnectionService connectionService) {
        this.log = Logger.getLogger((Class)this.getClass());
        this.contractCache = new HashMap<Integer, Contract>();
        this.connectionCache = new HashMap<Integer, Connection>();
        this.contractService = contractService;
        this.connectionService = connectionService;
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/download");
            resp.setHeader("Content-disposition", "attachment;filename=synchronizer-logs.zip");
            final OutputStream outputStream = (OutputStream)resp.getOutputStream();
            final ZipOutputStream zos = new ZipOutputStream(outputStream);
            this.addQueueLogsToZipFile("queueLogs.csv", zos);
            this.addPluginLogsToZipFile("intenso-synchronizer.log", zos);
            for (int i = 1; i <= 10; ++i) {
                this.addPluginLogsToZipFile("intenso-synchronizer.log." + i, zos);
            }
            zos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addPluginLogsToZipFile(final String fileName, final ZipOutputStream zos) {
        try {
            final String home = ((JiraHome)ComponentAccessor.getComponentOfType((Class)JiraHome.class)).getLocalHomePath();
            final File file = new File(home + File.separator + "log" + File.separator + fileName);
            if (!file.exists()) {
                return;
            }
            final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            final ZipEntry zipEntry = new ZipEntry(fileName.replace(".", "-") + ".csv");
            zos.putNextEntry(zipEntry);
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                zos.write((ExtendedLoggerFactory.decorateLine(line) + System.lineSeparator()).getBytes("UTF-8"));
            }
            zos.closeEntry();
            br.close();
        }
        catch (IOException e) {
            this.log.error((Object)e.getMessage());
        }
        catch (Exception e2) {
            this.log.error((Object)e2.getMessage());
        }
    }
    
    private void addQueueLogsToZipFile(final String fileName, final ZipOutputStream zos) {
        try {
            final QueueLogService queueLogService = (QueueLogService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueLogService.class);
            final List<QueueLog> all = queueLogService.getAll();
            final StringBuilder sb = new StringBuilder();
            for (final QueueLog queueLog : all) {
                try {
                    sb.append(this.convertLogsToString(queueLog));
                    sb.append(System.getProperty("line.separator"));
                }
                catch (Exception e) {
                    this.log.error((Object)e.getMessage());
                }
            }
            final ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            zos.write(sb.toString().getBytes());
            zos.closeEntry();
        }
        catch (IOException e2) {
            this.log.error((Object)e2.getMessage());
        }
        catch (Exception e3) {
            this.log.error((Object)e3.getMessage());
        }
    }
    
    private Contract findContract(final Integer id) {
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
    
    private String convertLogsToString(final QueueLog queueLog) {
        String result = "";
        result += ((queueLog.getCreateDate() != null) ? queueLog.getCreateDate().toString() : "|");
        result += ((queueLog.getIssueKey() != null) ? queueLog.getIssueKey() : "|");
        final Contract contract = this.findContract(queueLog.getContractId());
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
        result += ((queueLog.getResponseStatus() != null) ? queueLog.getResponseStatus() : "|");
        result += ((queueLog.getLogMessage() != null) ? queueLog.getLogMessage() : "|");
        result = result + this.getLogLevel((queueLog.getLogLevel() != null) ? ((int)queueLog.getLogLevel()) : -1) + "|";
        result += this.getQueueType(queueLog.getQueueType());
        return result;
    }
    
    private String getQueueType(final Integer queueType) {
        switch (queueType) {
            case 0: {
                return "IN";
            }
            case 1: {
                return "OUT";
            }
            default: {
                return "?";
            }
        }
    }
    
    public String getLogLevel(final int level) {
        if (level > QueueLogLevel.values().length - 1) {
            return new Integer(level).toString();
        }
        return QueueLogLevel.values()[level].toString();
    }
}
