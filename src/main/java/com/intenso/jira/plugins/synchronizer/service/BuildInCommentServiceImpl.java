// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.jira.issue.comments.MutableComment;
import com.google.gson.Gson;
import com.intenso.jira.plugins.synchronizer.service.comm.Response;
import com.atlassian.jira.user.ApplicationUser;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorServiceImpl;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.google.gson.JsonObject;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.issue.Issue;
import java.util.ArrayList;
import com.atlassian.jira.issue.comments.Comment;
import java.util.List;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicatorService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;

public class BuildInCommentServiceImpl implements BuildInCommentService
{
    private CommentManager commentManager;
    private ConnectionService connectionService;
    private IssueManager issueManager;
    private UserManager userManager;
    private SynchronizedIssuesService synchronizedIssuesService;
    private CommunicatorService communicatorService;
    
    public BuildInCommentServiceImpl(final CommentManager commentManager, final IssueManager issueManager, final ConnectionService connectionService, final UserManager userManager, final SynchronizedIssuesService synchronizedIssuesService, final CommunicatorService communicatorService) {
        this.commentManager = commentManager;
        this.issueManager = issueManager;
        this.connectionService = connectionService;
        this.userManager = userManager;
        this.synchronizedIssuesService = synchronizedIssuesService;
        this.communicatorService = communicatorService;
    }
    
    @Override
    public List<Comment> getAllBuildInComments(final Long issueId) {
        if (issueId == null) {
            return new ArrayList<Comment>();
        }
        final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
        if (issue == null) {
            return new ArrayList<Comment>();
        }
        final List<Comment> comments = (List<Comment>)this.commentManager.getComments(issue);
        if (comments == null) {
            return new ArrayList<Comment>();
        }
        return comments;
    }
    
    @Override
    public Comment createComment(final Long issueId, final Contract contract, final CommentT commentT) {
        final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
        if (issue == null) {
            return null;
        }
        final List<SyncIssue> syncIssues = this.synchronizedIssuesService.findByContract(contract.getID(), issueId);
        final Connection connection = this.connectionService.get(contract.getConnectionId());
        final String username = connection.getUsername();
        final ApplicationUser user = this.userManager.getUserByName(username);
        if (user == null) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("body", commentT.getBuildInCommentBody(connection, contract, (syncIssues != null && syncIssues.size() > 0) ? syncIssues.get(0).getRemoteIssueKey() : null));
        final String uri = "/issue/" + issue.getKey() + "/comment";
        final CommunicatorServiceImpl.HttpRequestMethod method = CommunicatorServiceImpl.HttpRequestMethod.POST;
        final Response response = this.communicatorService.callInternalRest(connection, method, uri, jsonObject.toString());
        if (response.getStatus().equals(201)) {
            final Gson gson = new GsonBuilder().create();
            final JsonObject createCommentJson = gson.fromJson(response.getJson(), (Type)JsonElement.class);
            final Long id = createCommentJson.getAsJsonObject().get("id").getAsLong();
            return this.commentManager.getCommentById(id);
        }
        return null;
    }
    
    @Override
    public Comment updateBuildComment(final Long buildInCommentId, final String dateExternal) {
        final MutableComment comment = this.commentManager.getMutableComment(buildInCommentId);
        if (comment != null) {
            comment.setBody(comment.getBody() + " \\\\ *Remote comment create date: " + dateExternal + "*");
            this.commentManager.update((Comment)comment, (boolean)Boolean.FALSE);
        }
        return (Comment)comment;
    }
}
