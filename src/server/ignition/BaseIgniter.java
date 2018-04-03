package server.ignition;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import server.ignition.docs.APIDocument;
import server.rest.RestConstant;
import server.rest.RestUtil;
import spark.Route;
import spark.RouteGroup;
import spark.Service;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import utils.MailSender;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author EuiJin.Ham
 * @version 3.0.0
 * @apiNote A Base Class of Ignition classes making Server-Rest API Documents Page in HTML and JSON automatically
 * @usage http://localhost:PORT/help/html OR http://localhost:PORT/help/json
 * Facade Class For Igniting server instances
 */
public class BaseIgniter {

    protected static final int LOG_DEFAULT_LENGTH = 500;
    protected int logLength = LOG_DEFAULT_LENGTH;

    private String callSample;
    private String projectName;
    private String developer;
    private boolean debugMode;

    /**
     * Style CSS of the API Doc.
     */
    private static final String tableStyle="<style>" +
            "body {\n" +
            "  font-family:'Malgun Gothic';\n" +
            "  font-size:10pt;\n" +
            "}\n" +
            "table {\n" +
            "    border-collapse: separate;\n" +
            "    border-spacing: 1px;\n" +
            "    text-align: left;\n" +
            "    border: 1px solid #ccc;\n" +
            "}\n" +
            " th {\n" +
            "    padding: 3px;\n" +
            "    font-weight: bold;\n" +
            "    vertical-align: top;\n" +
            "    border: 1px solid #ccc;\n" +
            "    font-size:10pt;\n" +
            "    background: #efefef;\n" +
            "}\n" +
            " td {\n" +
            "    padding: 3px;\n" +
            "    vertical-align: top;\n" +
            "    font-size:10pt;\n" +
            "    border: 1px solid #ccc;\n" +
            "}</style><script src=\"//code.jquery.com/jquery-1.11.0.min.js\"></script><script>(function blink() { \n" +
            "  $(document).ready(function(){" +
            "$('.emp').css('font-weight', 'bold').fadeOut(500).fadeIn(500, blink); " +
            "});\n" +
            "})();</script>";

    protected ConcurrentHashMap<Service, List<APIDocument>> apiData;

    {
        apiData = new ConcurrentHashMap<>();
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new EmbeddedJettyFactory((i, j, k) -> {
            Server server = new Server();
            server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 5000000);
            return server;
        }));
    }

    private String apiToHTML(Service service){
        if(!apiData.containsKey(service)) return null;
        StringBuffer stringBuffer = new StringBuffer();

        List<APIDocument> apiDocuments = apiData.get(service);

        final String titleText = "Auto-Generated API Document";

        stringBuffer.append("<html><head>"+ tableStyle +"<meta http-equiv=\"refresh\" content=\"10;url=/help\"></head><body><h1>");
        if(projectName == null) stringBuffer.append(titleText);
        else stringBuffer.append(titleText + " [" + projectName + "]");
        stringBuffer.append("</h1>Option : <input type='button' onClick=\"location.href='/help/json'\" value='Show Json' />");
        stringBuffer.append("<br/><h3>Total APIs : " + apiDocuments.size());
        if(developer != null) stringBuffer.append(" · Developer : " + developer);
        if(callSample != null) stringBuffer.append(" · Address : " + callSample);
        stringBuffer.append("</h3></h1><table width=100% border=1>");

        if(debugMode) {
            for (int e = 0; e < apiDocuments.size(); e++) {
                stringBuffer.append(apiDocuments.get(e).toHtml(e + 1));
            }
        }else{
            stringBuffer.append("<tr><td>Server does not want to show the API Document.</td></tr>");
        }

        stringBuffer.append("</table></body></html>");

        return stringBuffer.toString();
    }

    private String apiToJson(Service service) throws IOException{
        if(!apiData.containsKey(service)) return null;
        List<APIDocument> apiDocuments = apiData.get(service);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(apiDocuments);
    }

    private void addToApiMap(Service service, String type, String restApi, String retType, String desc, String... params){
        APIDocument apiDocument = new APIDocument(restApi);
        apiDocument.setApiDescription(desc);
        apiDocument.setParameters(params);
        apiDocument.setType(type);
        apiDocument.setReturnType(retType);
        if(!apiData.containsKey(service)) {
            apiData.put(service, new Vector<>());
            bindApiPage(service);
        }
        apiData.get(service).add(apiDocument);
    }

    private void bindApiPage(Service service){
        service.get("/help", (req, res)->{
            res.type(RestConstant.RESPONSE_TYPE_HTML);
            return apiToHTML(service);
        });
        service.get("/help/:format", (req, res)->{
            String format = req.params(":format").toLowerCase();
            switch (format){
                case "html": {
                    res.type(RestConstant.RESPONSE_TYPE_HTML);
                    return apiToHTML(service);
                }
                default: {
                    res.type(RestConstant.RESPONSE_TYPE_JSON);
                    return apiToJson(service);
                }
            }
        });
    }

    protected void get(Service service, String restApi, Route route){
        get(service, restApi, route, "");
    }

    protected void post(Service service, String restApi, Route route){
        post(service, restApi, route, "");
    }

    protected void put(Service service, String restApi, Route route){
        put(service, restApi, route, "");
    }

    protected void delete(Service service, String restApi, Route route){
        delete(service, restApi, route, "");
    }

    protected void get(Service service, String restApi, Route route, String description, String... params){
        service.get(restApi, route, RestUtil::toJson);
        addToApiMap(service, "GET", restApi, "JSON", description, params);
    }

    protected void link(Service service, String restApi, Route route, String description, String... params){
        service.get(restApi, route);
        addToApiMap(service, "GET", restApi, "HTML", description, params);
    }

    protected void post(Service service, String restApi, Route route, String description, String... params){
        service.post(restApi, route, RestUtil::toJson);
        addToApiMap(service, "POST", restApi, "JSON", description, params);
    }

    protected void put(Service service, String restApi, Route route, String description, String... params){
        service.put(restApi, route, RestUtil::toJson);
        addToApiMap(service, "PUT", restApi, "JSON", description, params);
    }

    protected void delete(Service service, String restApi, Route route, String description, String... params){
        service.delete(restApi, route, RestUtil::toJson);
        addToApiMap(service, "DELETE", restApi, "JSON", description, params);
    }

    protected BaseIgniter(){

    }

    protected File activateExternalDirectory(Service service, String dirName){
        final String uploadDir = dirName;
        final File intricsicDir = new File(uploadDir);
        intricsicDir.mkdir();
        service.staticFiles.externalLocation(uploadDir);
        return intricsicDir;
    }

    public String getCallSample() {
        return callSample;
    }

    public void setCallSample(String callSample) {
        this.callSample = callSample;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
