// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import java.util.Collection;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.intenso.jira.plugins.synchronizer.service.comm.CommunicationService;
import java.util.Iterator;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.entity.SyncIssue;
import com.intenso.jira.plugins.synchronizer.service.SynchronizedIssuesService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import com.intenso.jira.plugins.synchronizer.rest.model.OperationT;
import com.atlassian.jira.issue.Issue;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class BulkOperationAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = -7649223419029636248L;
    private Map<String, String> filters;
    private String filter;
    private List<Issue> issues;
    private int issuesToSynch;
    private List<OperationT> operationTs;
    private Set<Issue> failSynchIssues;
    private int successSynchIssuesCount;
    
    public BulkOperationAction() {
        this.filters = new HashMap<String, String>();
        this.filter = "";
        this.issues = new ArrayList<Issue>();
        this.issuesToSynch = 0;
        this.operationTs = new ArrayList<OperationT>();
        this.failSynchIssues = new HashSet<Issue>();
        this.successSynchIssuesCount = 0;
    }
    
    public String doDefault() throws Exception {
        this.searchAllFilters();
        return super.doDefault();
    }
    
    public String doResult() throws Exception {
        this.issueCalculate();
        return "result";
    }
    
    public String doStart() throws Exception {
        this.issueCalculate();
        this.createRemoteIssues();
        return "start";
    }
    
    protected String doExecute() throws Exception {
        this.searchAllFilters();
        return super.doExecute();
    }
    
    protected void doValidation() {
        super.doValidation();
    }
    
    private void issueCalculate() throws SearchException {
        if (this.filter != "") {
            final SearchRequest sr = this.searchFilterById(Long.parseLong(this.filter));
            if (sr != null) {
                final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
                final SearchResults searchResults = ((SearchService)ComponentAccessor.getComponent((Class)SearchService.class)).search(user, sr.getQuery(), PagerFilter.getUnlimitedFilter());
                this.issues = (List<Issue>)searchResults.getIssues();
                if (this.issues.size() > 0) {
                    this.check();
                }
            }
        }
    }
    
    private void check() {
        this.issuesToSynch = 0;
        for (final Issue issue : this.issues) {
            final List<Contract> contracts = ((ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class)).findByContext(issue.getProjectObject().getId(), issue.getIssueTypeId());
            final Map<Integer, String> contractsWithIssues = new HashMap<Integer, String>();
            final List<SyncIssue> findByIssue = ((SynchronizedIssuesService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizedIssuesService.class)).findByIssue(issue.getId());
            for (final SyncIssue syncIssue : findByIssue) {
                final Integer contractId = syncIssue.getContractId();
                contractsWithIssues.put(contractId, this.findContractName(contracts, contractId));
            }
            if (((SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class)).canCreateRemoteIssueOnDemand()) {
                boolean exist = false;
                for (final Contract c : contracts) {
                    if (!contractsWithIssues.keySet().contains(c.getID()) && new Integer(0).equals(c.getStatus())) {
                        exist = true;
                        final OperationT operationT = new OperationT();
                        operationT.setContractId(c.getID());
                        operationT.setIssueId(issue.getId());
                        this.operationTs.add(operationT);
                    }
                }
                if (!exist) {
                    continue;
                }
                ++this.issuesToSynch;
            }
        }
    }
    
    private String findContractName(final List<Contract> contracts, final Integer id) {
        if (contracts != null && id != null) {
            for (final Contract c : contracts) {
                if (id.equals(c.getID())) {
                    return c.getContractName();
                }
            }
        }
        return "";
    }
    
    public boolean createRemoteIssue(final OperationT input) {
        boolean result = false;
        try {
            final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            if (input.getIssueId() != null && input.getContractId() != null) {
                final IssueService.IssueResult issueResult = ComponentAccessor.getIssueService().getIssue(user, input.getIssueId());
                if (issueResult != null && issueResult.getIssue() != null) {
                    result = ((CommunicationService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)CommunicationService.class)).sendCreate((Issue)issueResult.getIssue(), input.getContractId(), user);
                }
            }
        }
        catch (Exception ex) {}
        return result;
    }
    
    private void createRemoteIssues() {
        for (final OperationT operationT : this.operationTs) {
            if (!this.createRemoteIssue(operationT)) {
                final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
                final IssueService.IssueResult issueResult = ComponentAccessor.getIssueService().getIssue(user, operationT.getIssueId());
                if (issueResult == null || issueResult.getIssue() == null) {
                    continue;
                }
                this.failSynchIssues.add((Issue)issueResult.getIssue());
            }
            else {
                ++this.successSynchIssuesCount;
            }
        }
    }
    
    private void searchAllFilters() {
        final List<ApplicationUser> users = (List<ApplicationUser>)((UserManager)ComponentAccessor.getComponent((Class)UserManager.class)).getAllApplicationUsers();
        final List<SearchRequest> filtersResult = new ArrayList<SearchRequest>();
        for (final ApplicationUser user : users) {
            filtersResult.addAll(((SearchRequestService)ComponentAccessor.getComponent((Class)SearchRequestService.class)).getNonPrivateFilters(user));
        }
        final ApplicationUser user2 = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        filtersResult.addAll(((SearchRequestService)ComponentAccessor.getComponent((Class)SearchRequestService.class)).getOwnedFilters(user2));
        for (final SearchRequest filter : filtersResult) {
            this.filters.put(filter.getId().toString(), filter.getName() + " (" + filter.getOwner().getDisplayName() + ")");
        }
    }
    
    private SearchRequest searchFilterById(final Long id) {
        final ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        final JiraServiceContext jsc = (JiraServiceContext)new JiraServiceContextImpl(user);
        final SearchRequest filter = ((SearchRequestService)ComponentAccessor.getComponent((Class)SearchRequestService.class)).getFilter(jsc, id);
        return filter;
    }
    
    public Map<String, String> getFilters() {
        return this.filters;
    }
    
    public void setFilters(final Map<String, String> filters) {
        this.filters = filters;
    }
    
    public List<Issue> getIssues() {
        return this.issues;
    }
    
    public void setIssues(final List<Issue> issues) {
        this.issues = issues;
    }
    
    public String getFilter() {
        return this.filter;
    }
    
    public void setFilter(final String filter) {
        this.filter = filter;
    }
    
    public List<OperationT> getOperationTs() {
        return this.operationTs;
    }
    
    public void setOperationTs(final List<OperationT> operationTs) {
        this.operationTs = operationTs;
    }
    
    public int getIssuesToSynch() {
        return this.issuesToSynch;
    }
    
    public void setIssuesToSynch(final int issuesToSynch) {
        this.issuesToSynch = issuesToSynch;
    }
    
    public Set<Issue> getFailSynchIssues() {
        return this.failSynchIssues;
    }
    
    public void setFailSynchIssues(final Set<Issue> failSynchIssues) {
        this.failSynchIssues = failSynchIssues;
    }
    
    public int getSuccessSynchIssuesCount() {
        return this.successSynchIssuesCount;
    }
    
    public void setSuccessSynchIssuesCount(final int successSynchIssuesCount) {
        this.successSynchIssuesCount = successSynchIssuesCount;
    }
}
