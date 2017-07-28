// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.postfunction;

import java.util.TreeMap;
import com.intenso.jira.plugins.synchronizer.utils.FieldMappingUtils;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import java.util.HashMap;
import java.util.Map;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;

public class CopyFieldValueFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{
    public static final String FROM_FIELD = "source";
    public static final String TO_FIELD = "destination";
    private static final String FIELD_LIST = "fields";
    
    public Map<String, ?> getDescriptorParams(final Map<String, Object> formParams) {
        final Map params = new HashMap();
        params.put("source", this.extractSingleParam((Map)formParams, "source"));
        params.put("destination", this.extractSingleParam((Map)formParams, "destination"));
        return (Map<String, ?>)params;
    }
    
    protected void getVelocityParamsForEdit(final Map<String, Object> velocityParams, final AbstractDescriptor descriptor) {
        final Map args = ((FunctionDescriptor)descriptor).getArgs();
        this.getVelocityParamsForInput(velocityParams);
        velocityParams.put("source", args.get("source"));
        velocityParams.put("destination", args.get("destination"));
    }
    
    protected void getVelocityParamsForInput(final Map<String, Object> velocityParams) {
        final Map<String, String> fields = FieldMappingUtils.prepareFields();
        final Map<String, String> sortedFields = new TreeMap<String, String>();
        sortedFields.putAll(fields);
        velocityParams.put("fields", sortedFields);
    }
    
    protected void getVelocityParamsForView(final Map<String, Object> velocityParams, final AbstractDescriptor descriptor) {
        final Map args = ((FunctionDescriptor)descriptor).getArgs();
        velocityParams.put("source", args.get("source"));
        velocityParams.put("destination", args.get("destination"));
    }
}
