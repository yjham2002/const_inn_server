package server.ignition;

import databases.paginator.ListBox;
import delayed.managers.PushManager;
import server.comm.DataMap;
import server.comm.RestProcessor;
import server.response.Response;
import server.response.ResponseConst;
import server.rest.DataMapUtil;
import server.rest.RestConstant;
import server.rest.RestUtil;
import services.CommonSVC;
import services.UserSVC;
import spark.Service;
import spark.utils.IOUtils;
import utils.Log;
import utils.MailSender;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.List;

/**
 * @author 함의진
 * @version 2.0.0
 * 서버 실행을 위한 이그니션 클래스
 * @description (version 2.5.0) Response Transformer refactored with the lambda exp. and BaseIgniter applied
 * Jul-21-2017
 */
public class ServiceIgniter extends BaseIgniter{

    private Service service;

    private CommonSVC commonSVC;
    private UserSVC userSVC;

    /**
     * 서버 실행에 필요한 전처리 작업을 위한 init 파트
     * utils 패키지가 포함하는 유틸리티 싱글턴의 경우, 이곳에서 상수로서 값을 전달하고, 존재하거나 초기화되었을 경우에 한해 인스턴스를 반환하도록
     * 별도로 인스턴스 취득자를 구성하였다.
     */
    {
        commonSVC = new CommonSVC();
        userSVC = new UserSVC();
        try {
            MailSender.start("euijin.ham@richware.co.kr", "gpswpf12!", 20);
            PushManager.start("AAAAWeDYee8:APA91bF8xbiIZMJdMyTuF9CciacPhwEAzn7qFN3jGPKvKoRr1y_rlXthzZTT8MzHCG3l3LFti5lo-H3Rt6n7VcpddPr69N8sCSkEvTiARHvhl4f5zVqn5Yq9CVWN8vDW2UiC-5dFx_0C");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ServiceIgniter instance;

    public static ServiceIgniter getInstance() {
        if (instance == null) instance = new ServiceIgniter();
        return instance;
    }

    public void igniteServiceServer() {

        setProjectName("const_inn");
        setDeveloper("EuiJin.Ham");
        setCallSample("http://192.168.0.38:10040");
        setDebugMode(true);

        service = Service.ignite().port(RestConstant.REST_SERVICE);
        final File img_path = activateExternalDirectory(service, "img_upload");

        service.before((req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            Log.e("Connection", "Service Server [" + Calendar.getInstance().getTime().toString() + "] :: [" + req.pathInfo() + "] FROM [" + RestUtil.extractIp(req.raw()) + "] :: " + map);
            res.type(RestConstant.RESPONSE_TYPE_JSON);
        });

        super.get(service, "/system", (req, res) -> new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, System.getenv()), "서버 시스템 환경을 확인하기 위한 API 입니다.");

        super.post(service, "/imgUpload", (req, res) -> {
            try {
                final Path tempFile = Files.createTempFile(img_path.toPath(), "", "");
                req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
                try (InputStream input = req.raw().getPart("uploadImg").getInputStream()) {
                    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
                return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, tempFile.getFileName());
            }catch (Exception e){
                return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            }
        }, "이미지 업로드를 위한 API 입니다.", "uploadImg(multipart)");

        super.post(service, "/web/user/login", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());

            if(DataMapUtil.isValid(map, "email", "password")){
                final String id = map.getString("email");
                final String password = map.getString("password");

                DataMap user = userSVC.loginWeb(id, password);

                if(user == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
                else {
                    DataMapUtil.mask(user, "password");
                    return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, user);
                }
            }else{
                return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
            }
        }, "직원용 APP 로그인을 위한 API 입니다.", "email", "password");

