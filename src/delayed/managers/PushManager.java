package delayed.managers;

import delayed.QueuedProcessorImpl;
import org.apache.commons.collections4.ListUtils;
import org.codehaus.jackson.map.ObjectMapper;
import server.comm.DataMap;
import utils.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

public class PushManager extends QueuedProcessorImpl{

    private String senderId;
    private static final int DEFAULT_POOL_SIZE = 20;
    private static final int MULTICAST_LIMIT_SIZE = 1000;

    public final static String API_URL = "https://fcm.googleapis.com/fcm/send";

    private static PushManager instance;

    public static PushManager getInstance(){
        if(instance == null) instance = new PushManager();
        return instance;
    }

    public static void start(String senderId){
        PushManager.getInstance().senderId = senderId;
        PushManager.getInstance().start(DEFAULT_POOL_SIZE);
    }

    private void _send(List<String> registrationKeys, String title, String message, DataMap extras){
        if(registrationKeys.size() == 0) return;
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + senderId);
            conn.setRequestProperty("Content-Type", "application/json");

            final ObjectMapper objectMapper = new ObjectMapper();

            final DataMap json = new DataMap();
            final DataMap info = new DataMap();

            info.put("title", title);
            info.put("body", message);

            json.put("notification", info);
            json.put("registration_ids", registrationKeys);

            json.put("data", extras);

            final String jsonString = objectMapper.writeValueAsString(json);
            Log.e(jsonString);

            Log.i(this.getClass().getSimpleName(), "Sending FCM to " + registrationKeys.size() + " user(s).");

            try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                wr.write(jsonString);
                wr.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

            final BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) Log.i(this.getClass().getSimpleName(), output);

            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send(List<String> registrationKeys, String title, String message, DataMap extras){
        for(final List<String> multicastUnit : ListUtils.partition(registrationKeys, MULTICAST_LIMIT_SIZE)) {
            super.offer(() -> _send(multicastUnit, title, message, extras));
        }
    }

    public static void main(String... args){
        PushManager.start("AAAAow6XPw8:APA91bHPWWLemYijEmDL-Qriu50TAJpVap44kvt8ufhOHkPa-lmNK67yzYm-lZalD1KTPf497BPMR2jswzPYDmw9K8edGdivWZQKah2DkuFdwAPmw46nKOyFgWaPra4Y4VWJXkGHldNE");
        final List<String> regKeys = new Vector<>();
        regKeys.add("ccgIyMKJ1Bo:APA91bGMZv0PYmc5Kemir8mwU3eDymtAkK2Wj6sBN_5617OP4qPruKT5DOUwsWz-4HZjWbFT8h4iU6RItT2W-VAkCseGgXQxA-HC6cdtWnLBf8pzvcLFpm4CWlUHmE8BjnpWO69x_b03");

        PushManager.getInstance().send(regKeys, "test", "contents", new DataMap());
    }

}
