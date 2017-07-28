// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.crowd.embedded.api.Group;
import java.util.Set;
import com.atlassian.jira.security.roles.ProjectRole;
import java.util.Iterator;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.util.ErrorCollection;
import java.util.Collection;
import com.google.common.collect.Lists;
import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import java.util.List;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class TechnicalUserPermissionsAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = -3541523796304052153L;
    private String contract;
    private boolean create;
    private boolean update;
    private boolean delete;
    private boolean comments;
    private boolean attachments;
    private boolean worklog;
    private boolean workflow;
    private boolean resolution;
    private boolean assignee;
    private boolean reporter;
    
    public String doDefault() throws Exception {
        this.getPermissions();
        return super.doDefault();
    }
    
    private void getPermissions() {
        if (this.contract != null) {
            final ContractService contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
            final List<Contract> contracts = contractService.findByContractName(this.contract);
            if (!contracts.isEmpty()) {
                final Contract contract = contracts.get(0);
                final Long projectId = contract.getProjectId();
                final Integer connectionId = contract.getConnectionId();
                final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
                final Connection connection = connectionService.find(connectionId);
                final String technicalUserName = connection.getUsername();
                final UserManager userManager = (UserManager)ComponentAccessor.getComponent((Class)UserManager.class);
                final ApplicationUser technicalUser = userManager.getUserByName(technicalUserName);
                if (technicalUser != null) {
                    final ProjectManager projectManager = (ProjectManager)ComponentAccessor.getComponent((Class)ProjectManager.class);
                    final Project project = projectManager.getProjectObj(projectId);
                    if (project == null) {
                        return;
                    }
                    final PermissionManager permissionManager = (PermissionManager)ComponentAccessor.getComponent((Class)PermissionManager.class);
                    this.create = permissionManager.hasPermission(ProjectPermissions.CREATE_ISSUES, project, technicalUser);
                    this.update = permissionManager.hasPermission(ProjectPermissions.EDIT_ISSUES, project, technicalUser);
                    this.delete = permissionManager.hasPermission(ProjectPermissions.DELETE_ISSUES, project, technicalUser);
                    this.comments = permissionManager.hasPermission(ProjectPermissions.ADD_COMMENTS, project, technicalUser);
                    this.attachments = permissionManager.hasPermission(ProjectPermissions.CREATE_ATTACHMENTS, project, technicalUser);
                    this.worklog = permissionManager.hasPermission(ProjectPermissions.WORK_ON_ISSUES, project, technicalUser);
                    this.workflow = permissionManager.hasPermission(ProjectPermissions.TRANSITION_ISSUES, project, technicalUser);
                    this.resolution = permissionManager.hasPermission(ProjectPermissions.RESOLVE_ISSUES, project, technicalUser);
                    this.assignee = permissionManager.hasPermission(ProjectPermissions.ASSIGN_ISSUES, project, technicalUser);
                    this.reporter = permissionManager.hasPermission(ProjectPermissions.MODIFY_REPORTER, project, technicalUser);
                }
            }
        }
    }
    
    private void addPermissions() {
        if (this.contract != null) {
            final ContractService contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
            final List<Contract> contracts = contractService.findByContractName(this.contract);
            if (!contracts.isEmpty()) {
                final Contract contract = contracts.get(0);
                final Long projectId = contract.getProjectId();
                final ProjectManager projectManager = (ProjectManager)ComponentAccessor.getComponent((Class)ProjectManager.class);
                final Project project = projectManager.getProjectObj(projectId);
                if (project == null) {
                    return;
                }
                final Integer connectionId = contract.getConnectionId();
                final ConnectionService connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
                final Connection connection = connectionService.find(connectionId);
                final String technicalUserName = connection.getUsername();
                final UserManager userManager = (UserManager)ComponentAccessor.getComponent((Class)UserManager.class);
                final ApplicationUser technicalUser = userManager.getUserByName(technicalUserName);
                final Scheme scheme = ((PermissionSchemeManager)ComponentAccessor.getComponent((Class)PermissionSchemeManager.class)).getSchemeFor(project);
                final List<SchemeEntity> schemeEntitys = (List<SchemeEntity>)scheme.getEntitiesByType((Object)ProjectPermissions.CREATE_ISSUES);
                for (final SchemeEntity schemeEntity : schemeEntitys) {
                    final String type = schemeEntity.getType();
                    schemeEntity.getParameter();
                }
                final ProjectRole role = null;
                final ProjectRoleService prs = (ProjectRoleService)ComponentAccessor.getComponent((Class)ProjectRoleService.class);
                final ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
                try {
                    final PermissionManager permissionManager = (PermissionManager)ComponentAccessor.getComponent((Class)PermissionManager.class);
                    boolean isAdmin = permissionManager.hasPermission(ProjectPermissions.ADMINISTER_PROJECTS, project, currentUser);
                    if (!isAdmin) {
                        ApplicationUser admin = null;
                        final Set<ApplicationUser> users = (Set<ApplicationUser>)userManager.getAllUsers();
                        for (final ApplicationUser user : users) {
                            isAdmin = permissionManager.hasPermission(ProjectPermissions.ADMINISTER_PROJECTS, project, user);
                            if (isAdmin) {
                                admin = user;
                                break;
                            }
                        }
                        if (admin != null) {
                            ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(admin);
                        }
                    }
                    prs.addActorsToProjectRole((Collection)Lists.newArrayList((Object[])new String[] { technicalUser.getKey() }), role, project, "atlassian-user-role-actor", (ErrorCollection)this);
                }
                finally {
                    ComponentAccessor.getJiraAuthenticationContext().setLoggedInUser(currentUser);
                }
                final Group group = null;
                final GroupManager groupManager = (GroupManager)ComponentAccessor.getComponent((Class)GroupManager.class);
                try {
                    groupManager.addUserToGroup(technicalUser, group);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                final SchemeManager schemeManager = (SchemeManager)ComponentAccessor.getComponent((Class)SchemeManager.class);
                schemeManager.addSchemeToProject(project, scheme);
                projectManager.updateProject(project, project.getName(), project.getDescription(), technicalUser.getKey(), project.getUrl(), project.getAssigneeType());
            }
        }
    }
    
    public String getContract() {
        return this.contract;
    }
    
    public void setContract(final String contract) {
        this.contract = contract;
    }
    
    public boolean getCreate() {
        return this.create;
    }
    
    public void setCreate(final boolean create) {
        this.create = create;
    }
    
    public boolean getUpdate() {
        return this.update;
    }
    
    public void setUpdate(final boolean update) {
        this.update = update;
    }
    
    public boolean getDelete() {
        return this.delete;
    }
    
    public void setDelete(final boolean delete) {
        this.delete = delete;
    }
    
    public boolean getComments() {
        return this.comments;
    }
    
    public void setComments(final boolean comments) {
        this.comments = comments;
    }
    
    public boolean getAttachments() {
        return this.attachments;
    }
    
    public void setAttachments(final boolean attachments) {
        this.attachments = attachments;
    }
    
    public boolean getWorkflow() {
        return this.workflow;
    }
    
    public void setWorkflow(final boolean workflow) {
        this.workflow = workflow;
    }
    
    public boolean getResolution() {
        return this.resolution;
    }
    
    public void setResolution(final boolean resolution) {
        this.resolution = resolution;
    }
    
    public boolean getAssignee() {
        return this.assignee;
    }
    
    public void setAssignee(final boolean assignee) {
        this.assignee = assignee;
    }
    
    public boolean getReporter() {
        return this.reporter;
    }
    
    public void setReporter(final boolean reporter) {
        this.reporter = reporter;
    }
    
    public boolean isWorklog() {
        return this.worklog;
    }
    
    public void setWorklog(final boolean worklog) {
        this.worklog = worklog;
    }
}