        super.post(service, "/web/user/update/name/:id", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            final int id = Integer.parseInt(req.params(":id"));
            if(DataMapUtil.isValid(map, "name")) {
                final String newVal = map.getString("name");
                final DataMap retVal = userSVC.changeName(id, newVal);
                return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, retVal);
            }
            return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
        }, "사용자 이름 변경을 위한 API 입니다.", "id[REST]", "name");

        super.post(service, "/web/user/update/phone/:id", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            final int id = Integer.parseInt(req.params(":id"));
            if(DataMapUtil.isValid(map, "phone")) {
                final String newVal = map.getString("phone");
                final DataMap retVal = userSVC.changePhone(id, newVal);
                return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, retVal);
            }
            return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
        }, "사용자 휴대폰 번호 변경을 위한 API 입니다.", "id[REST]", "phone");

        super.post(service, "/web/user/update/password/:id", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            final int id = Integer.parseInt(req.params(":id"));
            if(DataMapUtil.isValid(map, "password")) {
                final String newPassword = map.getString("password");
                userSVC.changePassword(id, newPassword);
                return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS);
            }
            return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
        }, "사용자 비밀번호 변경을 위한 API 입니다.", "id[REST]", "password");

        super.post(service, "/web/user/push/on/:id", (req, res) -> {
            final int id = Integer.parseInt(req.params(":id"));
            DataMap map = userSVC.turnOnPush(id);
            if(map == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, map);
        }, "사용자 설정 - 푸시 수신 여부(수신)를 설정하기 위한 API 입니다.", "id[REST]");

        super.post(service, "/web/user/push/off/:id", (req, res) -> {
            final int id = Integer.parseInt(req.params(":id"));
            DataMap map = userSVC.turnOffPush(id);
            if(map == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, map);
        }, "사용자 설정 - 푸시 수신 여부(미수신)를 설정하기 위한 API 입니다.", "id[REST]");

        super.post(service, "/web/user/alarm/on/:id", (req, res) -> {
            final int id = Integer.parseInt(req.params(":id"));
            DataMap map = userSVC.turnOnAlarm(id);
            if(map == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, map);
        }, "사용자 설정 - 알람 수신 여부(수신)를 설정하기 위한 API 입니다.", "id[REST]");

        super.post(service, "/web/user/alarm/off/:id", (req, res) -> {
            final int id = Integer.parseInt(req.params(":id"));
            DataMap map = userSVC.turnOffAlarm(id);
            if(map == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, map);
        }, "사용자 설정 - 알람 수신 여부(미수신)를 설정하기 위한 API 입니다.", "id[REST]");

        super.post(service, "/web/user/gesture/on/:id", (req, res) -> {
            final int id = Integer.parseInt(req.params(":id"));
            DataMap map = userSVC.turnOnGesture(id);
            if(map == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, map);
        }, "사용자 설정 - 제스쳐 사용 여부(사용)를 설정하기 위한 API 입니다.", "id[REST]");

        super.post(service, "/web/user/gesture/off/:id", (req, res) -> {
            final int id = Integer.parseInt(req.params(":id"));
            DataMap map = userSVC.turnOffGesture(id);
            if(map == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, map);
        }, "사용자 설정 - 제스쳐 사용 여부(미사용)를 설정하기 위한 API 입니다.", "id[REST]");

        super.post(service, "/device/user/init", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());

            if(DataMapUtil.isValid(map, "memberId")){
                final int memberId = map.getInt("memberId");
                if(DataMapUtil.isValid(map, "deviceType", "regKey", "lastIp")){
                    userSVC.initUser(map);
                    return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, userSVC.getUserByKey(memberId));
                }else{
                    return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
                }
            }
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, "There is no valid member id. Ignoring this request. :(");
        }, "<p class='emp'>디바이스</p>앱 실행 시 마다 호출되는 시작 프로세스로 memberId가 없을 경우, 업데이트를 수행하지 않습니다. 정상 업데이트의 경우, 회원정보가 반환됩니다.", "memberId", "regKey", "deviceType", "lastIp");

        super.post(service, "/web/user/join", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            if(DataMapUtil.isValid(map, "email", "password", "name", "phone", "deviceType", "regKey", "lastIp")){
                final int retCode = userSVC.joinWeb(map, req.host());

                if(retCode == ResponseConst.CODE_SUCCESS) return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS);
                else if(retCode == ResponseConst.CODE_ALREADY_EXIST) return new Response(ResponseConst.CODE_ALREADY_EXIST, ResponseConst.MSG_ALREADY_EXIST);
                else return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
            }else{
                return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
            }
        }, "직원용 APP 회원가입을 위한 API입니다. <b>본 API를 통해 호출 시 대기회원으로 저장됩니다.</b>", "email", "password", "name", "phone", "deviceType", "regKey", "lastIp");

        super.get(service, "/web/user/find/email", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            if(DataMapUtil.isValid(map, "name", "phone")){
                final String name = map.getString("name");
                final String phone = map.getString("phone");
                DataMap retVal = userSVC.findEmail(name, phone);
                if(retVal == null) return new Response(ResponseConst.CODE_NOT_EXISTING, ResponseConst.MSG_NOT_EXISTING);
                return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, retVal);
            }else{
                return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
            }
        }, "직원용 APP 이메일 찾기를 위한 API이며, 패스워드가 마스킹된 회원 정보 전체가 반환됩니다.", "name", "phone");

        super.get(service, "/web/user/find/password", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            if(DataMapUtil.isValid(map, "name", "phone", "email")){

                boolean isExisting = userSVC.findPassword(map);
                if(isExisting) return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS);
                else return new Response(ResponseConst.CODE_NOT_EXISTING, ResponseConst.MSG_NOT_EXISTING);
            }else{
                return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
            }
        }, "직원용 APP 비밀번호 찾기를 위한 API입니다.", "email", "name", "phone");

        super.link(service, "/approval/:code", (req, res) -> {
            final String code = req.params(":code");
            boolean succ = userSVC.authEmailApprovalCode(code);
            res.type(RestConstant.RESPONSE_TYPE_HTML);
            if(succ) {
                return RestConstant.getJSAlertAndClose("정상적으로 처리되었습니다.");
            }
            else {
                return RestConstant.getJSAlertAndClose("잘못된 접근입니다.");
            }
        }, "이메일 인증 링크 접속 시 호출되는 REST API입니다. <b>웹에서 명시 호출하지 않습니다.</b>", "code[REST]");

        super.get(service, "/info/region", (req, res) -> {
            List<DataMap> retVal = commonSVC.getSidoList();
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, retVal);
        }, "시/도 목록을 취득하기 위한 API입니다.");

        super.get(service, "/info/region/:sidoID", (req, res) -> {
            final int sidoID = Integer.parseInt(req.params(":sidoID"));
            List<DataMap> retVal = commonSVC.getGugunList(sidoID);
            return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, retVal);
        }, "시/군/구 목록을 취득하기 위한 API입니다.", "sidoID[REST]");

    }

}
