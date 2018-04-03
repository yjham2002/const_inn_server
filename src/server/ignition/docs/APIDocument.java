package server.ignition.docs;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class APIDocument implements Serializable{

    private String apiName;
    private String[] parameters;
    private String[] parameterTypes;
    private String apiDescription;
    private String type;
    private int[] returnCodes;
    private String returnType;
    private String[] returnDescriptions;

    public static final Comparator<APIDocument> apiComparator = (o1, o2) -> {
        return 0;
    };

    public String toHtml(int num){
        StringBuffer stringBuffer = new StringBuffer();
        String parametersStr =  Arrays.toString(parameters);
        if(parameters.length == 0) parametersStr = "<font color='red'>None</font>";
        stringBuffer.append("<tr><th style='background-color:lightgray'>No.</th><td style='background-color:lightgray'> ");
        if(apiName.indexOf(":") == -1) stringBuffer.append(num + "</td></tr><tr><th width=10%>API Path</th><td width=90%><a href='" + apiName + "'>" + apiName + "</a></td></tr>");
        else stringBuffer.append(num + "</td></tr><tr><th width=10%>API Path</th><td width=90%>" + apiName + " <b>[REST Parameter]</b></td></tr>");
        stringBuffer.append("<tr><th>API Description</th><td>" + apiDescription + "</td><tr>");
        stringBuffer.append("<tr><th>Call Type</th><td>" + type + "</td><tr>");
        stringBuffer.append("<tr><th>Parameters</th><td>" + parametersStr + "</td></tr>");
        stringBuffer.append("<tr><th>Return Type</th><td><font color=blue>" + returnType + "</font></td></tr>");
        return stringBuffer.toString();
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public APIDocument() {
    }

    public APIDocument(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String... parameters) {
        this.parameters = parameters;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String... parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription;
    }

    public int[] getReturnCodes() {
        return returnCodes;
    }

    public void setReturnCodes(int[] returnCodes) {
        this.returnCodes = returnCodes;
    }

    public String[] getReturnDescriptions() {
        return returnDescriptions;
    }

    public void setReturnDescriptions(String[] returnDescriptions) {
        this.returnDescriptions = returnDescriptions;
    }
}
