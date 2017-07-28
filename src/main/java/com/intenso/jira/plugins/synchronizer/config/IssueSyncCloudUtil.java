// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.config;

import org.apache.commons.lang3.StringUtils;

public class IssueSyncCloudUtil
{
    private static final String PROD_CLOUD_SERVER_URL = "https://cloud.issuesync.com";
    private static final String ISSUE_SYNC_SECRET_TOKEN = "b96895041c41e176f05c9bd6dea13fb2";
    public static final String SECRET_TOKEN_HEADER = "IssueSyncToken";
    public static final String JIRA_REMOTE_URL_HEADER = "X-JIRA-SYNC-REMOTE-URL";
    private static final String CLOUD_SERVER_URL;
    
    public static String getCloudServerUrl() {
        return IssueSyncCloudUtil.CLOUD_SERVER_URL;
    }
    
    public static String getCloudServerUrl(final String path) {
        return getCloudServerUrl() + path;
    }
    
    public static String getSecretToken() {
        return "b96895041c41e176f05c9bd6dea13fb2";
    }
    
    public static boolean isProdUrl() {
        return getCloudServerUrl().equals("https://cloud.issuesync.com");
    }
    
    static {
        final String devUrl = System.getProperty("issuesync.cloudServerUrl");
        if (StringUtils.isNotBlank((CharSequence)devUrl)) {
            CLOUD_SERVER_URL = devUrl;
        }
        else {
            CLOUD_SERVER_URL = "https://cloud.issuesync.com";
        }
    }
}
