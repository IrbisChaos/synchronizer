// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.service;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.intenso.jira.plugins.synchronizer.entity.ContractStatus;
import net.java.ao.RawEntity;
import java.util.HashMap;
import com.atlassian.jira.issue.fields.CustomField;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItemBuilder;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.Issue;
import org.ofbiz.core.entity.GenericValue;
import java.util.Collection;
import com.intenso.jira.plugins.synchronizer.utils.ChangeHistoryHelper;
import com.intenso.jira.plugins.synchronizer.entity.ContractFieldMappingEntry;
import com.intenso.jira.plugins.synchronizer.listener.ContractChangeItem;
import com.atlassian.jira.event.issue.IssueEvent;
import com.google.common.collect.Iterables;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import net.java.ao.Query;
import com.intenso.jira.plugins.synchronizer.entity.ContractEvents;
import java.util.ArrayList;
import com.intenso.jira.plugins.synchronizer.entity.EventType;
import java.util.concurrent.ConcurrentHashMap;
import com.atlassian.activeobjects.external.ActiveObjects;
import java.util.List;
import java.util.Map;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.intenso.jira.plugins.synchronizer.entity.Contract;

public class ContractServiceImpl extends SyncAwareServiceImpl<Contract> implements ContractService
{
    public static final String COL_NAME = "CONTRACT_NAME";
    public static final String COL_CONNECTION = "CONNECTION_ID";
    public static final String COL_PROJECT_ID = "PROJECT_ID";
    public static final String COL_ISSUE_TYPE = "ISSUE_TYPE";
    public static final String COL_REMOTE_CONTRACT_NAME = "REMOTE_CONTEXT_NAME";
    public static final String COL_FIELD_MAPPING = "FIELD_MAPPING";
    public static final String COL_RELATED_ISSUE_CF = "RELATED_ISSUE_CF";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_WORKFLOW_MAPPING = "WORKFLOW_MAPPING";
    public static final String COL_COMMENTS = "COMMENTS";
    public static final String COL_ATTACHMENTS = "ATTACHMENTS";
    public static final String COL_DISABLE_COMMENTS = "DISABLE_COMMENTS";
    public static final String COL_SYNCHRONIZE_ALL_ATTACHMENTS = "SYNCHRONIZE_ALL_ATTACHMENTS";
    public static final String COL_ADD_PREFIX_TO_REMOTE_ATTACHMENTS = "ADD_PREFIX_TO_REMOTE_ATTACHMENTS";
    public static final String COL_JQL_CONSTRAINTS = "JQL_CONSTRAINTS";
    public static final String COL_CONTRACT = "CONTRACT_ID";
    public static final String COL_EVENT_ID = "EVENT_ID";
    public static final String COL_EVENT_TYPE = "EVENT_TYPE";
    private CustomFieldManager customFieldManager;
    private FieldManager fieldManager;
    private final Map<Long, List<Contract>> matrixCache;
    
    public ContractServiceImpl(final ActiveObjects dao, final CustomFieldManager customFieldManager, final FieldManager fieldManager) {
        super(dao, Contract.class);
        this.matrixCache = new ConcurrentHashMap<Long, List<Contract>>();
        this.customFieldManager = customFieldManager;
        this.fieldManager = fieldManager;
    }
    
