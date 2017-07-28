// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest;

import javax.ws.rs.GET;
import java.io.IOException;
import com.intenso.jira.plugins.synchronizer.utils.ExtendedLoggerFactory;
import java.util.Stack;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.intenso.jira.plugins.synchronizer.utils.ReverseLineInputStream;
import java.io.File;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;

@Path("/")
public class LogsResource
{
    @GET
    @Path("logs")
    public Response getConnection() throws IOException {
        String result = "";
        final String home = ((JiraHome)ComponentAccessor.getComponentOfType((Class)JiraHome.class)).getLocalHomePath();
        final File file = new File(home + File.separator + "log" + File.separator + "intenso-synchronizer.log");
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(file)));
            int index = 0;
            final Stack<String> stringStack = new Stack<String>();
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final String line = br.readLine();
                if (line == null || index++ >= 300) {
                    break;
                }
                stringStack.push(ExtendedLoggerFactory.decorateLine(line));
            }
            int i = 0;
            while (!stringStack.isEmpty() && i++ < 300) {
                sb.append(stringStack.pop());
                sb.append("linebreak");
            }
            result = sb.toString();
        }
        catch (IOException e) {
            return Response.status(404).entity((Object)e.getMessage()).build();
        }
        return Response.ok((Object)result).build();
    }
}
