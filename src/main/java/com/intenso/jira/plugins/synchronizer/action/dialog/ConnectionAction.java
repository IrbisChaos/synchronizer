// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.action.dialog;

import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import java.util.UUID;
import webwork.action.ResultException;
import com.intenso.jira.plugins.synchronizer.service.RemoteResponse;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerMessageType;
import com.intenso.jira.plugins.synchronizer.utils.CryptoUtils;
import com.intenso.jira.plugins.synchronizer.service.RemoteJiraType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.intenso.jira.plugins.synchronizer.entity.Connection;
import com.atlassian.jira.component.ComponentAccessor;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import com.intenso.jira.plugins.synchronizer.service.ConnectionService;
import com.intenso.jira.plugins.synchronizer.service.comm.RemoteAuthenticationKeyResult;
import com.atlassian.jira.util.I18nHelper;
import com.intenso.jira.plugins.synchronizer.service.ConfigurationJIRAClient;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLogger;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ConnectionAction extends JiraWebActionSupport
{
    private static final long serialVersionUID = -3351186607447849985L;
    private Integer id;
    private String connectionName;
    private String connectionLocalUser;
    private String connectionRemoteUrl;
    private String alternativeBaseUrl;
    private String localAuthKey;
    private String remoteAuthKey;
    private Integer passive;
    private String password;
    private String proxy;
    private Integer generateNewLocalAuthenticationKey;
    private Boolean unauthorizedStatus;
    private ExtendedLogger logger;
    private ConfigurationJIRAClient remoteJiraClient;
    private I18nHelper i18nHelper;
    private String testInformationError;
    private String testInformationSuccess;
    private String testInformationSuccessUnauthorizedStatus;
    private String testInformationWarning;
    private RemoteAuthenticationKeyResult testRemoteAuthenticationKeyResult;
    private ConnectionService connectionService;
    
    public ConnectionAction(final ConfigurationJIRAClient remoteJiraClient, final ConnectionService connectionService) {
        this.logger = ExtendedLoggerFactory.getLogger(this.getClass());
        this.i18nHelper = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
        this.remoteJiraClient = remoteJiraClient;
        this.setConnectionService(connectionService);
    }
    
    protected void doValidation() {
        super.doValidation();
        final Map<String, String> errors = (Map<String, String>)this.getErrors();
        if (this.connectionName == null || this.connectionName.isEmpty()) {
            errors.put("connectionName", this.getI18nHelper().getText("connection.empty.name"));
        }
        if (this.connectionLocalUser == null || this.connectionLocalUser.isEmpty()) {
            errors.put("connectionLocalUser", this.getI18nHelper().getText("connection.empty.connectionUsername"));
        }
        if (this.passive == null || !this.passive.equals(1)) {
            if (this.connectionRemoteUrl == null || this.connectionRemoteUrl.isEmpty()) {
                errors.put("connectionRemoteUrl", this.getI18nHelper().getText("connection.empty.connectionRemoteUrl"));
            }
            else if (this.connectionRemoteUrl != null && !this.connectionRemoteUrl.startsWith("http://") && !this.connectionRemoteUrl.startsWith("https://")) {
                errors.put("connectionRemoteUrl", this.getI18nHelper().getText("connection.invalid.connectionRemoteUrl"));
            }
            else if (this.connectionRemoteUrl.matches("/s")) {
                errors.put("connectionRemoteUrl", "Whitespaces not allowed in Remote Authentication Key");
            }
        }
        if (this.alternativeBaseUrl == null || this.alternativeBaseUrl.isEmpty()) {
            errors.put("alternativeBaseUrl", this.getI18nHelper().getText("connection.empty.alternative.baseurl"));
        }
        if (this.id != null && (this.localAuthKey == null || this.localAuthKey.isEmpty())) {
            errors.put("localAuthKey", this.getI18nHelper().getText("connection.empty.authKey"));
        }
        else {
            final List<Connection> connections = this.connectionService.findAllByAppKey(this.localAuthKey);
            final Connection editConnection = (this.id != null) ? this.connectionService.get(this.id) : null;
            if (connections != null && connections.size() > 0) {
                boolean existConnection = false;
                if (editConnection == null && connections.size() > 0) {
                    existConnection = true;
                }
                else {
                    for (final Connection c : connections) {
                        if (c.getID() != editConnection.getID()) {
                            existConnection = true;
                            break;
                        }
                    }
                }
                if (existConnection) {
                    errors.put("localAuthKey", this.getI18nHelper().getText("connection.unique.authKey"));
                }
            }
        }
    }
    
    private boolean validateConnection() {
        this.testInformationSuccess = "";
        this.testInformationWarning = "";
        this.testInformationError = "";
        this.testInformationSuccessUnauthorizedStatus = "";
        if (this.passive == null || this.passive == 0) {
            final RemoteResponse remoteResponse = this.remoteJiraClient.testRemoteConnection(this.connectionRemoteUrl, this.proxy, this.remoteAuthKey);
            final int type = remoteResponse.getResult();
            this.unauthorizedStatus = remoteResponse.getUnauthorizedStatus();
            if (type != RemoteJiraType.UNKNOWN.ordinal()) {
                if (this.unauthorizedStatus) {
                    this.testInformationSuccessUnauthorizedStatus = this.testInformationSuccessUnauthorizedStatus + this.getI18nHelper().getText("connection.ok", "Remote") + " Status code: 401. ";
                }
                else {
                    this.testInformationSuccess = this.testInformationSuccess + this.getI18nHelper().getText("connection.ok", "Remote") + " ";
                }
                boolean success = false;
                if (type == RemoteJiraType.SERVER.ordinal()) {
                    success = this.remoteJiraClient.testRemoteAuthenticationKey(this.connectionRemoteUrl, this.proxy, this.remoteAuthKey);
                }
                else if (type == RemoteJiraType.CLOUD.ordinal()) {
                    this.testRemoteAuthenticationKeyResult = this.remoteJiraClient.testRemoteAuthenticationKeyCloud(this.connectionRemoteUrl, this.proxy, this.remoteAuthKey);
                    success = this.testRemoteAuthenticationKeyResult.getKeyValid();
                }
                if (!success) {
                    this.testInformationError = this.testInformationError + this.getI18nHelper().getText("connection.authentication.error") + " ";
                }
            }
            else {
                this.testInformationError = this.testInformationError + this.getI18nHelper().getText("connection.error", "Remote") + " ";
                if (this.connectionRemoteUrl.startsWith("https://")) {
                    this.testInformationWarning = this.testInformationWarning + this.getI18nHelper().getText("connection.ssl", "<br /> For more info <a href='https://confluence.atlassian.com/jira/connecting-to-ssl-services-117455.html' target='_blank'>click</a>") + " ";
                }
            }
        }
        else {
            this.testInformationSuccess = this.testInformationSuccess + this.getI18nHelper().getText("connection.passive") + " ";
        }
        boolean success2 = this.remoteJiraClient.testLocalConnection(this.alternativeBaseUrl, this.connectionLocalUser, (this.password == null || this.password.isEmpty()) ? null : CryptoUtils.encrypt("com.intenso.jira.synchronizer", this.password));
        if (success2) {
            this.testInformationSuccess += this.getI18nHelper().getText("connection.ok", "Local");
        }
        else {
            final String localUrl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
            success2 = this.remoteJiraClient.testLocalConnection(localUrl, this.connectionLocalUser, (this.password == null || this.password.isEmpty()) ? null : CryptoUtils.encrypt("com.intenso.jira.synchronizer", this.password));
            if (success2) {
                this.testInformationError += this.getI18nHelper().getText("connection.local.baseurl.error", "Local");
            }
            else {
                this.testInformationError += this.getI18nHelper().getText("connection.error", "Local");
            }
        }
        this.logger.debug(ExtendedLoggerMessageType.CFG, "Test Connectivity. " + this.testInformationError + "__" + this.testInformationSuccess);
        return this.testInformationError.isEmpty();
    }
    
    public String doTest() throws ResultException {
        super.validate();
        this.validateConnection();
        return "input";
    }
    
    public String doInput() {
        Connection connection = null;
        if (this.id != null) {
            connection = this.connectionService.get(this.id);
            if (connection != null) {
                this.id = connection.getID();
                this.connectionName = connection.getConnectionName();
                this.connectionLocalUser = connection.getUsername();
                this.connectionRemoteUrl = connection.getRemoteJiraURL();
                this.alternativeBaseUrl = connection.getAlterBaseUrl();
                this.remoteAuthKey = connection.getRemoteAuthKey();
                this.localAuthKey = connection.getLocalAuthKey();
                this.passive = connection.getPassive();
                this.password = CryptoUtils.decrypt("com.intenso.jira.synchronizer", connection.getPassword());
                this.proxy = connection.getProxy();
            }
        }
        return "input";
    }
    
    @RequiresXsrfCheck
    public String doExecute() {
        final int remoteJiraType = this.getRemoteJiraType(this.proxy);
        if ((this.passive == null || this.passive == 0) && remoteJiraType == RemoteJiraType.UNKNOWN.ordinal()) {
            this.validateConnection();
            return "input";
        }
        final Connection connection = this.connectionService.get(this.id);
        if (connection == null) {
            this.localAuthKey = UUID.randomUUID().toString();
            this.connectionService.saveConnection(this.connectionName, this.connectionLocalUser, this.localAuthKey, this.remoteAuthKey, this.connectionRemoteUrl, this.passive, (this.password == null || this.password.isEmpty()) ? null : CryptoUtils.encrypt("com.intenso.jira.synchronizer", this.password), this.alternativeBaseUrl, remoteJiraType, this.proxy);
        }
        else {
            connection.setUsername(this.connectionLocalUser);
            connection.setRemoteJiraURL(this.connectionRemoteUrl);
            if (this.generateNewLocalAuthenticationKey != null && this.generateNewLocalAuthenticationKey == 1) {
                this.localAuthKey = UUID.randomUUID().toString();
            }
            connection.setLocalAuthKey(this.localAuthKey);
            connection.setRemoteAuthKey(this.remoteAuthKey);
            connection.setPassive(this.passive);
            connection.setPassword((this.password == null || this.password.isEmpty()) ? null : CryptoUtils.encrypt("com.intenso.jira.synchronizer", this.password));
            connection.setAlterBaseUrl(this.alternativeBaseUrl);
            connection.setRemoteJiraType(remoteJiraType);
            connection.setProxy(this.proxy);
            this.connectionService.update(connection);
        }
        return this.returnCompleteWithInlineRedirect("/secure/admin/ConnectionConfigAction.jspa");
    }
    
    private int getRemoteJiraType(final String proxy) {
        int type = RemoteJiraType.UNKNOWN.ordinal();
        if (this.passive == null || this.passive == 0) {
            final RemoteResponse remoteResponse = this.remoteJiraClient.testRemoteConnection(this.connectionRemoteUrl, proxy, this.remoteAuthKey);
            type = remoteResponse.getResult();
            this.unauthorizedStatus = remoteResponse.getUnauthorizedStatus();
        }
        return type;
    }
    
    public String getBaseURL() {
        return ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
    }
    
    public String getConnectionName() {
        return this.connectionName;
    }
    
    public void setConnectionName(final String connectionName) {
        this.connectionName = connectionName;
    }
    
    public String getLocalAuthKey() {
        return this.localAuthKey;
    }
    
    public void setLocalAuthKey(final String localAuthKey) {
        this.localAuthKey = localAuthKey;
    }
    
    public String getRemoteAuthKey() {
        return this.remoteAuthKey;
    }
    
    public void setRemoteAuthKey(final String remoteAuthKey) {
        this.remoteAuthKey = remoteAuthKey;
    }
    
    public ConfigurationJIRAClient getRemoteJiraClient() {
        return this.remoteJiraClient;
    }
    
    public void setRemoteJiraClient(final ConfigurationJIRAClient remoteJiraClient) {
        this.remoteJiraClient = remoteJiraClient;
    }
    
    public I18nHelper getI18nHelper() {
        return this.i18nHelper;
    }
    
    public void setI18nHelper(final I18nHelper i18nHelper) {
        this.i18nHelper = i18nHelper;
    }
    
    public static long getSerialversionuid() {
        return -3351186607447849985L;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getTestInformationError() {
        return this.testInformationError;
    }
    
    public void setTestInformationError(final String testInformationError) {
        this.testInformationError = testInformationError;
    }
    
    public String getTestInformationSuccess() {
        return this.testInformationSuccess;
    }
    
    public void setTestInformationSuccess(final String testInformationSuccess) {
        this.testInformationSuccess = testInformationSuccess;
    }
    
    public String getTestInformationSuccessUnauthorizedStatus() {
        return this.testInformationSuccessUnauthorizedStatus;
    }
    
    public void setTestInformationSuccessUnauthorizedStatus(final String testInformationSuccessUnauthorizedStatus) {
        this.testInformationSuccessUnauthorizedStatus = testInformationSuccessUnauthorizedStatus;
    }
    
    public String getTestInformationWarning() {
        return this.testInformationWarning;
    }
    
    public void setTestInformationWarning(final String testInformationWarning) {
        this.testInformationWarning = testInformationWarning;
    }
    
    public ConnectionService getConnectionService() {
        return this.connectionService;
    }
    
    public void setConnectionService(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }
    
    public String getConnectionLocalUser() {
        return this.connectionLocalUser;
    }
    
    public void setConnectionLocalUser(final String connectionLocalUser) {
        this.connectionLocalUser = connectionLocalUser;
    }
    
    public String getConnectionRemoteUrl() {
        return this.connectionRemoteUrl;
    }
    
    public void setConnectionRemoteUrl(String connectionRemoteUrl) {
        if (connectionRemoteUrl.endsWith("/")) {
            connectionRemoteUrl = connectionRemoteUrl.substring(0, connectionRemoteUrl.length() - 1);
        }
        this.connectionRemoteUrl = connectionRemoteUrl;
    }
    
    public Integer getPassive() {
        return this.passive;
    }
    
    public void setPassive(final Integer passive) {
        this.passive = passive;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public ExtendedLogger getLogger() {
        return this.logger;
    }
    
    public String getAlternativeBaseUrl() {
        if (this.alternativeBaseUrl == null || this.alternativeBaseUrl.isEmpty()) {
            this.alternativeBaseUrl = this.getBaseURL();
        }
        return this.alternativeBaseUrl;
    }
    
    public void setAlternativeBaseUrl(String alternativeBaseUrl) {
        if (alternativeBaseUrl.endsWith("/")) {
            alternativeBaseUrl = alternativeBaseUrl.substring(0, alternativeBaseUrl.length() - 1);
        }
        this.alternativeBaseUrl = alternativeBaseUrl;
    }
    
    public String getProxy() {
        return this.proxy;
    }
    
    public void setProxy(final String proxy) {
        this.proxy = proxy;
    }
    
    public RemoteAuthenticationKeyResult getTestRemoteAuthenticationKeyResult() {
        return this.testRemoteAuthenticationKeyResult;
    }
    
    public Integer getGenerateNewLocalAuthenticationKey() {
        return this.generateNewLocalAuthenticationKey;
    }
    
    public void setGenerateNewLocalAuthenticationKey(final Integer generateNewLocalAuthenticationKey) {
        this.generateNewLocalAuthenticationKey = generateNewLocalAuthenticationKey;
    }
    
    public Boolean getUnauthorizedStatus() {
        return this.unauthorizedStatus;
    }
    
    public void setUnauthorizedStatus(final Boolean unauthorizedStatus) {
        this.unauthorizedStatus = unauthorizedStatus;
    }
}
