// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import java.text.ParseException;
import org.quartz.CronTrigger;
import javax.ws.rs.POST;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfig;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.google.gson.Gson;
import javax.ws.rs.core.Response;
import com.intenso.jira.plugins.synchronizer.config.SynchronizerConfigBuilder;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.intenso.jira.plugins.synchronizer.service.SynchronizerConfigService;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;

@Path("/configuration")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class ConfigurationResource
{
    private final SynchronizerConfigService synchronizerConfigService;
    private ExtendedLogger logger;
    
    public ConfigurationResource(final SynchronizerConfigService synchronizerConfigService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.synchronizerConfigService = synchronizerConfigService;
    }
    
    @POST
    @Path("/")
    public Response updateContractStatus(final SynchronizerConfigBuilder config) {
        final SynchronizerConfig configObject = config.build();
        if (configObject == null) {
            final Gson gson = new Gson();
            this.logger.error(ExtendedLoggerMessageType.CFG, "Unable to save configuration " + gson.toJson(config));
            return Response.serverError().status(500).entity((Object)config).build();
        }
        final Map<String, Boolean> validationResult = new HashMap<String, Boolean>();
        validationResult.put("Cron outgoing job", this.isValidCron(configObject.getCronOutgoingJob(), "OutgoingJOB"));
        validationResult.put("Cron outgoing response job", this.isValidCron(configObject.getCronOutgoingResponseJob(), "OutgoingRespJOB"));
        validationResult.put("Cron incoming job", this.isValidCron(configObject.getCronIncomingJob(), "IncomingJOB"));
        validationResult.put("Cron incoming job response", this.isValidCron(configObject.getCronIncomingResponseJob(), "IncomingResponseJOB"));
        validationResult.put("Cron pull job", this.isValidCron(configObject.getCronPullJob(), "PullJOB"));
        validationResult.put("Cron pull responses job", this.isValidCron(configObject.getCronPullResponsesJob(), "PullResponsesJOB"));
        validationResult.put("Cron pull configuration job", this.isValidCron(configObject.getCronPullConfigurationJob(), "PullConfigurationJOB"));
        boolean isValid = true;
        for (final Boolean b : validationResult.values()) {
            isValid &= b;
        }
        if (!isValid) {
            String errorText = "Invalid cron expression in: ";
            final List<String> errors = new ArrayList<String>();
            for (final String key : validationResult.keySet()) {
                if (!validationResult.get(key)) {
                    errors.add(key);
                }
            }
            errorText = errorText + StringUtils.join((Collection)errors, ", ") + ".";
            return Response.serverError().status(400).entity((Object)errorText).build();
        }
        this.synchronizerConfigService.saveConfig(configObject);
        return Response.ok((Object)config).build();
    }
    
    private Boolean isValidCron(final String cronExpression, final String jobName) {
        if (cronExpression != null && !cronExpression.isEmpty()) {
            final CronTrigger cronTrigger = new CronTrigger();
            try {
                cronTrigger.setCronExpression(cronExpression);
            }
            catch (ParseException e) {
                this.logger.error(ExtendedLoggerMessageType.CFG, "Validator - wrong cron expression: [" + cronExpression + "] for job: " + jobName);
                return false;
            }
        }
        return true;
    }
    
    public SynchronizerConfigService getSynchronizerConfigService() {
        return this.synchronizerConfigService;
    }
}
