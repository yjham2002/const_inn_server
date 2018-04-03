package services;

import com.sun.org.apache.xpath.internal.operations.Bool;
import databases.mybatis.mapper.CommMapper;
import databases.mybatis.mapper.UserMapper;
import databases.paginator.ListBox;
import databases.paginator.PageInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import server.comm.DataMap;
import server.response.Response;
import server.response.ResponseConst;
import server.rest.DataMapUtil;
import server.rest.RestUtil;
import server.rest.ValidationUtil;
import utils.Log;
import utils.MailSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserSVC extends BaseService {

    public DataMap checkPassword(String id, String pw){
        pw = RestUtil.getMessageDigest(pw);
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.getUser(id, pw);
        }
    }

    public DataMap getUserAppendableData(int id){
        final DataMap toAppend = new DataMap();

        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final List<DataMap> affiliation = userMapper.getWorkplaceList(id);
            int primaryCompany = -1;
            for(DataMap map : affiliation) if(map.getInt("isPrimary") == 1) primaryCompany = map.getInt("isPrimary");
            toAppend.put("affiliation", affiliation);
            toAppend.put("primary", getPrimaryWorkSpace(id));
            toAppend.put("current", primaryCompany != -1 ? userMapper.getUserCurrentDiligence(id, primaryCompany) : null);

        }

        return toAppend;
    }

    public DataMap loginWithApprovalToken(String id, String token){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap user = userMapper.getUserByApprovalCodeStateless(token);
            if(user != null) user = getUserByKey(user.getInt("id"));
            if(user != null) userMapper.updateLastLoginDate(user.getInt("id"));
            return user;
        }
    }

    public DataMap loginWeb(String id, String pw){
        pw = RestUtil.getMessageDigest(pw);
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap user = userMapper.getUser(id, pw);
            if(user != null) user = getUserByKey(user.getInt("id"));
            if(user != null) userMapper.updateLastLoginDate(user.getInt("id"));
            return user;
        }
    }

    public DataMap findEmail(String name, String phone){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap map = userMapper.findEmail(name, phone);
            if(map != null) map = getUserByKey(map.getInt("id"));
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public void changePassword(int id, String newPassword){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.changePassword(id, RestUtil.getMessageDigest(newPassword));
            sqlSession.commit();
        }
    }

    public DataMap addWorkplace(int memberId, int companyId, int permission){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int count = userMapper.getAffiliationCount(memberId, companyId);
            if(count > 0) return null;
            else{
                final DataMap param = new DataMap();
                param.put("memberId", memberId);
                param.put("companyId", companyId);
                param.put("permission", permission);
                userMapper.addWorkplace(param);
                sqlSession.commit();
                return getUserByKey(memberId);
            }
        }
    }

    public Boolean confirmWorkplaceToken(int memberId, int companyId, String token){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            String key = userMapper.getApprovalCode(companyId);

            if(key.equals(token)){
                userMapper.approveWorkplace(memberId, companyId);
                return true;
            }
            else return false;
        }
    }

    public int countDoorGesture(int memberId){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int enabledGusture = userMapper.countDoorGesture(memberId);
            return enabledGusture;
        }
    }

    public boolean gestureDoor(int memberId, int gateId){
        if(countDoorGesture(memberId) > 0) return false;

        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.gestureDoor(memberId, gateId);
            sqlSession.commit();
        }
        return true;
    }

    public boolean undoGestureDoor(int memberId, int gateId){
        if(countDoorGesture(memberId) == 0) return false;

        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.undoGestureDoor(memberId, gateId);
            sqlSession.commit();
        }
        return true;
    }

    public int countDoorLikes(int memberId) {
        try (SqlSession sqlSession = super.getSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int likes = userMapper.countDoorLikes(memberId);
            return likes;
        }
    }

    public boolean likeDoor(int memberId, int gateId){
        if(countDoorLikes(memberId) >= 10) return false;

        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.likeDoor(memberId, gateId);
            sqlSession.commit();
        }
        return true;
    }

    public boolean unlikeDoor(int memberId, int gateId){
        if(countDoorLikes(memberId) == 0) return false;

        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.unlikeDoor(memberId, gateId);
            sqlSession.commit();
        }
        return true;
    }

    public DataMap changeName(int id, String newName){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.changeName(id, newName);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public DataMap changePhone(int id, String newPhone){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.changePhone(id, newPhone.replaceAll("-", ""));
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public DataMap turnOnPush(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.turnOnPush(id);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public DataMap turnOffPush(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.turnOffPush(id);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public  DataMap turnOnAlarm(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.turnOnAlarm(id);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public DataMap turnOffAlarm(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.turnOffAlarm(id);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public DataMap turnOnGesture(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.turnOnGesture(id);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }

    public DataMap turnOffGesture(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.turnOffGesture(id);
            sqlSession.commit();

            final DataMap map = getUserByKey(id);
            DataMapUtil.mask(map, "password");
            return map;
        }
    }
    
    public boolean findPassword(DataMap params){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap map = userMapper.findPassword(params);
            if(map == null) return false;
            final String newPassword = RandomStringUtils.random(7, RestUtil.RANDOM_STRING_SET);
            userMapper.changePassword(map.getInt("id"), RestUtil.getMessageDigest(newPassword));
            MailSender.getInstance().sendAnEmail(map.getString("email"), "OTION 비밀번호 재발급 메일입니다.", "재발급 비밀번호 : " + newPassword);
            sqlSession.commit();
            return true;
        }
    }

    public int joinWeb(DataMap map, String currentHost){
        final String email = map.getString("email");
        final String password = RestUtil.getMessageDigest(map.getString("password"));
        final String phone = map.getString("phone").replaceAll("-", "");
        final String approvalCode = RandomStringUtils.random(32, RestUtil.RANDOM_STRING_SET);

        map.put("password", password);
        map.put("approvalCode", approvalCode);

        if(ValidationUtil.validate(email, ValidationUtil.ValidationType.Email) && ValidationUtil.validate(phone, ValidationUtil.ValidationType.Phone)){
            try(SqlSession sqlSession = super.getSession()){
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                final DataMap preProcessUser = userMapper.getUserById(email);
                if(preProcessUser != null) return ResponseConst.CODE_ALREADY_EXIST;
                userMapper.joinUserMember(map);
                sqlSession.commit();

                MailSender.getInstance().sendAnEmail(email, "OTION 가입 인증 메일입니다.", "다음 링크를 클릭하여 회원가입을 진행해주시기 바랍니다.\nhttp://" + currentHost + "/approval/" + approvalCode);
            }
            return ResponseConst.CODE_SUCCESS;
        }

        return ResponseConst.CODE_FAILURE;
    }

    public List<DataMap> getWorkplaces(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.getWorkplaceList(id);
        }
    }

    public DataMap getWorkplace(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.getWorkplaceDetail(id);
        }
    }

    public DataMap getWorkplaceAdmin(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.getWorkplaceAdmin(id);
        }
    }

    public DataMap getUserByKey(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap map = userMapper.getUserByKey(id);
            map.put("summatives",  getUserAppendableData(id));
            return map;
        }
    }

    public DataMap getPrimaryWorkSpace(int id){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap map = userMapper.getPrimaryWorkPlace(id);
            return map;
        }
    }

    public boolean authEmailApprovalCode(String code){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap user = userMapper.getUserByApprovalCode(code);
            if(user == null) return false;
            userMapper.changeUserStatus(user.getInt("id"), 1);
            sqlSession.commit();
        }
        return true;
    }

    public void initUser(DataMap map){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.initUser(map);
            sqlSession.commit();
        }
    }

    public List<DataMap> getGateList(int companyId, int memberId){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final List<DataMap> likedGates = userMapper.getLikedDoorList(memberId);
            final Set<Integer> likedNumbers = new HashSet<>();
            for(DataMap map : likedGates) likedNumbers.add(map.getInt("gateId"));
            List<DataMap> gates = userMapper.getDoorListOfCompany(companyId);
            for(DataMap map : gates) {
                map.put("liked", likedNumbers.contains(map.getInt("id")));
                map.put("gesture", likedNumbers.contains(map.getInt("id")));
            }
            return gates;
        }
    }

    public List<DataMap> getFavoredGateList(int memberId){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final List<DataMap> likedGates = userMapper.getLikedDoorList(memberId);
            return likedGates;
        }
    }

    public DataMap getGateDetail(int gateId, int memberId){
        try(SqlSession sqlSession = super.getSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            final DataMap gateInfo = userMapper.getDoorDetail(gateId, memberId);
            final List<DataMap> entranceRange = userMapper.getEntranceRange(gateId);

            final DataMap retVal = new DataMap();
            retVal.put("gateInfo", gateInfo);
            retVal.put("entranceRange", entranceRange);

            return retVal;
        }
    }

    public boolean deleteWorkplace(int user, int company){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.deleteWorkplace(user, company);
            sqlSession.commit();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public DataMap getLatestDiligenceCompany(int user, int company){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final DataMap retVal = userMapper.getLatestDiligenceCompany(user, company);
            return retVal;
        }
    }

    public DataMap getLatestDiligenceUser(int user){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            final List<DataMap> retVal = userMapper.getLatestDiligenceUser(user);
            DataMap latest = retVal.get(0);
            int prevType = -1;

            if(retVal.size() > 1) prevType = retVal.get(1).getInt("type");
            latest.put("prevType", prevType);
            return latest;
        }
    }

    public boolean manipulateDiligence(int memberId, int gateId, int classifier, int type){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insertDiligence(memberId, gateId, classifier, type);
            sqlSession.commit();
        }
        return true;
    }
}
