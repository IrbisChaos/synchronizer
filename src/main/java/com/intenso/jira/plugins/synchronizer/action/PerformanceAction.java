// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action;

import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import com.intenso.jira.plugins.synchronizer.entity.Statistic;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import com.intenso.jira.plugins.synchronizer.entity.Contract;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;
import com.intenso.jira.plugins.synchronizer.entity.MessageType;
import com.intenso.jira.plugins.synchronizer.entity.QueueType;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.intenso.jira.plugins.synchronizer.service.StatisticService;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.ContractService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueArchiveService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueInService;
import com.intenso.jira.plugins.synchronizer.service.comm.QueueOutService;
import org.slf4j.Logger;

public class PerformanceAction extends LicenseAwareAction
{
    private static final Logger log;
    private static final long serialVersionUID = -7649223419029636247L;
    private Integer incomingErrorCount;
    private Integer outgoingErrorCount;
    private QueueOutService queueOutService;
    private QueueInService queueInService;
    private QueueArchiveService queueArchiveService;
    private ContractService contractService;
    private ConnectionService connectionService;
    private SynchronizerConfigService synchronizerConfigService;
    private StatisticService statisticService;
    private int[] jobsArray;
    private String[] filter;
    private Long msgSize;
    private Long attSize;
    private String type;
    private int zoomStart;
    private int avgProcessingTimeAvgMsgSizeZoom;
    private int msgCountMsgSizeZoom;
    private int msgSizeAttSizeZoom;
    private Long avgProcessingTime;
    private Long avgMsgSize;
    
    public PerformanceAction(final PluginLicenseManager pluginLicenseManager) {
        super(pluginLicenseManager);
        this.filter = new String[0];
        this.type = "Week";
        this.zoomStart = 7;
        this.avgProcessingTimeAvgMsgSizeZoom = 0;
        this.msgCountMsgSizeZoom = 0;
        this.msgSizeAttSizeZoom = 0;
        this.avgProcessingTime = 0L;
        this.avgMsgSize = 0L;
    }
    
    public boolean isPerformance() {
        return true;
    }
    
    @Override
    public String doDefault() throws Exception {
        this.filter = this.filterAll();
        return super.doDefault();
    }
    
    public String doFilter() {
        this.range();
        return "success";
    }
    
    protected QueueOutService getQueueOutService() {
        if (this.queueOutService == null) {
            this.queueOutService = (QueueOutService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueOutService.class);
        }
        return this.queueOutService;
    }
    
    protected QueueInService getQueueInService() {
        if (this.queueInService == null) {
            this.queueInService = (QueueInService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueInService.class);
        }
        return this.queueInService;
    }
    
