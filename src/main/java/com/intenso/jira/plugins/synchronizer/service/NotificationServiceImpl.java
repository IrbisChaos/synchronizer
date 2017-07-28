// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import java.util.Iterator;
import com.atlassian.mail.queue.MailQueue;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.util.BuildUtilsInfo;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.rest.model.CommentT;
import com.intenso.jira.plugins.synchronizer.entity.Comment;
import java.util.HashSet;
import java.util.Set;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;

public class NotificationServiceImpl extends AbstractFeatureFlagAware implements NotificationService
{
    private IssueManager issueManager;
    
    public NotificationServiceImpl(final IssueManager issueManager) {
        this.setIssueManager(issueManager);
    }
    
    private Set<String> getInvolvedUsers(final Issue issue) {
        final Set<String> users = new HashSet<String>();
        if (issue.getAssignee() != null) {
            users.add(issue.getAssignee().getEmailAddress());
        }
        if (issue.getCreator() != null) {
            users.add(issue.getCreator().getEmailAddress());
        }
        if (issue.getReporterUser() != null) {
            users.add(issue.getReporterUser().getEmailAddress());
        }
        return users;
    }
    
    @Override
    public void notifyAboutComment(final Comment comment) {
        if (this.isOff(this.getFeatureFlagKey())) {
            return;
        }
        final Long issueId = comment.getIssueId();
        final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
        final Set<String> usersToBeNotified = this.getInvolvedUsers(issue);
        this.sendNotification(this.getCommentNotificationSubject(issue), this.getCommentNotoficationBody(issue, null, null, comment.getAuthor(), comment.getComment()), usersToBeNotified);
    }
    
    @Override
    public void notifyAboutComment(final CommentT comment) {
        if (this.isOff(this.getFeatureFlagKey())) {
            return;
        }
        final Long issueId = comment.getIssueId();
        final Issue issue = (Issue)this.issueManager.getIssueObject(issueId);
        final Set<String> usersToBeNotified = this.getInvolvedUsers(issue);
        this.sendNotification(this.getCommentNotificationSubject(issue), this.getCommentNotoficationBody(issue, comment.getAvatarUrl(), comment.getAuthor(), comment.getAuthorDisplayName(), comment.getComment()), usersToBeNotified);
    }
    
