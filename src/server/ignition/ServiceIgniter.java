package server.ignition;

import delayed.managers.PushManager;
import server.comm.DataMap;
import server.comm.RestProcessor;
import server.response.Response;
import server.response.ResponseConst;
import server.rest.RestConstant;
import server.rest.RestUtil;
import services.CommonSVC;
import services.UserSVC;
import spark.Service;
import utils.Log;
import utils.MailSender;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
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
