package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

import java.util.Date;
import java.util.List;

public interface DiligenceMapper {

    List<DataMap> getDiligenceList(@Param("page") int page, @Param("limit") int limit, @Param("memberId") int memberId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<DataMap> getDiligenceCount(@Param("memberId") int memberId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<DataMap> getGatesByBeacon(@Param("beacon") String beacon, @Param("major") int major, @Param("minor") int minor);

    DataMap getGate(@Param("id") int id);

}
