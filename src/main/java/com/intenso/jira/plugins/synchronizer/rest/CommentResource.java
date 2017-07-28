// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import com.intenso.jira.plugins.synchronizer.entity.CommentType;
import javax.ws.rs.POST;
import com.atlassian.jira.util.SimpleErrorCollection;
import java.net.URI;
import com.atlassian.jira.user.ApplicationUser;
import java.util.Arrays;
import com.intenso.jira.plugins.synchronizer.utils.LicenseUtils;
import java.net.MalformedURLException;
import com.atlassian.jira.component.ComponentAccessor;
import javax.ws.rs.DELETE;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import javax.ws.rs.GET;
import java.util.Iterator;
import java.util.Map;
import com.atlassian.jira.issue.Issue;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.rest.model.ContractT;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.atlassian.jira.avatar.AvatarService;
import com.intenso.jira.plugins.synchronizer.service.NotificationService;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicationService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.CommentService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.security.PermissionManager;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/comment")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class CommentResource
{
    private ExtendedLogger logger;
    private PermissionManager permissionManager;
    private IssueManager issueManager;
    private UserManager userManager;
    private CommentService commentService;
    private ContractService contractService;
    private CommunicationService communicationService;
    private SynchronizedIssuesService syncIssuesService;
    private PluginLicenseManager pluginLicenseManager;
    private NotificationService notificationService;
    private AvatarService avatarService;
    
    public CommentResource(final CommentService commentService, final IssueManager issueManager, final PermissionManager permissionManager, final UserManager userManager, final CommunicationService communicationService, final ContractService contractService, final SynchronizedIssuesService syncIssuesService, final PluginLicenseManager pluginLicenseManager, final NotificationService notificationService, final AvatarService avatarService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.setCommentService(commentService);
        this.setIssueManager(issueManager);
        this.setPermissionManager(permissionManager);
        this.userManager = userManager;
        this.communicationService = communicationService;
        this.contractService = contractService;
        this.syncIssuesService = syncIssuesService;
        this.pluginLicenseManager = pluginLicenseManager;
        this.setNotificationService(notificationService);
        this.avatarService = avatarService;
    }
    
    public UserManager getUserManager() {
        return this.userManager;
    }
    
    public void setUserManager(final UserManager userManager) {
        this.userManager = userManager;
    }
    
    @GET
    @Path("/{issueId}")
    public Response listCommentByIssue(@PathParam("issueId") final Long issueId) {
        if (issueId != null) {
            final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
            final Map<String, Object> result = new HashMap<String, Object>();
            final List<Contract> contracts = this.contractService.findByContextAndStatusAndComments(issue.getProjectObject().getId(), issue.getIssueTypeObject().getId(), ContractStatus.ENABLED, 1);
            final Map<Integer, ContractT> contractMap = new HashMap<Integer, ContractT>();
            for (final Contract ct : contracts) {
                final ContractT c = new ContractT(ct);
                String remoteIssueId = "";
                final List<SyncIssue> issues = (ct != null) ? this.syncIssuesService.findByContract(ct.getID(), issueId) : new ArrayList<SyncIssue>();
                if (issues != null && issues.size() > 0) {
                    remoteIssueId = issues.get(0).getRemoteIssueKey();
                }
                c.setDisplayContractName(((remoteIssueId == null) ? "" : remoteIssueId) + c.getDisplayContractName());
                contractMap.put(ct.getID(), c);
            }
            final ContractT undefined = new ContractT();
            undefined.setContractName("Contract not defined");
            undefined.setDisplayContractName(undefined.getContractName());
            contractMap.put(-1, undefined);
            result.put("contracts", contractMap);
            final List<Comment> list = this.commentService.findExternalByIssue(issueId);
            final Map<Integer, List<CommentT>> comments = new HashMap<Integer, List<CommentT>>();
            for (final Contract contract : contracts) {
                for (final Comment c2 : list) {
                    if (c2.getContractId() == null || !contractMap.containsKey(c2.getContractId())) {
                        if (comments.get(-1) == null) {
                            comments.put(-1, new ArrayList<CommentT>());
                        }
                        comments.get(-1).add(new CommentT(c2, this.isAbleToRemoveComment(issueId, c2)));
                    }
                    else {
                        if (!c2.getContractId().equals(contract.getID())) {
                            continue;
                        }
                        if (comments.get(c2.getContractId()) == null) {
                            comments.put(c2.getContractId(), new ArrayList<CommentT>());
                        }
                        comments.get(c2.getContractId()).add(new CommentT(c2, this.isAbleToRemoveComment(issueId, c2)));
                    }
                }
            }
            result.put("comments", comments);
            return Response.ok((Object)result).build();
        }
        return Response.serverError().status(500).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteComment(@PathParam("id") final Integer id) {
        if (id == null) {
            this.logger.warn(ExtendedLoggerMessageType.REST, "CommentResource.deleteComment  id is null");
            return Response.serverError().status(404).build();
        }
        final Comment comment = this.commentService.get(id);
        if (comment == null) {
            this.logger.warn(ExtendedLoggerMessageType.REST, "CommentResource.deleteComment  comment is null");
            return Response.serverError().status(404).build();
        }
        this.commentService.delete(comment);
        return Response.ok().build();
    }
    
    @POST
    public Response saveComment(final CommentT comment) {
        if (this.isAbleToComment(comment.getIssueId())) {
            final ApplicationUser au = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            List<Contract> contracts = new ArrayList<Contract>();
            if (comment.getContractId() != null) {
                final Contract contract = this.contractService.get(comment.getContractId());
                if (contract != null) {
                    contracts.add(contract);
                }
            }
            else {
                final Issue issue = (Issue)this.issueManager.getIssueObject(comment.getIssueId());
                if (issue != null) {
                    contracts = this.contractService.findContracts(issue.getProjectObject(), issue.getIssueTypeObject());
                }
            }
            final List<Comment> cList = this.commentService.createInternalComment(au.getName(), comment.getComment(), comment.getIssueId(), null, contracts);
            comment.setAuthor(au.getKey());
            comment.setAuthorDisplayName(au.getDisplayName());
            final URI uri = this.avatarService.getAvatarURL(au, au);
            if (uri != null) {
                try {
                    comment.setAvatarUrl(uri.toURL().toString());
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            this.getNotificationService().notifyAboutComment(comment);
            final SimpleErrorCollection errorCollection = LicenseUtils.checkLicense(this.pluginLicenseManager);
            if (!errorCollection.hasAnyErrors()) {
                for (final Comment cm : cList) {
                    final List<SyncIssue> syncs = this.syncIssuesService.findByContract(cm.getContractId(), cm.getIssueId());
                    if (syncs != null && syncs.size() > 0) {
                        this.communicationService.send(Arrays.asList(cm));
                    }
                }
            }
            else {
                this.logger.error(ExtendedLoggerMessageType.REST, "Unable to send comment to remote instance. Invalid license key!");
            }
            final List<CommentT> comments = new ArrayList<CommentT>();
            for (final Comment c : cList) {
                final List<Comment> tmp = new ArrayList<Comment>();
                tmp.add(c);
                comments.add(new CommentT(c, this.isAbleToRemoveComment(c.getIssueId(), tmp)));
            }
            return Response.ok((Object)comments).build();
        }
        return Response.serverError().status(500).build();
    }
    
    private boolean isAbleToComment(final Long issueId) {
        final Issue issue = this.getIssueObject(issueId);
        final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        return (issue == null) ? this.permissionManager.hasPermission(15, issue.getProjectObject(), user) : this.permissionManager.hasPermission(15, issue, user);
    }
    
    private boolean isAbleToRemoveComment(final Long issueId, final Comment comment) {
        final List<Comment> tmp = new ArrayList<Comment>();
        tmp.add(comment);
        return this.isAbleToRemoveComment(issueId, tmp);
    }
    
    private boolean isAbleToRemoveComment(final Long issueId, final List<Comment> comments) {
        boolean result = true;
        final Issue issue = this.getIssueObject(issueId);
        final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        for (final Comment comment : comments) {
            final boolean isAbleToDeleteCommentAll = (issue == null) ? this.permissionManager.hasPermission(36, issue.getProjectObject(), user) : this.permissionManager.hasPermission(36, issue, user);
            boolean isAbleToDeleteCommentOwn = false;
            if (comment != null && !isAbleToDeleteCommentAll && comment.getCommentType() != null && comment.getCommentType().equals(CommentType.INTERNAL.ordinal()) && comment.getAuthor() != null && comment.getAuthor().equals(user.getName())) {
                isAbleToDeleteCommentOwn = this.permissionManager.hasPermission(37, issue.getProjectObject(), user);
            }
            result = (result && (isAbleToDeleteCommentAll || isAbleToDeleteCommentOwn));
        }
        return result;
    }
    
    private Issue getIssueObject(final Long issueId) {
        return (Issue)this.issueManager.getIssueObject(issueId);
    }
    
    public CommentService getCommentService() {
        return this.commentService;
    }
    
    public void setCommentService(final CommentService commentService) {
        this.commentService = commentService;
    }
    
    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }
    
    public void setPermissionManager(final PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
    
    public IssueManager getIssueManager() {
        return this.issueManager;
    }
    
    public void setIssueManager(final IssueManager issueManager) {
        this.issueManager = issueManager;
    }
    
    public CommunicationService getCommunicationService() {
        return this.communicationService;
    }
    
    public void setCommunicationService(final CommunicationService communicationService) {
        this.communicationService = communicationService;
    }
    
    public ContractService getContractService() {
        return this.contractService;
    }
    
    public void setContractService(final ContractService contractService) {
        this.contractService = contractService;
    }
    
    public SynchronizedIssuesService getSyncIssuesService() {
        return this.syncIssuesService;
    }
    
    public void setSyncIssuesService(final SynchronizedIssuesService syncIssuesService) {
        this.syncIssuesService = syncIssuesService;
    }
    
    public NotificationService getNotificationService() {
        return this.notificationService;
    }
    
    public void setNotificationService(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
