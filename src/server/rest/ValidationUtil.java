package server.rest;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class ValidationUtil {

    public enum ValidationType{
        Email, Phone, Password
    }

    public static boolean validate(String value, ValidationType validationType){
        switch (validationType){
            case Email:
                return isValidEmailAddress(value);
            case Phone:
                return isValidPhoneNumber(value);
            default:
                return false;
        }
    }

    public static boolean isValidPhoneNumber(String phone){
        return phone.trim().replaceAll(" ", "").replaceAll("01[0-9]-?[0-9]{3,4}-?[0-9]{4}", "").length() == 0;
    }

    public static boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }

}
