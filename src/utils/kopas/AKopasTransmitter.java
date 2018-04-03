package utils.kopas;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class AKopasTransmitter {

    protected SSLContext sslctx;
    protected HttpsURLConnection con;

    protected static final String API_URL = "https://drua.co.kr/api/1/PLUS_KOPAS/";
    protected static final String API_KEY = "faaep4jd9a1s27hpw8ksvbqcshux0m";

    public enum PID{
        getStatus{
            @Override
            public String toString() {
                return "getStatus";
            }
        },
        doAction{
            @Override
            public String toString() {
                return "doAction";
            }
        };

        public abstract String toString();
    }

    public enum ACTION{
        ON{
            @Override
            public int toInteger() {
                return 1;
            }
        },
        OFF{
            @Override
            public int toInteger() {
                return 0;
            }
        },
        NONE{
            @Override
            public int toInteger() {
                return -1;
            }
        };
        public abstract int toInteger();
    }

    public AKopasTransmitter(){
        try {
            sslctx = SSLContext.getInstance("SSL");
            sslctx.init(null, new X509TrustManager[]{new KopasTransmitter.MyTrustManager()}, null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslctx.getSocketFactory());
            URL url = new URL(API_URL);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("X-drua-api-key", API_KEY);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String send(PID pid, String productNo, ACTION action){
        final StringBuffer stringBuffer = new StringBuffer();
        try {
            PrintStream ps = new PrintStream(con.getOutputStream());
            ps.println(String.format("pid=%s&pn=%s&action=%d&crlf=0", pid.toString(), productNo, action.toInteger()));
            ps.close();
            con.connect();
            if (con.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuffer.append(line);
                }
                br.close();
            }
            con.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    protected static class MyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
