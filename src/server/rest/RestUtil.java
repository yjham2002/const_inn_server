package server.rest;

import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RestUtil {

    public static final String RANDOM_STRING_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String toJson(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        String retVal = null;
        try {
            retVal = objectMapper.writeValueAsString(object);
        }catch (IOException e){
            e.printStackTrace();
        }
        return retVal;
    }

    public static String getMessageDigest(String str){
        String MD5 = "";
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }

    public static String extractIp(HttpServletRequest req){
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        return ip;
    }

}