    @Override
    public List<Contract> getContracts(final Long eventId, final String issueTypeId, final Long projectId, final EventType eventType) {
        final List<Contract> result = new ArrayList<Contract>();
        try {
            final List<Contract> contracts = this.findByContext(projectId, issueTypeId);
            for (final Contract c : contracts) {
                final ContractEvents[] ce = (ContractEvents[])this.getDao().find((Class)ContractEvents.class, Query.select().where("EVENT_ID = ? AND CONTRACT_ID = ? AND EVENT_TYPE = ?", new Object[] { eventId, c.getID(), eventType.ordinal() }));
                if (ce != null && ce.length > 0) {
                    result.add(c);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public Set<Integer> getContractsIds(final Long eventId, final String issueTypeId, final Long projectId, final EventType eventType) {
        final Set<Integer> result = new HashSet<Integer>();
        final List<Contract> contracts = this.getContracts(eventId, issueTypeId, projectId, eventType);
        for (final Contract c : contracts) {
            result.add(c.getID());
        }
        return result;
    }
    
    @Override
    public List<Contract> getContractsForEvent(final Long eventId, final EventType type) {
        List<Contract> result = new ArrayList<Contract>();
        try {
            final ContractEvents[] ce = (ContractEvents[])this.getDao().find((Class)ContractEvents.class, Query.select().where("EVENT_ID = ? AND EVENT_TYPE" + ((type == null) ? " IS ?" : " = ?"), new Object[] { eventId, (type != null) ? type.ordinal() : null }));
            final List<ContractEvents> ceList = Arrays.asList(ce);
            final List<Integer> ints = new ArrayList<Integer>();
            for (final ContractEvents c : ceList) {
                ints.add(c.getContractId());
            }
            final String placeholderCommaList = Joiner.on(", ").join(Iterables.transform((Iterable)ints, Functions.constant((Object)"?")));
            final Object[] matchValuesArray = Iterables.toArray((Iterable)ints, (Class)Object.class);
            final Contract[] contracts = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("ID IN (" + placeholderCommaList + ")", matchValuesArray));
            result = Arrays.asList(contracts);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return (result != null) ? result : new ArrayList<Contract>();
    }
    
    @Override
    public List<Contract> getContractsWithWorkflow(final String issueTypeId, final Long projectId, final Long workflowId) {
        final List<Contract> result = Arrays.asList((Contract[])this.getDao().find((Class)Contract.class, Query.select().where("PROJECT_ID = ? AND ISSUE_TYPE = ? AND WORKFLOW_MAPPING IS NOT NULL ", new Object[] { projectId, issueTypeId })));
        return result;
    }
    
    @Override
    public List<ContractEvents> getEventsForContract(final Integer contractId, final EventType type) {
        final ContractEvents[] list = (ContractEvents[])this.getDao().find((Class)ContractEvents.class, Query.select().where("CONTRACT_ID = ? AND EVENT_TYPE" + ((type == null) ? " IS ?" : " = ?"), new Object[] { contractId, (type == null) ? type : type.ordinal() }));
        return (list != null) ? Arrays.asList(list) : new ArrayList<ContractEvents>();
    }
    
    @Override
    public List<ContractChangeItem> changes(final Contract contract, final IssueEvent issueEvent) {
        final List<ContractFieldMappingEntry> entries = this.getContractFielMappingEntries(contract);
        final Set<String> mappedFields = new HashSet<String>();
        for (final ContractFieldMappingEntry en : entries) {
            mappedFields.add(en.getLocalFieldId());
        }
        final List<ContractChangeItem> items = new ArrayList<ContractChangeItem>();
        final GenericValue changelog = issueEvent.getChangeLog();
        if (issueEvent.getComment() != null) {
            this.handleComments(issueEvent);
        }
        if (changelog != null) {
            final List<GenericValue> changeItems = ChangeHistoryHelper.getChangeHistory(changelog);
            if (changeItems != null) {
                items.addAll(this.handleIssueUpdate(changeItems, issueEvent.getIssue(), mappedFields, entries));
            }
        }
        else {
            items.addAll(this.handleIssueCreate(entries, issueEvent.getIssue()));
        }
        return items;
    }
    
    @Override
    public List<ContractChangeItem> changes(final Contract contract, final Issue issue) {
        final List<ContractFieldMappingEntry> entries = this.getContractFielMappingEntries(contract);
        final Set<String> mappedFields = new HashSet<String>();
        for (final ContractFieldMappingEntry en : entries) {
            mappedFields.add(en.getLocalFieldId());
        }
        final List<ContractChangeItem> items = new ArrayList<ContractChangeItem>();
        items.addAll(this.handleIssueCreate(entries, issue));
        return items;
    }
    
    private List<ContractChangeItem> handleComments(final IssueEvent issueEvent) {
        final Comment c = issueEvent.getComment();
        final String commentBody = c.getBody();
        final List<ContractChangeItem> result = new ArrayList<ContractChangeItem>();
        return result;
    }
    
    private List<ContractChangeItem> handleIssueUpdate(final List<GenericValue> changeItems, final Issue issue, final Set<String> mappedFields, final List<ContractFieldMappingEntry> contractfieldMappingEntries) {
        final List<ContractChangeItem> result = new ArrayList<ContractChangeItem>();
        for (final GenericValue changetemp : changeItems) {
            final CustomField customField = this.customFieldManager.getCustomFieldObjectByName(changetemp.getString("field"));
            String field;
            if (customField == null) {
                field = changetemp.getString("field");
            }
            else {
                field = customField.getId();
            }
            final String fieldType = changetemp.getString("fieldtype");
            final ContractChangeItem ci = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName(field).type(fieldType).build(contractfieldMappingEntries);
            if (ci != null && ci.getField() != null) {
                boolean exist = false;
                for (final ContractChangeItem item : result) {
                    if (item.getField().getId().equals(ci.getField().getId())) {
                        exist = true;
                        break;
                    }
                }
                if (exist || (!mappedFields.contains(ci.getField().getId()) && !mappedFields.contains(ChangeHistoryHelper.fieldName2FieldId(ci.getFieldName())))) {
                    continue;
                }
                result.add(ci);
            }
        }
        this.handleCreatedUpdated(issue, mappedFields, contractfieldMappingEntries, result);
        return result;
    }
    
    private void handleCreatedUpdated(final Issue issue, final List<ContractFieldMappingEntry> contractfieldMappingEntries, final List<ContractChangeItem> result) {
        final Set<String> mappedFields = new HashSet<String>();
        for (final ContractFieldMappingEntry en : contractfieldMappingEntries) {
            mappedFields.add(en.getLocalFieldId());
        }
        this.handleCreatedUpdated(issue, mappedFields, contractfieldMappingEntries, result);
    }
    
    private void handleCreatedUpdated(final Issue issue, final Set<String> mappedFields, final List<ContractFieldMappingEntry> contractfieldMappingEntries, final List<ContractChangeItem> result) {
        if (mappedFields.contains("created")) {
            final ContractChangeItem created = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName("created").type("jira").build(contractfieldMappingEntries);
            result.add(created);
        }
        if (mappedFields.contains("updated")) {
            final ContractChangeItem updated = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName("updated").type("jira").build(contractfieldMappingEntries);
            result.add(updated);
        }
        if (mappedFields.contains("resolutiondate")) {
            final ContractChangeItem resolved = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName("resolutiondate").type("jira").build(contractfieldMappingEntries);
            result.add(resolved);
        }
        if (mappedFields.contains("issuekey")) {
            final ContractChangeItem resolved = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName("issuekey").type("jira").build(contractfieldMappingEntries);
            result.add(resolved);
        }
    }
    
    private List<ContractChangeItem> handleIssueCreate(final List<ContractFieldMappingEntry> mappings, final Issue issue) {
        final List<ContractChangeItem> result = new ArrayList<ContractChangeItem>();
        for (final ContractFieldMappingEntry fme : mappings) {
            final String fid = fme.getLocalFieldId();
            String fieldType = null;
            if (this.fieldManager.isCustomField(fid)) {
                fieldType = "custom";
            }
            else {
                fieldType = "jira";
            }
            final ContractChangeItem cci = new ContractChangeItemBuilder(issue, this.customFieldManager, this.fieldManager).fieldName(fid).type(fieldType).build(mappings);
            if (cci != null) {
                result.add(cci);
            }
        }
        this.handleCreatedUpdated(issue, mappings, result);
        return result;
    }
    
    private List<ContractFieldMappingEntry> getContractFielMappingEntries(final Contract c) {
        final ContractFieldMappingEntry[] entries = (ContractFieldMappingEntry[])this.getDao().find((Class)ContractFieldMappingEntry.class, Query.select().where("CONTRACT_ID = ?", new Object[] { c.getID() }));
        return Arrays.asList(entries);
    }
    
    @Override
    public List<Contract> findAll() {
        final Contract[] list = (Contract[])this.getDao().find((Class)Contract.class, Query.select().order("ID ASC"));
        return (list != null) ? Arrays.asList(list) : new ArrayList<Contract>();
    }
    
    @Override
    public ContractEvents addContractEvent(final Integer contractId, final Long eventId, final EventType type) {
        final ContractEvents[] entries = (ContractEvents[])this.getDao().find((Class)ContractEvents.class, Query.select().where("CONTRACT_ID = ? AND EVENT_ID = ? AND EVENT_TYPE" + ((type == null) ? " IS ?" : " = ?"), new Object[] { contractId, eventId, type.ordinal() }));
        if (entries == null || entries.length == 0) {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("CONTRACT_ID", contractId);
            params.put("EVENT_ID", eventId);
            params.put("EVENT_TYPE", type.ordinal());
            return (ContractEvents)this.getDao().create((Class)ContractEvents.class, (Map)params);
        }
        return entries[0];
    }
    
    @Override
    public ContractEvents[] removeContractEvent(final Integer contractId, final Long eventId, final EventType type) {
        final ContractEvents[] entries = (ContractEvents[])this.getDao().find((Class)ContractEvents.class, Query.select().where("CONTRACT_ID = ? AND EVENT_ID = ? AND EVENT_TYPE" + ((type == null) ? " IS ?" : " = ?"), new Object[] { contractId, eventId, (type == null) ? type : type.ordinal() }));
        if (entries != null && entries.length > 0) {
            this.getDao().delete((RawEntity[])entries);
            return entries;
        }
        return null;
    }
    
    @Override
    public Contract createContract(final Integer connectionId, final String name) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRACT_NAME", name);
        params.put("CONNECTION_ID", connectionId);
        params.put("STATUS", ContractStatus.DISABLED);
        final Contract contract = (Contract)this.getDao().create((Class)Contract.class, (Map)params);
        this.makeConnectionOutOfSync(connectionId);
        return contract;
    }
    
    @Override
    public Contract createContract(final Integer connectionId, final String contractName, final ContractStatus status) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("CONTRACT_NAME", contractName);
        params.put("CONNECTION_ID", connectionId);
        params.put("STATUS", status.ordinal());
        final Contract contract = (Contract)this.getDao().create((Class)Contract.class, (Map)params);
        this.makeConnectionOutOfSync(connectionId);
        return contract;
    }
    
    @Override
    public List<Contract> findByContext(final Long projectId, final String issueTypeId) {
        final Contract[] result = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("PROJECT_ID = ? AND ISSUE_TYPE = ?", new Object[] { projectId, issueTypeId }));
        return Arrays.asList(result);
    }
    
    @Override
    public List<Contract> findByContextAndStatus(final Long projectId, final String issueTypeId, final ContractStatus status) {
        final Contract[] result = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("PROJECT_ID = ? AND ISSUE_TYPE = ? AND STATUS = ? ", new Object[] { projectId, issueTypeId, status.ordinal() }));
        return Arrays.asList(result);
    }
    
