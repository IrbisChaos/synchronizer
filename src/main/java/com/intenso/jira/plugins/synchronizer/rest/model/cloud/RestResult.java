// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.model.cloud;

import java.util.Collections;
import java.util.List;

public class RestResult
{
    private Object dataObject;
    private List<String> errorMessages;
    private Boolean hasErrors;
    private Boolean succeeded;
    private Class<? extends Exception> errorClass;
    private List<FieldErrorDTO> fieldErrors;
    
    private RestResult() {
    }
    
    private RestResult(final Object dataObject, final List<String> errorMessages, final List<FieldErrorDTO> fieldErrors, final Boolean hasErrors, final Boolean succeeded, final Class<? extends Exception> errorClass) {
        this.dataObject = dataObject;
        this.errorMessages = errorMessages;
        this.hasErrors = hasErrors;
        this.succeeded = succeeded;
        this.fieldErrors = fieldErrors;
        this.errorClass = errorClass;
    }
    
    public static RestResult createSuccess() {
        return new RestResult(null, Collections.emptyList(), Collections.emptyList(), false, true, null);
    }
    
    public static RestResult createSuccess(final Object dataObject) {
        return new RestResult(dataObject, Collections.emptyList(), Collections.emptyList(), false, true, null);
    }
    
    public static RestResult createError(final List<FieldErrorDTO> fieldErrors) {
        return new RestResult(null, Collections.emptyList(), fieldErrors, true, false, null);
    }
    
    public static RestResult createErrorWithMessages(final List<String> errorMessages, final Class<? extends Exception> errorClass) {
        return new RestResult(null, errorMessages, null, true, false, errorClass);
    }
    
    public Object getDataObject() {
        return this.dataObject;
    }
    
    public void setDataObject(final Object dataObject) {
        this.dataObject = dataObject;
    }
    
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public void setErrorMessages(final List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    public Boolean getHasErrors() {
        return this.hasErrors;
    }
    
    public void setHasErrors(final Boolean hasErrors) {
        this.hasErrors = hasErrors;
    }
    
    public Boolean getSucceeded() {
        return this.succeeded;
    }
    
    public void setSucceeded(final Boolean succeeded) {
        this.succeeded = succeeded;
    }
    
    public List<FieldErrorDTO> getFieldErrors() {
        return this.fieldErrors;
    }
    
    public void setFieldErrors(final List<FieldErrorDTO> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    public Class<? extends Exception> getErrorClass() {
        return this.errorClass;
    }
}
