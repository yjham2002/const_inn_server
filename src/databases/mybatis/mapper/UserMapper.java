package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

import java.util.List;

public interface UserMapper {

    void turnOnPush(@Param("id") int id);

    void turnOffPush(@Param("id") int id);

    void turnOnAlarm(@Param("id") int id);

    void turnOffAlarm(@Param("id") int id);

    void turnOnGesture(@Param("id") int id);

    void turnOffGesture(@Param("id") int id);

    DataMap getUser(@Param("id") String id, @Param("pw") String pw);

    DataMap getUserById(@Param("id") String id);

    DataMap getUserByApprovalCode(@Param("code") String code);

    DataMap getUserByApprovalCodeStateless(@Param("code") String code);

    void changeUserStatus(@Param("id") int id, @Param("status") int status);

    DataMap findEmail(@Param("name") String name, @Param("phone") String phone);

    List<DataMap> getListOfLogs(@Param("search") String search, @Param("page") int page, @Param("limit") int limit);

    Integer getCountOfLogs(@Param("search") String search);

    void joinUserMember(DataMap map);

    DataMap findPassword(DataMap map);

    void changeName(@Param("id") int id, @Param("name") String name);

    void changePhone(@Param("id") int id, @Param("phone") String phone);

    void changePassword(@Param("id") int id, @Param("pw") String pw);

    void initEmailApprovalCode(@Param("id") int id);

    DataMap getUserByKey(@Param("id") int id);

    void updateLastLoginDate(@Param("id") int id);

    List<DataMap> getWorkplaceList(@Param("id") int id);

    DataMap getWorkplaceDetail(@Param("id") int id);

    DataMap getWorkplaceAdmin(@Param("id") int id);

    DataMap getPrimaryWorkPlace(@Param("id") int id);

    Integer getAffiliationCount(@Param("memberId") int memberId, @Param("companyId") int companyId);

    void addWorkplace(DataMap map);

    String getApprovalCode(@Param("companyId") int companyId);

    void approveWorkplace(@Param("memberId") int memberId, @Param("companyId") int companyId);

    DataMap getUserCurrentDiligence(@Param("memberId") int memberId, @Param("companyId") int companyId);

    Integer countDoorLikes(@Param("memberId") int memberId);

    Integer countDoorGesture(@Param("memberId") int memberId);

    void gestureDoor(@Param("memberId") int memberId, @Param("gateId") int gateId);

    void undoGestureDoor(@Param("memberId") int memberId, @Param("gateId") int gateId);

    void likeDoor(@Param("memberId") int memberId, @Param("gateId") int gateId);

    void unlikeDoor(@Param("memberId") int memberId, @Param("gateId") int gateId);

    List<DataMap> getLikedDoorList(@Param("memberId") int memberId);

    List<DataMap> getDoorListOfCompany(@Param("companyId") int companyId);

    DataMap getDoorDetail(@Param("gateId") int gateId, @Param("memberId") int memberId);

    List<DataMap> getEntranceRange(@Param("gateId") int gateId);

    void initUser(DataMap map);

    void deleteWorkplace(@Param("id") int user, @Param("company") int company);

    DataMap getLatestDiligenceCompany(@Param("id") int user, @Param("company") int company);

    List<DataMap> getLatestDiligenceUser(@Param("id") int user);

    void insertDiligence(@Param("memberId") int memberId, @Param("gateId") int gateId, @Param("classifier") int classifier, @Param("type") int type);
}