    protected QueueArchiveService getQueueArchiveService() {
        if (this.queueArchiveService == null) {
            this.queueArchiveService = (QueueArchiveService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)QueueArchiveService.class);
        }
        return this.queueArchiveService;
    }
    
    protected ContractService getContractService() {
        if (this.contractService == null) {
            this.contractService = (ContractService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ContractService.class);
        }
        return this.contractService;
    }
    
    protected ConnectionService getConnectionService() {
        if (this.connectionService == null) {
            this.connectionService = (ConnectionService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)ConnectionService.class);
        }
        return this.connectionService;
    }
    
    protected SynchronizerConfigService getSynchronizerConfigService() {
        if (this.synchronizerConfigService == null) {
            this.synchronizerConfigService = (SynchronizerConfigService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)SynchronizerConfigService.class);
        }
        return this.synchronizerConfigService;
    }
    
    protected StatisticService getStatisticService() {
        if (this.statisticService == null) {
            this.statisticService = (StatisticService)ComponentAccessor.getOSGiComponentInstanceOfType((Class)StatisticService.class);
        }
        return this.statisticService;
    }
    
    private void generateData() {
        final QueueType[] t = { QueueType.IN, QueueType.OUT };
        final MessageType[] t2 = { MessageType.ATTACHMENT, MessageType.COMMENT, MessageType.CREATE, MessageType.UPDATE, MessageType.WORKFLOW };
        for (int y = 2016; y < 2017; ++y) {
            for (int m = 0; m < 11; ++m) {
                for (int d = 1; d < 31; ++d) {
                    final Random random = new Random();
                    for (int count = random.nextInt(96) + 5, i = 0; i < count; ++i) {
                        try {
                            final Integer connectionId = 1;
                            final Integer contractId = 1;
                            final MessageType msgtype = t2[random.nextInt(5)];
                            final QueueType queue = t[random.nextInt(2)];
                            final Long processingTime = (Long)Integer.valueOf(random.nextInt(1401) + 100);
                            final Integer msgSize = random.nextInt(571) + 30;
                            final Long attachmentSize = (Long)Integer.valueOf(random.nextInt(10501) + 500);
                            final Calendar cal = Calendar.getInstance();
                            cal.set(5, d);
                            cal.set(2, m);
                            cal.set(1, y);
                            cal.set(11, 0);
                            cal.set(12, 0);
                            cal.set(13, 0);
                            cal.set(14, 0);
                            final Calendar now = Calendar.getInstance();
                            now.set(11, 0);
                            now.set(12, 0);
                            now.set(13, 0);
                            now.set(14, 0);
                            now.add(5, 1);
                            if (cal.compareTo(now) > -1) {
                                return;
                            }
                            this.getStatisticService().create(new Timestamp(cal.getTime().getTime()), connectionId, contractId, msgtype, queue, processingTime, msgSize, attachmentSize);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    private String[] filterAll() {
        final List<String> result = new ArrayList<String>();
        final Map<List<Object>, List<List<Object>>> map = this.getConnections();
        for (final List<Object> connection : map.keySet()) {
            result.add("connection:" + connection.get(0));
            for (final List<Object> contract : map.get(connection)) {
                result.add("contract:" + contract.get(0));
            }
        }
        return result.toArray(new String[result.size()]);
    }
    
    private void range() {
        if (this.type.equals("Day")) {
            this.zoomStart = 1;
        }
        if (this.type.equals("Week")) {
            this.zoomStart = 7;
        }
        if (this.type.equals("Month")) {
            this.zoomStart = 30;
        }
        if (this.type.equals("Year")) {
            this.zoomStart = 365;
        }
    }
    
    public int getAllFilter() {
        return (this.filter.length == this.filterAll().length) ? 1 : 0;
    }
    
    private int filterContains(final String id) {
        int result = 0;
        for (final String s : this.filter) {
            if (s.equals(id)) {
                result = 1;
                break;
            }
        }
        return result;
    }
    
    public Map<List<Object>, List<List<Object>>> getConnections() {
        final Map<List<Object>, List<List<Object>>> result = new HashMap<List<Object>, List<List<Object>>>();
        final List<Connection> connections = this.getConnectionService().getAll();
        for (final Connection connection : connections) {
            final List<Object> key = new ArrayList<Object>();
            key.add(connection.getID());
            key.add(connection.getConnectionName());
            key.add(this.filterContains("connection:" + connection.getID()));
            result.put(key, new ArrayList<List<Object>>());
        }
        final List<Contract> contracts = this.getContractService().findAll();
        for (final Contract contract : contracts) {
            final Connection connection2 = this.getConnectionService().find(contract.getConnectionId());
            if (connection2 != null) {
                List<Object> key2 = null;
                for (final List<Object> k : result.keySet()) {
                    if (k.get(0).equals(connection2.getID())) {
                        key2 = k;
                        break;
                    }
                }
                if (key2 == null) {
                    continue;
                }
                final List<List<Object>> list = result.get(key2);
                if (list == null) {
                    continue;
                }
                final List<Object> value = new ArrayList<Object>();
                value.add(contract.getID());
                value.add(contract.getContractName());
                value.add(this.filterContains("contract:" + contract.getID()));
                list.add(value);
                result.put(key2, list);
            }
        }
        return result;
    }
    
    public String getMsgByType() {
        String result = "";
        final List<Statistic> all = this.getStatisticByFilter();
        final JSONObject create = new JSONObject();
        final JSONObject update = new JSONObject();
        final JSONObject comment = new JSONObject();
        final JSONObject attachment = new JSONObject();
        final JSONObject workflow = new JSONObject();
        final JSONArray array = new JSONArray();
        try {
            create.put("type", (Object)"Create");
            create.put("in", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.CREATE, QueueType.IN));
            create.put("out", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.CREATE, QueueType.OUT));
            update.put("type", (Object)"Update");
            update.put("in", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.UPDATE, QueueType.IN));
            update.put("out", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.UPDATE, QueueType.OUT));
            comment.put("type", (Object)"Comment");
            comment.put("in", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.COMMENT, QueueType.IN));
            comment.put("out", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.COMMENT, QueueType.OUT));
            attachment.put("type", (Object)"Attachment");
            attachment.put("in", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.ATTACHMENT, QueueType.IN));
            attachment.put("out", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.ATTACHMENT, QueueType.OUT));
            workflow.put("type", (Object)"Workflow");
            workflow.put("in", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.WORKFLOW, QueueType.IN));
            workflow.put("out", (Object)this.getStatisticCountByMsgTypeAndQueueType(all, MessageType.WORKFLOW, QueueType.OUT));
            array.put((Object)create);
            array.put((Object)update);
            array.put((Object)comment);
            array.put((Object)attachment);
            array.put((Object)workflow);
            result = array.toString();
        }
        catch (JSONException e) {
            PerformanceAction.log.error(e.getMessage());
        }
        return result;
    }
    
    private List<Statistic> getStatisticByFilter() {
        final List<Statistic> result = new ArrayList<Statistic>();
        final List<Statistic> all = this.getStatisticService().getAll();
        for (final Statistic statistic : all) {
            for (final String f : this.filter) {
                if (f.startsWith("connection:")) {
                    final String id = f.replace("connection:", "").trim();
                    final int i = Integer.parseInt(id);
                    if (statistic.getConnectionId() != null && i == statistic.getConnectionId()) {
                        result.add(statistic);
                        break;
                    }
                }
                else if (f.startsWith("contract:")) {
                    final String id = f.replace("contract:", "").trim();
                    final int i = Integer.parseInt(id);
                    if (statistic.getContractId() != null && i == statistic.getContractId()) {
                        result.add(statistic);
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    public String getAvgProcessingTimeAvgMsgSize() {
        String result = "";
        final JSONArray array = new JSONArray();
        final List<Object[]> timeSize = new ArrayList<Object[]>();
        final List<Statistic> all = this.getStatisticByFilter();
        this.avgMsgSize = 0L;
        this.avgProcessingTime = 0L;
        for (final Statistic s : all) {
            final Timestamp date = s.getDate();
            final Object[] object = { date, s.getMsgSize(), s.getProcessingTime() };
            this.avgMsgSize += (Long)s.getMsgSize();
            this.avgProcessingTime += s.getProcessingTime();
            boolean exist = false;
            for (final Object[] o : timeSize) {
                if (o[0].equals(date)) {
                    try {
                        o[1] = ((int)object[1] + (int)o[1]) / 2;
                        o[2] = ((long)object[2] + (long)o[2]) / 2L;
                        exist = true;
                        break;
                    }
                    catch (Exception e) {
                        PerformanceAction.log.error(e.getMessage());
                    }
                }
            }
            if (!exist) {
                timeSize.add(object);
            }
        }
        if (all.size() > 0) {
            this.avgMsgSize /= (Long)all.size();
            this.avgProcessingTime /= (Long)all.size();
        }
        this.avgProcessingTimeAvgMsgSizeZoom = timeSize.size() - 1;
        this.sort(timeSize);
        for (final Object[] o2 : timeSize) {
            final JSONObject object2 = new JSONObject();
            try {
                object2.put("date", (Object)new SimpleDateFormat("yyyy-MM-dd").format(o2[0]));
                object2.put("size", o2[1]);
                object2.put("time", o2[2]);
            }
            catch (JSONException e2) {
                PerformanceAction.log.error(e2.getMessage());
            }
            array.put((Object)object2);
        }
        result = array.toString();
        return result;
    }
    
    public String getMsgCountMsgSize() {
        String result = "";
        final JSONArray array = new JSONArray();
        final List<Object[]> countSize = new ArrayList<Object[]>();
        final List<Statistic> all = this.getStatisticByFilter();
        for (final Statistic s : all) {
            final Timestamp date = s.getDate();
            final Object[] object = { date, s.getMsgSize(), 1 };
            boolean exist = false;
            for (final Object[] o : countSize) {
                if (o[0].equals(date)) {
                    try {
                        o[1] = (int)object[1] + (int)o[1];
                        o[2] = (int)object[2] + (int)o[2];
                        exist = true;
                        break;
                    }
                    catch (Exception e) {
                        PerformanceAction.log.error(e.getMessage());
                    }
                }
            }
            if (!exist) {
                countSize.add(object);
            }
        }
        this.msgCountMsgSizeZoom = countSize.size() - 1;
        this.sort(countSize);
        for (final Object[] o2 : countSize) {
            final JSONObject object2 = new JSONObject();
            try {
                object2.put("date", (Object)new SimpleDateFormat("yyyy-MM-dd").format(o2[0]));
                object2.put("size", o2[1]);
                object2.put("count", o2[2]);
            }
            catch (JSONException e2) {
                PerformanceAction.log.error(e2.getMessage());
            }
            array.put((Object)object2);
        }
        result = array.toString();
        return result;
    }
    
    public String getMsgSizeAttSize() {
        String result = "";
        final JSONArray array = new JSONArray();
        final List<Object[]> size = new ArrayList<Object[]>();
        final List<Statistic> all = this.getStatisticByFilter();
        this.msgSize = 0L;
        this.attSize = 0L;
        for (final Statistic s : all) {
            final Timestamp date = s.getDate();
            final Timestamp date2 = this.getDateRange(this.getCurrentDateWithoutTime(), -(this.zoomStart + 1));
            final Timestamp date3 = this.getDateRange(this.getCurrentDateWithoutTime(), 1);
            final Object[] object = { date, s.getMsgSize(), s.getAttachmentSize() };
            if (s.getDate().after(date2) && s.getDate().before(date3)) {
                this.msgSize += (Long)s.getMsgSize();
                this.attSize += s.getAttachmentSize();
            }
            boolean exist = false;
            for (final Object[] o : size) {
                if (o[0].equals(date)) {
                    try {
                        o[1] = (int)object[1] + (int)o[1];
                        o[2] = (long)object[2] + (long)o[2];
                        exist = true;
                        break;
                    }
                    catch (Exception e) {
                        PerformanceAction.log.error(e.getMessage());
                    }
                }
            }
            if (!exist) {
                size.add(object);
            }
        }
        this.msgSizeAttSizeZoom = size.size() - 1;
        this.sort(size);
        for (final Object[] o2 : size) {
            final JSONObject object2 = new JSONObject();
            try {
                object2.put("date", (Object)new SimpleDateFormat("yyyy-MM-dd").format(o2[0]));
                object2.put("message", o2[1]);
                object2.put("attachment", o2[2]);
            }
            catch (JSONException e2) {
                PerformanceAction.log.error(e2.getMessage());
            }
            array.put((Object)object2);
        }
        result = array.toString();
        return result;
    }
    
    private void sort(final List<Object[]> list) {
        for (int i = 0; i < list.size() - 1; ++i) {
            for (int j = 1; j < list.size() - i; ++j) {
                if (((Timestamp)list.get(j - 1)[0]).compareTo((Timestamp)list.get(j)[0]) > 0) {
                    final Object[] temp = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }
    
    public String getProcessingTimeDeviation() {
        String result = "";
        final JSONArray array = new JSONArray();
        final JSONObject object = new JSONObject();
        final Timestamp date = this.getCurrentDateWithoutTime();
        final List<Statistic> all = this.getStatisticByFilter();
        Long time = 50L;
        try {
            Long now = 0L;
            int nowCount = 0;
            Long avg = 0L;
            for (final Statistic s : all) {
                if (s.getDate().equals(date)) {
                    now += s.getProcessingTime();
                    ++nowCount;
                }
                avg += s.getProcessingTime();
            }
            if (nowCount > 0) {
                now /= (Long)nowCount;
            }
            if (all.size() > 0) {
                avg /= (Long)all.size();
            }
            if (avg >= 0L) {
                time = (now - avg) * 50L / avg + 50L;
            }
            if (time > 100L) {
                time = 100L;
            }
            else if (time < 0L) {
                time = 0L;
            }
        }
        catch (Exception e) {
            PerformanceAction.log.error(e.getMessage());
        }
        try {
            object.append("value", (Object)time);
        }
        catch (JSONException e2) {
            PerformanceAction.log.error(e2.getMessage());
        }
        array.put((Object)object);
        result = array.toString();
        return result;
    }
    
    public String getAvgMsgSizeDeviation() {
        String result = "";
        final JSONArray array = new JSONArray();
        final JSONObject object = new JSONObject();
        final Timestamp date = this.getCurrentDateWithoutTime();
        final Timestamp date2 = this.getDateRange(date, -(this.zoomStart + 1));
        final Timestamp date3 = this.getDateRange(date, 1);
        final List<Statistic> all = this.getStatisticByFilter();
        Long size = 50L;
        try {
            Long now = 0L;
            int nowCount = 0;
            Long avg = 0L;
            for (final Statistic s : all) {
                if (s.getDate().after(date2) && s.getDate().before(date3)) {
                    now += (Long)s.getMsgSize();
                    ++nowCount;
                }
                avg += (Long)s.getMsgSize();
            }
            if (nowCount > 0) {
                now /= (Long)nowCount;
            }
            if (all.size() > 0) {
                avg /= (Long)all.size();
            }
            if (avg >= 0L) {
                size = (now - avg) * 50L / avg + 50L;
            }
            if (size > 100L) {
                size = 100L;
            }
            else if (size < 0L) {
                size = 0L;
            }
        }
        catch (Exception e) {
            PerformanceAction.log.error(e.getMessage());
        }
        try {
            object.append("value", (Object)size);
        }
        catch (JSONException e2) {
            PerformanceAction.log.error(e2.getMessage());
        }
        array.put((Object)object);
        result = array.toString();
        return result;
    }
    
    public String getMsgCountDeviation() {
        String result = "";
        final JSONArray array = new JSONArray();
        final JSONObject object = new JSONObject();
        final Timestamp date = this.getCurrentDateWithoutTime();
        final Timestamp date2 = this.getDateRange(date, -(this.zoomStart + 1));
        final Timestamp date3 = this.getDateRange(date, 1);
        final List<Statistic> all = this.getStatisticByFilter();
        Integer msgCount = 50;
        try {
            final Set<Timestamp> dates = new HashSet<Timestamp>();
            int now = 0;
            int sum = 0;
            for (final Statistic s : all) {
                if (s.getDate().after(date2) && s.getDate().before(date3)) {
                    ++now;
                }
                dates.add(s.getDate());
                ++sum;
            }
            if (sum >= 0) {
                msgCount = (now - sum) * 50 / sum + 50;
            }
            if (msgCount > 100) {
                msgCount = 100;
            }
            else if (msgCount < 0) {
                msgCount = 0;
            }
        }
        catch (Exception e) {
            PerformanceAction.log.error(e.getMessage());
        }
        try {
            object.append("value", (Object)msgCount);
        }
        catch (JSONException e2) {
            PerformanceAction.log.error(e2.getMessage());
        }
        array.put((Object)object);
        result = array.toString();
        return result;
    }
    
    public String getMsgSizeDeviation() {
        String result = "";
        final JSONArray array = new JSONArray();
        final JSONObject object = new JSONObject();
        final Timestamp date = this.getCurrentDateWithoutTime();
        final Timestamp date2 = this.getDateRange(date, -(this.zoomStart + 1));
        final Timestamp date3 = this.getDateRange(date, 1);
        final List<Statistic> all = this.getStatisticByFilter();
        Long size = 50L;
        try {
            Long now = 0L;
            Long sum = 0L;
            for (final Statistic s : all) {
                if (s.getDate().after(date2) && s.getDate().before(date3)) {
                    now += (Long)s.getMsgSize();
                }
                sum += (Long)s.getMsgSize();
            }
            if (sum >= 0L) {
                size = (now - sum) * 50L / sum + 50L;
            }
            if (size > 100L) {
                size = 100L;
            }
            else if (size < 0L) {
                size = 0L;
            }
        }
        catch (Exception e) {
            PerformanceAction.log.error(e.getMessage());
        }
        try {
            object.append("value", (Object)size);
        }
        catch (JSONException e2) {
            PerformanceAction.log.error(e2.getMessage());
        }
        array.put((Object)object);
        result = array.toString();
        return result;
    }
    
    public String getAttSizeDeviation() {
        String result = "";
        final JSONArray array = new JSONArray();
        final JSONObject object = new JSONObject();
        final Timestamp date = this.getCurrentDateWithoutTime();
        final Timestamp date2 = this.getDateRange(date, -(this.zoomStart + 1));
        final Timestamp date3 = this.getDateRange(date, 1);
        final List<Statistic> all = this.getStatisticByFilter();
        Long size = 50L;
        try {
            Long now = 0L;
            Long sum = 0L;
            for (final Statistic s : all) {
                if (s.getDate().after(date2) && s.getDate().before(date3)) {
                    now += s.getAttachmentSize();
                }
                sum += s.getAttachmentSize();
            }
            if (sum >= 0L) {
                size = (now - sum) * 50L / sum + 50L;
            }
            if (size > 100L) {
                size = 100L;
            }
            else if (size < 0L) {
                size = 0L;
            }
        }
        catch (Exception e) {
            PerformanceAction.log.error(e.getMessage());
        }
        try {
            object.append("value", (Object)size);
        }
        catch (JSONException e2) {
            PerformanceAction.log.error(e2.getMessage());
        }
        array.put((Object)object);
        result = array.toString();
        return result;
    }
    
    public String getMsgSizeAttSizeRate() {
        String result = "";
        final JSONObject msg = new JSONObject();
        final JSONObject att = new JSONObject();
        final JSONArray array = new JSONArray();
        try {
            msg.append("title", (Object)"Message");
            msg.append("value", (Object)this.msgSize);
            att.append("title", (Object)"Attachment");
            att.append("value", (Object)this.attSize);
            array.put((Object)msg);
            array.put((Object)att);
            result = array.toString();
        }
        catch (JSONException e) {
            PerformanceAction.log.error(e.getMessage());
        }
        return result;
    }
    
    private Timestamp getDateRange(final Timestamp date, final int i) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, i);
        return new Timestamp(cal.getTime().getTime());
    }
    
    private Timestamp getCurrentDateWithoutTime() {
        final Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return new Timestamp(cal.getTime().getTime());
    }
    
    public String getColorForNotFinishedStatuses() {
        return "#6E9ECF";
    }
    
    public String getColorForErrorStatus() {
        return "#DE593A";
    }
    
    public String getColorForDoneStatus() {
        return "#84b761";
    }
    
    private Integer getStatisticCountByMsgTypeAndQueueType(final List<Statistic> all, final MessageType type, final QueueType in) {
        Integer result = 0;
        for (final Statistic statistic : all) {
            if (statistic.getMessageType() != null && statistic.getInOut() != null && statistic.getMessageType().equals(type.ordinal()) && statistic.getInOut().equals(in.ordinal())) {
                ++result;
            }
        }
        return result;
    }
    
    public Integer getIncomingErrorCount() {
        return this.incomingErrorCount;
    }
    
    public void setIncomingErrorCount(final Integer incomingErrorCount) {
        this.incomingErrorCount = incomingErrorCount;
    }
    
    public Integer getOutgoingErrorCount() {
        return this.outgoingErrorCount;
    }
    
    public void setOutgoingErrorCount(final Integer outgoingErrorCount) {
        this.outgoingErrorCount = outgoingErrorCount;
    }
    
    public void setJobsArray(final int[] jobsArray) {
        this.jobsArray = jobsArray;
    }
    
    public int[] getJobsArray() {
        return this.jobsArray;
    }
    
    public String[] getFilter() {
        return this.filter;
    }
    
    public void setFilter(final String[] filter) {
        this.filter = filter;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public int getAvgProcessingTimeAvgMsgSizeZoom() {
        return this.avgProcessingTimeAvgMsgSizeZoom;
    }
    
    public void setAvgProcessingTimeAvgMsgSizeZoom(final int avgProcessingTimeAvgMsgSizeZoom) {
        this.avgProcessingTimeAvgMsgSizeZoom = avgProcessingTimeAvgMsgSizeZoom;
    }
    
    public int getMsgCountMsgSizeZoom() {
        return this.msgCountMsgSizeZoom;
    }
    
    public void setMsgCountMsgSizeZoom(final int msgCountMsgSizeZoom) {
        this.msgCountMsgSizeZoom = msgCountMsgSizeZoom;
    }
    
    public int getMsgSizeAttSizeZoom() {
        return this.msgSizeAttSizeZoom;
    }
    
    public void setMsgSizeAttSizeZoom(final int msgSizeAttSizeZoom) {
        this.msgSizeAttSizeZoom = msgSizeAttSizeZoom;
    }
    
    public Long getAvgProcessingTime() {
        return this.avgProcessingTime;
    }
    
    public void setAvgProcessingTime(final Long avgProcessingTime) {
        this.avgProcessingTime = avgProcessingTime;
    }
    
    public Long getAvgMsgSize() {
        return this.avgMsgSize;
    }
    
    public void setAvgMsgSize(final Long avgMsgSize) {
        this.avgMsgSize = avgMsgSize;
    }
    
    public int getZoomStart() {
        return this.zoomStart;
    }
    
    public void setZoomStart(final int zoomStart) {
        this.zoomStart = zoomStart;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)PerformanceAction.class);
    }
}
