// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.rest.mapper;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.rest.api.issue.IssueFields;
import java.util.Iterator;
import java.util.Collection;
import org.boon.Boon;
import org.apache.commons.lang3.StringEscapeUtils;
import com.atlassian.jira.project.version.Version;
import java.util.ArrayList;
import com.atlassian.jira.issue.Issue;
import java.util.List;

public class IssueAffectedVersionsMapper extends AbstractIssueFieldsMapper<List<String>> implements IssueFieldMapper<List<String>>
{
    @Override
    public String getFieldId() {
        return "versions";
    }
    
    @Override
    public String getIssueFieldObjectValue(final Issue issue) {
        final Collection<Version> versions = this.getValue(issue);
        final List<String> resources = new ArrayList<String>();
        for (final Version p : versions) {
            final List<String> resource = new ArrayList<String>();
            resource.add("\"" + StringEscapeUtils.escapeJson(p.getName()) + "\"");
            resource.add(Boon.toJson(p.getStartDate()));
            resource.add(Boon.toJson(p.getReleaseDate()));
            final String description = (p.getDescription() == null) ? "" : StringEscapeUtils.escapeJson(p.getDescription());
            resource.add("\"" + description + "\"");
            resources.add(resource.toString());
        }
        return resources.toString();
    }
    
