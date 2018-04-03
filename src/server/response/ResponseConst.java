package server.response;

/**
 * @author 함의진
 * @version 1.0.0
 * REST 호출에 대한 응답의 코드와 메시지를 정의하기 위한 클래스
 * Jul-21-2017
 */
public class ResponseConst {

    /**
     * CODE
     */
    public static final int CODE_FAILURE = -1;
    public static final int CODE_INVALID_PARAM = -2;
    public static final int CODE_INVALID_RANGE = -3;
    public static final int CODE_COULD_NOT_PARSE = -4;
    public static final int CODE_ALREADY_EXIST = -5;
    public static final int CODE_INVALID_LOCALE = -6;
    public static final int CODE_UNAUTHORIZED = -7;
    public static final int CODE_BLOCKED = -8;
    public static final int CODE_NO_PROPER_VALUE = -9;
    public static final int CODE_NO_PROPER_FORM = -10;
    public static final int CODE_NOT_EXISTING = -11;

    public static final int CODE_SUCCESS = 1;

    /**
     * Messages
     */
    public static final String MSG_INVALID_RANGE = "Invalid Range of value.";
    public static final String MSG_INVALID_PARAM = "You just have sent invalid parameter! Please Check if there is any problem with specs. :(";
    public static final String MSG_FAILURE = "Something went wrong! :(";
    public static final String MSG_PARSE_ERROR = "server.Server has failed to parse json data.";
    public static final String MSG_SUCCESS = "Request Processed Successfully. :)";
    public static final String MSG_ALREADY_EXIST = "User account you just requested is already exist. Please check if there is any problem. :(";
    public static final String MSG_SAVE_SUCC = "Saving succeeded.";
    public static final String MSG_SAVE_FAIL = "Saving failed.";
    public static final String MSG_NONE = "Process succeeded but unknown error occurred.";
    public static final String MSG_INVALID_LOCALE = "The locale string you were about to apply is not applicable for setting.";
    public static final String MSG_UNAUTHORIZED = "The access token you sent is not qualified to access. :(";
    public static final String MSG_BLOCKED = "The host you are using is blocked from server! :( Go away!";
    public static final String MSG_NO_PROPER_VALUE = "The value you used is not proper value for process. :X";
    public static final String MSG_NO_PROPER_FORM = "The file you used is not proper form for process. :X";
    public static final String MSG_NOT_EXISTING = "There is no tuple satisfying these conditions.";

}
