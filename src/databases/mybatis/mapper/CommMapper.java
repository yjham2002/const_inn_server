package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

import java.util.List;

public interface CommMapper {

    List<DataMap> getSidoList();

    List<DataMap> getGugunList(@Param("sidoID") int sidoID);

    List<DataMap> getCompanyList(@Param("page") int page, @Param("limit") int limit, @Param("search") String search, @Param("sido") int sido, @Param("gungu") int gungu);

    Integer getCompanyCount(@Param("search") String search, @Param("sido") int sido, @Param("gungu") int gungu);

    List<DataMap> getBoardList(@Param("page") int page, @Param("limit") int limit, @Param("search") String search, @Param("company") int company, @Param("mode") int mode);

    Integer getBoardCount(@Param("search") String search, @Param("company") int company, @Param("mode") int mode);

    DataMap getBoard(@Param("id") int id);

    DataMap getBesidesPrev(@Param("id") int id);

    DataMap getBesidesNext(@Param("id") int id);

    List<DataMap> getFiltedBeacon(@Param("list") String list);

}
