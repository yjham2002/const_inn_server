package delayed.managers;

import delayed.IJob;
import delayed.QueuedProcessorImpl;
import server.whois.SMSService;

public class SMSManager extends QueuedProcessorImpl{

    private static SMSManager instance;
    private SMSService smsService;

    public static SMSManager getInstance(SMSService smsService){
        if(instance == null) instance = new SMSManager();
        return instance;
    }

    public void send(String from, String to, String message){
        this.offer(()->{
            smsService.sendSMS(to, from, message);
        });
    }

}
