// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.historytab;

import java.util.Iterator;
import java.util.List;
import com.atlassian.jira.util.SimpleErrorCollection;
import java.util.Comparator;
import java.util.Collections;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import org.boon.Boon;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutResponseDTO;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.HashMap;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import java.util.Date;
import java.util.Calendar;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import java.sql.Timestamp;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;

public class HistoryTabAction extends AbstractIssueAction
{
    private Timestamp timePerformed;
    private IssueTabPanelModuleDescriptor descriptor;
    private Issue issue;
    private PluginLicenseManager pluginLicenseManager;
    private QueueInService queueInService;
    private QueueOutService queueOutService;
    private ContractService contractService;
    private Map<Integer, String> msgTypeMap;
    private Map<Integer, String> statusMap;
    
    public HistoryTabAction(final IssueTabPanelModuleDescriptor descriptor, final Issue issue, final PluginLicenseManager pluginLicenseManager, final QueueInService queueInService, final QueueOutService queueOutService, final ContractService contractService) {
        super(descriptor);
        this.msgTypeMap = (Map<Integer, String>)ImmutableMap.builder().put((Object)0, (Object)"Response").put((Object)1, (Object)"Create").put((Object)2, (Object)"Update").put((Object)3, (Object)"Comment").put((Object)4, (Object)"Response Comment").put((Object)5, (Object)"Attachment").put((Object)6, (Object)"Workflow").put((Object)7, (Object)"IN Response").put((Object)8, (Object)"Delete").put((Object)9, (Object)"Worklog").build();
        this.statusMap = (Map<Integer, String>)ImmutableMap.builder().put((Object)0, (Object)"aui-lozenge-complete").put((Object)1, (Object)"aui-lozenge-complete").put((Object)2, (Object)"aui-lozenge-current").put((Object)3, (Object)"").put((Object)4, (Object)"aui-lozenge-moved").put((Object)5, (Object)"aui-lozenge-success").put((Object)6, (Object)"aui-lozenge-error").put((Object)7, (Object)"aui-lozenge-error").build();
        this.descriptor = descriptor;
        this.issue = issue;
        this.timePerformed = new Timestamp(Calendar.getInstance().getTimeInMillis());
        this.pluginLicenseManager = pluginLicenseManager;
        this.queueInService = queueInService;
        this.queueOutService = queueOutService;
        this.contractService = contractService;
    }
    
    public Date getTimePerformed() {
        return this.timePerformed;
    }
    
    protected void populateVelocityParams(final Map params) {
        final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
        if (errorCollection.hasAnyErrors()) {
            params.put("licErrors", errorCollection);
        }
        if (this.issue != null) {
            params.put("issueId", this.issue.getId());
            final List<HistoryTabRecord> history = new ArrayList<HistoryTabRecord>();
            final Map<Integer, Contract> contractsMap = new HashMap<Integer, Contract>();
            final List<QueueIn> ins = this.queueInService.findByIssue(this.issue);
            for (final QueueIn in : ins) {
                final HistoryTabRecord row = new HistoryTabRecord();
                row.setCreateDate(in.getCreateDate().toString());
                row.setQueueType("IN");
                row.setArrowDirection("left");
                Contract contract;
                if (!contractsMap.containsKey(in.getContractId())) {
                    contract = this.contractService.find(in.getContractId());
                    contractsMap.put(in.getContractId(), contract);
                }
                else {
                    contract = contractsMap.get(in.getContractId());
                }
                String contractName = "";
                if (contract != null) {
                    contractName = contract.getContractName();
                }
                row.setContractName(contractName);
                final SimpleDateFormat parser = new SimpleDateFormat("dd/MMM/yy hh:mm aaa", Locale.US);
                row.setCreateDateFormatted(parser.format(in.getCreateDate()));
                row.setMessageType(this.msgTypeMap.get(in.getMsgType()));
                row.setQueueStatus(QueueStatus.values()[in.getStatus()].toString().replace("_", " "));
                row.setQueueStatusLozenge(this.statusMap.get(in.getStatus()));
                row.setQueueId("" + in.getID());
                row.setContractId("" + in.getContractId());
                if (in.getMsgType().equals(MessageType.RESPONSE.ordinal()) || in.getMsgType().equals(MessageType.RESPONSE_COMMENT.ordinal())) {
                    final QueueOutResponseDTO response = Boon.fromJson(in.getJsonMsg(), QueueOutResponseDTO.class);
                    row.setResponseState(response.getResponseState());
                }
                history.add(row);
            }
            final List<QueueOut> outs = this.queueOutService.findByIssue(this.issue);
            for (final QueueOut out : outs) {
                final HistoryTabRecord row2 = new HistoryTabRecord();
                row2.setCreateDate(out.getCreateDate().toString());
                row2.setQueueType("OUT");
                row2.setArrowDirection("right");
                Contract contract2;
                if (!contractsMap.containsKey(out.getContractId())) {
                    contract2 = this.contractService.find(out.getContractId());
                    contractsMap.put(out.getContractId(), contract2);
                }
                else {
                    contract2 = contractsMap.get(out.getContractId());
                }
                String contractName2 = "";
                if (contract2 != null) {
                    contractName2 = contract2.getContractName();
                }
                row2.setContractName(contractName2);
                final SimpleDateFormat parser2 = new SimpleDateFormat("dd/MMM/yy hh:mm aaa", Locale.US);
                row2.setCreateDateFormatted(parser2.format(out.getCreateDate()));
                row2.setMessageType(this.msgTypeMap.get(out.getMsgType()));
                row2.setQueueStatus(QueueStatus.values()[out.getStatus()].toString().replace("_", " "));
                row2.setQueueStatusLozenge(this.statusMap.get(out.getStatus()));
                row2.setQueueId("" + out.getID());
                row2.setContractId("" + out.getContractId());
                history.add(row2);
            }
            Collections.sort(history, new CustomComparator());
            params.put("history", history);
            final Map<String, Boolean> contracts = new HashMap<String, Boolean>();
            final Iterator<Contract> iterator3 = this.contractService.getAll().iterator();
            while (iterator3.hasNext()) {
                final Contract contract = iterator3.next();
                contracts.put("IN" + contract.getID(), true);
                contracts.put("OUT" + contract.getID(), true);
            }
            params.put("contracts", contracts);
        }
    }
    
    public IssueTabPanelModuleDescriptor getDescriptor() {
        return this.descriptor;
    }
    
    public void setDescriptor(final IssueTabPanelModuleDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public Issue getIssue() {
        return this.issue;
    }
    
    public void setIssue(final Issue issue) {
        this.issue = issue;
    }
    
    public void setTimePerformed(final Timestamp timePerformed) {
        this.timePerformed = timePerformed;
    }
    
    public QueueInService getQueueInService() {
        return this.queueInService;
    }
    
    public void setQueueInService(final QueueInService queueInService) {
        this.queueInService = queueInService;
    }
    
    public QueueOutService getQueueOutService() {
        return this.queueOutService;
    }
    
    public void setQueueOutService(final QueueOutService queueOutService) {
        this.queueOutService = queueOutService;
    }
    
    public class CustomComparator implements Comparator<HistoryTabRecord>
    {
        @Override
        public int compare(final HistoryTabRecord o1, final HistoryTabRecord o2) {
            return o2.getCreateDate().compareTo(o1.getCreateDate());
        }
    }
}
