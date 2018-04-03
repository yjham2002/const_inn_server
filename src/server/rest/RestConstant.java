package server.rest;

/**
 * @author 함의진
 * @version 1.0.1
 * REST 운영을 위한 상수 정의 클래스
 * Jul-24-2017
 */
public class RestConstant {

    /**
     * Connection-related Constant
     */
    public static final int REST_SERVICE = 10040;
    public static final String UPLOAD_PATH = "./upload/";

    /**
     * Content-Type Headers
     */
    public static final String RESPONSE_TYPE_JSON = "application/json; charset=UTF-8";
    public static final String RESPONSE_TYPE_HTML = "text/html; charset=UTF-8";
    public static final String RESPONSE_TYPE_EXCEL = "application/vnd.ms-excel; charset=UTF-8; Content-Disposition: attachment; filename = apiDoc.xls";

    /**
     * A Method for generating Javascript-based source with simple HTML showing an alert message and the closing action
     * @param msg A Message to Show
     * @return Generated Source Code
     */
    public static String getJSAlertAndClose(String msg){
        return String.format("<html><script>self.opener = self; alert('%s'); window.close();</script></html>", msg);
    }

}
