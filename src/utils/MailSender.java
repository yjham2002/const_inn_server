package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author EuiJin.Ham
 * @version 1.0.0
 * @description Utility Class for Sending emails with javax.mail and smtp prtc.
 */
public class MailSender {

    private Properties props;
    private Authenticator auth;
    private Session session;
    private String senderEmail;

    private static MailSender instance;
    private BlockingQueue<EmailUnit> queuedList = new LinkedBlockingQueue<>();

    private class EmailUnit{
        private List<String> recvs;
        private String title;
        private String contents;

        public EmailUnit(String title, String contents, List<String> recvs){
            this.recvs = recvs;
            this.title = title;
            this.contents = contents;
        }

        public EmailUnit(String title, String contents, String... recvs){
            this.recvs = Arrays.asList(recvs);
            this.title = title;
            this.contents = contents;
        }

        public EmailUnit(String recv, String title, String contents){
            this.recvs = new Vector<>();
            this.recvs.add(recv);
            this.title = title;
            this.contents = contents;
        }

        public void send(){
            instance._sendEmails(title, contents, recvs);
        }

        public List<String> getRecvs() {
            return recvs;
        }

        public void setRecvs(List<String> recvs) {
            this.recvs = recvs;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }
    }

    public static MailSender getInstance() throws NullPointerException{
        if(instance == null) throw new NullPointerException();
        return instance;
    }

    public static MailSender start(String senderEmail, String senderPassword, int threadPoolSize) throws Exception{
        if(instance == null) instance = new MailSender(senderEmail, senderPassword, threadPoolSize);
        else{
            Log.e("MailSender", "Email Address and threadPoolSize are already defined. The last Request was ignored.");
        }
        return instance;
    }

    private MailSender(String senderEmail, String senderPassword, int threads) throws Exception{
        Log.e(this.getClass().getSimpleName(), "Started to consume.");
        for(int e = 0; e < threads; e++){
            new Thread(()->{
                while(true){
                    try{
                        EmailUnit emailUnit = this.queuedList.take();
                        emailUnit.send();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }).start();
        }

        this.senderEmail = senderEmail;
        this.props = new Properties();
        this.props.setProperty("mail.transport.protocol", "smtp");
        this.props.setProperty("mail.host", "smtp.gmail.com");
        this.props.put("mail.smtp.auth", "true");
        this.props.put("mail.smtp.port", "465");
        this.props.put("mail.smtp.socketFactory.port", "465");
        this.props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.props.put("mail.smtp.socketFactory.fallback", "false");
        this.props.setProperty("mail.smtp.quitwait", "false");

        this.auth = new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        };

        this.session = Session.getDefaultInstance(this.props, this.auth);
    }

    private void _sendEmails(String title, String contents, String... recvEmails){
        _sendEmails(title, contents, Arrays.asList(recvEmails));
    }

    private void _sendEmails(String title, String contents, List<String> recvEmails){
        Log.i("MailSender", "Sending email to " + recvEmails.size() + " user(s).");
        for(String email : recvEmails){
            _send(email, title, contents);
        }
    }

    private void _send(String recvEmail, String title, String contents){
        Log.i("MailSender", "Sending email to " + recvEmail);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(this.senderEmail));
            message.setSubject(title);

            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recvEmail));

            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(contents);
            mp.addBodyPart(mbp1);

            message.setContent(mp);

            Transport.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendAnEmail(String recv, String title, String contents){
        try {
            this.queuedList.put(new EmailUnit(recv, title, contents));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void sendEmails(String title, String contents, String... recvs) {
        sendEmails(title, contents, Arrays.asList(recvs));
    }

    public void sendEmails(String title, String contents, List<String> recvs) {
        try {
            this.queuedList.put(new EmailUnit(title, contents, recvs));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
