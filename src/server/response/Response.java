package server.response;

import org.codehaus.jackson.map.ObjectMapper;
import utils.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 함의진
 * @version 1.0.0
 * REST 호출에 대한 응답을 수행하기 위한 클래스로 JSON 응답을 기반으로 함
 * Jul-21-2017
 */
public class Response {

    public static final int LOG_LENGTH = 500;

    private int returnCode;
    private String returnMessage;
    private Object data;

    public Response(){

    }

    public Response(int returnCode, String returnMessage) {
        this.returnCode = returnCode;
        this.returnMessage = returnMessage;
        Log.i(getTime() + " Response Generated [" + this.returnCode + "/" + this.returnMessage + "]");
    }

    public Response(int code, String message, Object object){
        this();
        this.returnCode = code;
        this.returnMessage = message;
        this.data = object;

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(this.data);
            if(json.length() > LOG_LENGTH) json = json.substring(0, LOG_LENGTH);
            Log.i(getTime() + " Response Generated [" + this.returnCode + "/" + this.returnMessage + "/ Data Attached : " + json + " ]");
        }catch (IOException e){
            Log.i(getTime() + " Response Generated [" + this.returnCode + "/" + this.returnMessage + "/ Data Attached :" + (this.data != null) + "]");
        }
    }

    public static String response(int code, String message, Object object) throws IOException {
        Response response = new Response(code, message, object);
        String res = new ObjectMapper().writeValueAsString(response);

        return res;
    }

    public static String response(int code, String message) throws IOException{
        return response(code, message, null);
    }

    public static String response(int code) throws IOException{
        return response(code, null);
    }

    public static String getTime(){
        SimpleDateFormat fmt = new SimpleDateFormat("[yyyy-MM-d hh:mm:ss]");
        return fmt.format(new Date());
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