    @Deprecated
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final List<String> resourcesString) {
        final List<ResourceRef> resources = new ArrayList<ResourceRef>();
        for (final String res : resourcesString) {
            resources.add(ResourceRef.withName(res));
        }
        return this.setValue(fields, resources);
    }
    
    @Override
    public IssueFields getIssueFields(final IssueFields fields, final Object value, final Long projectId) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: new             Ljava/util/ArrayList;
        //     3: dup            
        //     4: invokespecial   java/util/ArrayList.<init>:()V
        //     7: astore          resources
        //     9: aload_2         /* value */
        //    10: ifnull          238
        //    13: aload_2         /* value */
        //    14: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //    17: ldc             Ljava/util/List;.class
        //    19: invokestatic    org/boon/Boon.fromJsonArray:(Ljava/lang/String;Ljava/lang/Class;)Ljava/util/List;
        //    22: astore          resourcesString
        //    24: ldc             Lcom/atlassian/jira/project/version/VersionManager;.class
        //    26: invokestatic    com/atlassian/jira/component/ComponentAccessor.getComponent:(Ljava/lang/Class;)Ljava/lang/Object;
        //    29: checkcast       Lcom/atlassian/jira/project/version/VersionManager;
        //    32: astore          vm
        //    34: aload           resourcesString
        //    36: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //    41: astore          7
        //    43: aload           7
        //    45: invokeinterface java/util/Iterator.hasNext:()Z
        //    50: ifeq            238
        //    53: aload           7
        //    55: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    60: checkcast       Ljava/util/List;
        //    63: astore          res
        //    65: aload           vm
        //    67: aload_3         /* projectId */
        //    68: aload           res
        //    70: iconst_0       
        //    71: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //    76: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //    79: invokeinterface com/atlassian/jira/project/version/VersionManager.getVersion:(Ljava/lang/Long;Ljava/lang/String;)Lcom/atlassian/jira/project/version/Version;
        //    84: ifnonnull       213
        //    87: aload           res
        //    89: iconst_0       
        //    90: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //    95: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //    98: astore          name
        //   100: aconst_null    
        //   101: astore          start
        //   103: new             Ljava/util/Date;
        //   106: dup            
        //   107: aload           res
        //   109: iconst_1       
        //   110: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //   115: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //   118: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   121: invokespecial   java/util/Date.<init>:(J)V
        //   124: astore          start
        //   126: goto            136
        //   129: astore          e
        //   131: aload           e
        //   133: invokevirtual   java/lang/NumberFormatException.printStackTrace:()V
        //   136: aconst_null    
        //   137: astore          release
        //   139: new             Ljava/util/Date;
        //   142: dup            
        //   143: aload           res
        //   145: iconst_2       
        //   146: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //   151: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //   154: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   157: invokespecial   java/util/Date.<init>:(J)V
        //   160: astore          release
        //   162: goto            172
        //   165: astore          e
        //   167: aload           e
        //   169: invokevirtual   java/lang/NumberFormatException.printStackTrace:()V
        //   172: aload           res
        //   174: iconst_3       
        //   175: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //   180: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //   183: astore          description
        //   185: aload           vm
        //   187: aload           name
        //   189: aload           start
        //   191: aload           release
        //   193: aload           description
        //   195: aload_3         /* projectId */
        //   196: aconst_null    
        //   197: invokeinterface com/atlassian/jira/project/version/VersionManager.createVersion:(Ljava/lang/String;Ljava/util/Date;Ljava/util/Date;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;)Lcom/atlassian/jira/project/version/Version;
        //   202: pop            
        //   203: goto            213
        //   206: astore          e
        //   208: aload           e
        //   210: invokevirtual   com/atlassian/jira/exception/CreateException.printStackTrace:()V
        //   213: aload           resources
        //   215: aload           res
        //   217: iconst_0       
        //   218: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //   223: invokevirtual   java/lang/Object.toString:()Ljava/lang/String;
        //   226: invokestatic    com/atlassian/jira/rest/api/issue/ResourceRef.withName:(Ljava/lang/String;)Lcom/atlassian/jira/rest/api/issue/ResourceRef;
        //   229: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   234: pop            
        //   235: goto            43
        //   238: aload_0         /* this */
        //   239: aload_1         /* fields */
        //   240: aload           resources
        //   242: invokevirtual   com/intenso/jira/plugins/synchronizer/rest/mapper/IssueAffectedVersionsMapper.setValue:(Lcom/atlassian/jira/rest/api/issue/IssueFields;Ljava/util/List;)Lcom/atlassian/jira/rest/api/issue/IssueFields;
        //   245: areturn        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name             Signature
        //  -----  ------  ----  ---------------  -------------------------------------------------------------------------------
        //  131    5       11    e                Ljava/lang/NumberFormatException;
        //  167    5       12    e                Ljava/lang/NumberFormatException;
        //  100    103     9     name             Ljava/lang/String;
        //  103    100     10    start            Ljava/util/Date;
        //  139    64      11    release          Ljava/util/Date;
        //  185    18      12    description      Ljava/lang/String;
        //  208    5       9     e                Lcom/atlassian/jira/exception/CreateException;
        //  65     170     8     res              Ljava/util/List;
        //  24     214     5     resourcesString  Ljava/util/List;
        //  34     204     6     vm               Lcom/atlassian/jira/project/version/VersionManager;
        //  0      246     0     this             Lcom/intenso/jira/plugins/synchronizer/rest/mapper/IssueAffectedVersionsMapper;
        //  0      246     1     fields           Lcom/atlassian/jira/rest/api/issue/IssueFields;
        //  0      246     2     value            Ljava/lang/Object;
        //  0      246     3     projectId        Ljava/lang/Long;
        //  9      237     4     resources        Ljava/util/List;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name             Signature
        //  -----  ------  ----  ---------------  -----------------------------------------------------------------
        //  24     214     5     resourcesString  Ljava/util/List<Ljava/util/List;>;
        //  9      237     4     resources        Ljava/util/List<Lcom/atlassian/jira/rest/api/issue/ResourceRef;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                          
        //  -----  -----  -----  -----  ----------------------------------------------
        //  103    126    129    136    Ljava/lang/NumberFormatException;
        //  139    162    165    172    Ljava/lang/NumberFormatException;
        //  87     203    206    213    Lcom/atlassian/jira/exception/CreateException;
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:276)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:271)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:150)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:187)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:626)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:173)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:173)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:39)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:276)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2581)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:881)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:586)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:397)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:123)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public IssueFields getIssueFieldsObj(final IssueFields fields, final Object value) {
        final List<ResourceRef> resources = new ArrayList<ResourceRef>();
        if (value != null) {
            final List<String> resourcesString = Boon.fromJsonArray(value.toString(), String.class);
            for (final String res : resourcesString) {
                resources.add(ResourceRef.withName(res));
            }
        }
        return this.setValue(fields, resources);
    }
    
    protected IssueFields setValue(final IssueFields fields, final List<ResourceRef> resources) {
        return fields.versions((List)resources);
    }
    
    protected Collection<Version> getValue(final Issue issue) {
        return (Collection<Version>)issue.getAffectedVersions();
    }
}
