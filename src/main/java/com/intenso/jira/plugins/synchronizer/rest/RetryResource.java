// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.lang.exception.ExceptionUtils;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssueDecorator;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.comm.JIRAResponse;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import java.util.Iterator;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.QueueIn;
import java.util.ArrayList;
import javax.ws.rs.POST;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueStatus;
import com.intenso.jira.plugins.synchronizer.entity.QueueOut;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.intenso.jira.plugins.synchronizer.service.comm.MessageComposerService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/retry")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class RetryResource
{
    private QueueInService queueInService;
    private QueueOutService queueOutService;
    private ContractService contractService;
    private MessageComposerService composer;
    private SynchronizedIssuesService syncIssuesService;
    private ExtendedLogger logger;
    
    public RetryResource(final QueueInService queueInService, final QueueOutService queueOutService, final ContractService contractService, final MessageComposerService composer, final SynchronizedIssuesService syncIssuesService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.queueInService = queueInService;
        this.queueOutService = queueOutService;
        this.contractService = contractService;
        this.composer = composer;
        this.syncIssuesService = syncIssuesService;
    }
    
    @POST
    @Path("/create/{id}")
    public Response create(@PathParam("id") final Integer id) {
        final QueueOut queueOut = this.queueOutService.find(id);
        this.queueOutService.update(id, QueueStatus.CANCELLED);
        this.queueOutService.create(queueOut.getConnectionId(), queueOut.getContractId(), MessageType.getByOrdinal(queueOut.getMsgType()), QueueStatus.NEW, queueOut.getJsonMsg(), queueOut.getIssueId(), queueOut.getMatchQueueId());
        return Response.ok().build();
    }
    
    @POST
    @Path("/update/{type}/{contract}")
    public Response update(@PathParam("type") final String type, @PathParam("contract") final Integer contract) {
        if (type.equals("IN")) {
            final List<QueueIn> queuein = this.queueInService.findByStatus(QueueStatus.ERROR);
            final List<QueueIn> queuein2 = new ArrayList<QueueIn>();
            QueueIn lastIn = null;
            for (final QueueIn in : queuein) {
                if (in.getContractId() == contract) {
                    queuein2.add(in);
                    if (lastIn != null && !in.getCreateDate().after(lastIn.getCreateDate())) {
                        continue;
                    }
                    lastIn = in;
                }
            }
            if (lastIn != null) {
                this.queueInService.updateAll(queuein2, QueueStatus.CANCELLED);
                this.queueInService.create(lastIn.getContractId(), lastIn.getConnectionId(), null, lastIn.getJsonMsg(), lastIn.getMatchQueueId());
            }
        }
        else {
            if (!type.equals("OUT")) {
                return Response.status(404).build();
            }
            final List<QueueOut> queueout = this.queueOutService.findByStatus(QueueStatus.ERROR);
            final List<QueueOut> queueout2 = new ArrayList<QueueOut>();
            QueueOut lastOut = null;
            for (final QueueOut out : queueout) {
                if (out.getContractId() == contract) {
                    queueout2.add(out);
                    if (lastOut != null && !out.getCreateDate().after(lastOut.getCreateDate())) {
                        continue;
                    }
                    lastOut = out;
                }
            }
            if (lastOut != null) {
                this.queueOutService.updateAll(queueout2, QueueStatus.CANCELLED);
                this.queueOutService.create(lastOut.getConnectionId(), lastOut.getContractId(), MessageType.getByOrdinal(lastOut.getMsgType()), QueueStatus.NEW, lastOut.getJsonMsg(), lastOut.getIssueId(), lastOut.getMatchQueueId());
            }
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("/hard/{id}")
    public Response hard(@PathParam("id") final Integer id) {
        final QueueOut queueOut = this.queueOutService.find(id);
        final Contract contract = this.contractService.find(queueOut.getContractId());
        final List<QueueOut> queueout = this.queueOutService.findByStatus(QueueStatus.ERROR);
        final List<QueueOut> queueout2 = new ArrayList<QueueOut>();
        for (final QueueOut out : queueout) {
            if (out.getContractId() == contract.getID()) {
                queueout2.add(out);
            }
        }
        this.queueOutService.updateAll(queueout2, QueueStatus.CANCELLED);
        final List<QueueOut> queueout3 = this.queueOutService.findByStatus(QueueStatus.ERROR_REMOTE);
        final List<QueueOut> queueout4 = new ArrayList<QueueOut>();
        for (final QueueOut out2 : queueout3) {
            if (out2.getContractId() == contract.getID()) {
                queueout4.add(out2);
            }
        }
        this.queueOutService.updateAll(queueout4, QueueStatus.CANCELLED);
        for (final QueueOut out2 : queueout4) {
            final JIRAResponse respObj = new JIRAResponse();
            respObj.setId(out2.getIssueId() + "");
            final String jsonMsg = this.composer.buildResponseInternalJSON(contract, respObj, id, 3);
            this.queueOutService.create(contract, MessageType.IN_RESPONSE, QueueStatus.NEW, jsonMsg, Long.valueOf(Long.parseLong(respObj.getId())), out2.getMatchQueueId());
        }
        try {
            final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            final IssueService.IssueResult issueResult = ComponentAccessor.getIssueService().getIssue(user, queueOut.getIssueId());
            final Issue issue = (Issue)issueResult.getIssue();
            final List<ContractChangeItem> changes = this.contractService.changes(contract, issue);
            if (changes.size() > 0) {
                final List<SyncIssue> syncIssues = this.syncIssuesService.findByContract(contract.getID(), issue.getId());
                for (final SyncIssue syncIssue : syncIssues) {
                    final String jsonMsg2 = this.composer.buildInternalJSON(contract, issue, MessageType.getByOrdinal(queueOut.getMsgType()), changes, syncIssue);
                    this.queueOutService.create(contract, MessageType.getByOrdinal(queueOut.getMsgType()), new SyncIssueDecorator(syncIssue).determineQueueOutStatus(), jsonMsg2, queueOut.getIssueId(), queueOut.getMatchQueueId());
                }
            }
        }
        catch (Exception e) {
            this.logger.error(ExtendedLoggerMessageType.REST, ExceptionUtils.getStackTrace((Throwable)e));
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("/cancel/{type}/{id}")
    public Response cancel(@PathParam("type") final String type, @PathParam("id") final Integer id) {
        if (type.equals("IN")) {
            final QueueIn queueIn = this.queueInService.find(id);
            queueIn.setStatus(QueueStatus.CANCELLED.ordinal());
            this.queueInService.update(queueIn);
        }
        else if (type.equals("OUT")) {
            this.queueOutService.update(id, QueueStatus.CANCELLED);
        }
        return Response.ok().build();
    }
}