    @Override
    public List<Contract> findByContextAndStatusAndComments(final Long projectId, final String issueTypeId, final ContractStatus status, final Integer lever) {
        final Contract[] result = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("PROJECT_ID = ? AND ISSUE_TYPE = ? AND STATUS = ? AND COMMENTS = ? ", new Object[] { projectId, issueTypeId, status.ordinal(), (lever == null) ? 0 : lever }));
        return Arrays.asList(result);
    }
    
    @Override
    public List<Contract> findByConnection(final Integer connection) {
        final Contract[] result = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("CONNECTION_ID= ?", new Object[] { connection }));
        return Arrays.asList(result);
    }
    
    @Override
    public List<Contract> findByConnectionAndName(final Integer connectionId, final String contractName) {
        final Contract[] contracts = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("CONNECTION_ID= ? AND CONTRACT_NAME = ?", new Object[] { connectionId, contractName }));
        return Arrays.asList(contracts);
    }
    
    @Override
    public List<Contract> findContracts(final Project projectObject, final IssueType issueTypeObject) {
        final Contract[] contracts = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("PROJECT_ID = ? AND ISSUE_TYPE = ?", new Object[] { projectObject.getId(), issueTypeObject.getId() }));
        return Arrays.asList(contracts);
    }
    
    @Override
    public void updateContractEvents(final Integer contractId, final List<Long> typeEvents, final EventType type) {
        final List<ContractEvents> events = this.getEventsForContract(contractId, type);
        for (final ContractEvents ce : events) {
            boolean found = false;
            if (typeEvents != null) {
                for (final Long cet : typeEvents) {
                    if ((int)(Object)cet == ce.getID()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                this.getDao().delete(new RawEntity[] { ce });
            }
        }
        if (typeEvents != null) {
            for (final Long c : typeEvents) {
                boolean found = false;
                for (final ContractEvents ce2 : events) {
                    if (ce2.getID() == (int)(Object)c) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.addContractEvent(contractId, c, type);
                }
            }
        }
    }
    
    @Override
    public void save(final Contract contract) {
        contract.save();
    }
    
    public CustomFieldManager getCustomFieldManager() {
        return this.customFieldManager;
    }
    
    public void setCustomFieldManager(final CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
    
    public FieldManager getFieldManager() {
        return this.fieldManager;
    }
    
    public void setFieldManager(final FieldManager fieldManager) {
        this.fieldManager = fieldManager;
    }
    
    @Override
    public Integer countByContext(final Integer connectionId, final Long projectId, final String issueTypeId) {
        return this.getDao().count((Class)Contract.class, Query.select().where("CONNECTION_ID = ? AND PROJECT_ID = ? AND ISSUE_TYPE = ?", new Object[] { connectionId, projectId, issueTypeId }));
    }
    
    @Override
    public List<Contract> findByContext(final Integer connectionId, final Long projectId, final String issueTypeId) {
        final Contract[] contracts = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("CONNECTION_ID = ? AND PROJECT_ID = ? AND ISSUE_TYPE = ?", new Object[] { connectionId, projectId, issueTypeId }));
        return Arrays.asList(contracts);
    }
    
    @Override
    public List<Contract> findByName(final String contract) {
        final Contract[] contracts = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("CONTRACT_NAME like '%" + contract + "%' ", new Object[0]));
        return (contracts == null || contracts.length == 0) ? new ArrayList<Contract>() : Arrays.asList(contracts);
    }
    
    @Override
    public List<Contract> findByContractName(final String contract) {
        final Contract[] contracts = (Contract[])this.getDao().find((Class)Contract.class, Query.select().where("CONTRACT_NAME = ?", new Object[] { contract }));
        return (contracts == null || contracts.length == 0) ? new ArrayList<Contract>() : Arrays.asList(contracts);
    }
}