    private String getCommentNotoficationBody(final Issue issue, final String avatarUrl, final String author, final String authorDisplayName, String commentText) {
        commentText = commentText.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
        final String url = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
        final String issueUrl = url + "/browse/" + issue.getKey();
        final String commitId = ((BuildUtilsInfo)ComponentAccessor.getOSGiComponentInstanceOfType((Class)BuildUtilsInfo.class)).getCommitId();
        final String version = "v" + ((BuildUtilsInfo)ComponentAccessor.getOSGiComponentInstanceOfType((Class)BuildUtilsInfo.class)).getVersion() + "#" + ((BuildUtilsInfo)ComponentAccessor.getOSGiComponentInstanceOfType((Class)BuildUtilsInfo.class)).getCurrentBuildNumber() + "-<span title='" + commitId + "' data-commit-id='" + commitId + "}'>sha1:" + commitId.substring(0, 7) + "</span>";
        final StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0' /> <base href='" + url + "' />" + "<title>Message Title</title>" + "</head>" + "<body class='jira' style='color: #333; font-family: Arial, sans-serif; font-size: 14px; line-height: 1,429'>" + "<table id='background-table' cellpadding='0' cellspacing='0' width='100%' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f5f5f5; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt'>" + "<!-- header here -->" + "<tr>" + "<td id='header-pattern-container' style='padding: 0px; border-collapse: collapse; padding: 10px 20px'>" + "<table id='header-pattern' cellspacing='0' cellpadding='0' border='0' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt'>" + "<tr>" + "<td id='header-avatar-image-container' valign='top' style='padding: 0px; border-collapse: collapse; vertical-align: top; width: 32px; padding-right: 8px'> ");
        if (avatarUrl != null) {
            body.append("<img id='header-avatar-image' class='image_fix' src='" + avatarUrl + "' height='32' width='32' border='0' style='border-radius: 3px; vertical-align: top' />");
        }
        else {
            body.append("<img id='header-avatar-image' class='image_fix' src='https://secure.gravatar.com/avatar/dfce6b4f12e5dd1849471dc8cb79e96a?d=mm&s=48' height='32' width='32' border='0' style='border-radius: 3px; vertical-align: top' />");
        }
        body.append("</td><td id='header-text-container' valign='middle' style='padding: 0px; border-collapse: collapse; vertical-align: middle; font-family: Arial, sans-serif; font-size: 14px; line-height: 20px; mso-line-height-rule: exactly; mso-text-raise: 1px'> ");
        if (author != null) {
            body.append("<a class='user-hover' rel='admin' id='email_admin' href='" + url + "/secure/ViewProfile.jspa?name=" + author + "' style='color:#3b73af; color: #3b73af; text-decoration: none'>" + authorDisplayName + "</a>");
        }
        else {
            body.append(authorDisplayName);
        }
        body.append(" <strong>commented</strong> on <a href='" + issueUrl + "' style='color: #3b73af; text-decoration: none'><img src='https://intenso.atlassian.net/servicedesk/issue-type-icons?icon=it-help' height='16' width='16' border='0' align='absmiddle' alt='Task' /> " + issue.getKey() + "</a>" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "<tr>" + "<td id='email-content-container' style='padding: 0px; border-collapse: collapse; padding: 0 20px'>" + "<table id='email-content-table' cellspacing='0' cellpadding='0' border='0' width='100%' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-spacing: 0; border-collapse: separate'>" + "<tr>" + "<!-- there needs to be content in the cell for it to render in some clients -->" + "<td class='email-content-rounded-top mobile-expand' style='padding: 0px; border-collapse: collapse; color: #fff; padding: 0 15px 0 16px; height: 15px; background-color: #fff; border-left: 1px solid #ccc; border-top: 1px solid #ccc; border-right: 1px solid #ccc; border-bottom: 0; border-top-right-radius: 5px; border-top-left-radius: 5px; height: 10px; line-height: 10px; padding: 0 15px 0 16px; mso-line-height-rule: exactly'>" + "&nbsp;" + "</td>" + "</tr>" + "<tr>" + "<td class='email-content-main mobile-expand' style='padding: 0px; border-collapse: collapse; border-left: 1px solid #ccc; border-right: 1px solid #ccc; border-top: 0; border-bottom: 0; padding: 0 15px 0 16px; background-color: #fff'>" + "<table class='page-title-pattern' cellspacing='0' cellpadding='0' border='0' width='100%' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt'>" + "<tr>" + "<td style='vertical-align: top; padding: 0px; border-collapse: collapse; padding-right: 5px; font-size: 20px; line-height: 30px; mso-line-height-rule: exactly' class='page-title-pattern-header-container'> <span class='page-title-pattern-header' style='font-family: Arial, sans-serif; padding: 0; font-size: 20px; line-height: 30px; mso-text-raise: 2px; mso-line-height-rule: exactly; vertical-align: middle'> <a href='" + issueUrl + "' style='color: #3b73af; text-decoration: none'>Re: " + issue.getSummary() + "</a> </span>" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "<tr>" + "<td id='text-paragraph-pattern-top' class='email-content-main mobile-expand  comment-top-pattern' style='padding: 0px; border-collapse: collapse; border-left: 1px solid #ccc; border-right: 1px solid #ccc; border-top: 0; border-bottom: 0; padding: 0 15px 0 16px; background-color: #fff; border-bottom: none; padding-bottom: 0'>" + "<table class='text-paragraph-pattern' cellspacing='0' cellpadding='0' border='0' width='100%' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: Arial, sans-serif; font-size: 14px; line-height: 20px; mso-line-height-rule: exactly; mso-text-raise: 2px'>" + "<tr>" + "<td class='text-paragraph-pattern-container mobile-resize-text' style='padding: 0px; border-collapse: collapse; padding: 0 0 10px 0'>" + "<p style='margin: 10px 0 0 0'>" + commentText + "</p>" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "<tr>" + "<td class='email-content-main mobile-expand' style='padding: 0px; border-collapse: collapse; border-left: 1px solid #ccc; border-right: 1px solid #ccc; border-top: 0; border-bottom: 0; padding: 0 15px 0 16px; background-color: #fff'>" + "<table id='actions-pattern' cellspacing='0' cellpadding='0' border='0' width='100%' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-family: Arial, sans-serif; font-size: 14px; line-height: 20px; mso-line-height-rule: exactly; mso-text-raise: 1px'>" + "<tr>" + "<td id='actions-pattern-container' valign='middle' style='padding: 0px; border-collapse: collapse; padding: 10px 0 10px 24px; vertical-align: middle; padding-left: 0'>" + "<table align='left' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt'>" + "<tr>" + "<td class='actions-pattern-action-icon-container' style='padding: 0px; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 14px; line-height: 20px; mso-line-height-rule: exactly; mso-text-raise: 0px; vertical-align: middle'> <a href='" + issueUrl + "#add-comment' target='_blank' title='Add Comment' style='color: #3b73af; text-decoration: none'> <img class='actions-pattern-action-icon-image' src='" + "data:image/png;base64," + "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAADAFBMVEX///+1tbWzs7OysrKrq6um" + "pqalpaWdnZ2ZmZn///+VlZWPj4/7+/vu7u7p6enn5+fl5eXk5OTh4eHW1tbT09PMzMzJycm6urq5" + "ubm1tbWzs7OysrKrq6upqamlpaWKioqFhYWRkZGKioqHh4eDg4OAgIB/f398fHx6enp4eHh2dnZ0" + "dHRycnJwcHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + "AAAAAAAAAAAAAAAIFWMjAAAAIXRSTlMAIiIzZoiZu8zd3d3u7u7u7u7u7u7u7u7u7u7u7u7u7u6n" + "leogAAAAG3RFWHRqaXJhLXN5c3RlbS1pbWFnZS10eXBlAGljb266KjlFAAAAo0lEQVR42l2P2wrC" + "MAyG09Pa1SqKzIk3QwTf/3G8UByKyK4EcV0POqzDaTUQwv+R/EkA/gKFJGyuQrFH/+iAWJnGOCCp" + "THcmALms6ne7ykpNeFHpft75xZWsL/XX0bMppj7eUXNseQykI7eZu3+0GpfYuVz1ephriwAnxdkD" + "QVwIsTVAoTUswZNBuPRkw6X01cqzZtMbdWB0aFqIANqb349ZLJ5XFjQIuveaPQAAAABJRU5ErkJg" + "gg==" + "' alt='Add Comment' title='Add Comment' height='16' width='16' border='0' style='vertical-align: middle' /> </a>" + "</td>" + "<td class='actions-pattern-action-text-container' style='padding: 0px; border-collapse: collapse; font-family: Arial, sans-serif; font-size: 14px; line-height: 20px; mso-line-height-rule: exactly; mso-text-raise: 4px; padding-left: 5px'> <a href='" + issueUrl + "#add-comment' target='_blank' title='Add Comment' style='color: #3b73af; text-decoration: none'>Add Comment</a>" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "<!-- there needs to be content in the cell for it to render in some clients -->" + "<tr>" + "<td class='email-content-rounded-bottom mobile-expand' style='padding: 0px; border-collapse: collapse; color: #fff; padding: 0 15px 0 16px; height: 5px; line-height: 5px; background-color: #fff; border-top: 0; border-left: 1px solid #ccc; border-bottom: 1px solid #ccc; border-right: 1px solid #ccc; border-bottom-right-radius: 5px; border-bottom-left-radius: 5px; mso-line-height-rule: exactly'>" + "&nbsp;" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "<tr>" + "<td id='footer-pattern' style='padding: 0px; border-collapse: collapse; padding: 12px 20px'>" + "<table id='footer-pattern-container' cellspacing='0' cellpadding='0' border='0' style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt'>" + "<tr>" + "<td id='footer-pattern-text' class='mobile-resize-text' width='100%' style='padding: 0px; border-collapse: collapse; color: #999; font-size: 12px; line-height: 18px; font-family: Arial, sans-serif; mso-line-height-rule: exactly; mso-text-raise: 2px'>" + "This message was sent by Atlassian JIRA (" + version + ")</span>" + "</td>" + "<td id='footer-pattern-logo-desktop-container' valign='top' style='padding: 0px; border-collapse: collapse; padding-left: 20px; vertical-align: top'>" + "<table style='border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt'>" + "<tr>" + "<td id='footer-pattern-logo-desktop-padding' style='padding: 0px; border-collapse: collapse; padding-top: 3px'> <img id='footer-pattern-logo-desktop' src='" + "data:image/png;base64," + "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" + "nvfbckn7YgAAAABJRU5ErkJggg==" + "' alt='Atlassian logo' title='Atlassian logo' width='169' height='36' class='image_fix' />" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "</table>" + "</td>" + "</tr>" + "</table>" + "</body>" + "</html>");
        return body.toString();
    }
    
    private String getCommentNotificationSubject(final Issue issue) {
        final StringBuilder subject = new StringBuilder();
        subject.append("[JIRA](");
        subject.append(issue.getKey());
        subject.append(") ");
        subject.append(issue.getSummary());
        return subject.toString();
    }
    
    private void sendNotification(final String subject, final String body, final Set<String> users) {
        final MailQueue mailQueue = ComponentAccessor.getMailQueue();
        for (final String user : users) {
            final Email email = new Email(user);
            email.setSubject(subject);
            email.setMimeType("text/html");
            email.setBody(body);
            final SingleMailQueueItem item = new SingleMailQueueItem((com.atlassian.mail.Email)email);
            mailQueue.addItem((MailQueueItem)item);
        }
    }
    
    public IssueManager getIssueManager() {
        return this.issueManager;
    }
    
    public void setIssueManager(final IssueManager issueManager) {
        this.issueManager = issueManager;
    }
    
    @Override
    public String getFeatureFlagKey() {
        return "external.comment.notification.feature";
    }
    
    @Override
    public void notifyAboutAlert(final String message) {
        final String url = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
        final String subject = "[JIRA Synchronization problem report]";
        final String body = "JIRA Synchronization problem report: <a href='" + url + "'>" + url + "</a><br />" + message;
        final Set<String> users = new HashSet<String>();
        try {
            final AlertsService alertsService = (AlertsService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)AlertsService.class);
            final String emails = alertsService.getConfiguration().emails;
            if (emails != null && !emails.isEmpty()) {
                final String[] split;
                final String[] emailsArray = split = emails.split(",");
                for (final String email : split) {
                    final String user = email.trim();
                    if (!user.isEmpty()) {
                        users.add(user);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.sendNotification(subject, body, users);
    }
}
