package databases.mybatis.mapper;

import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

public interface SqlMapper {

    class PureSqlProvider {
        public String sql(String sql) {
            return sql;
        }
        public String count(String from) {
            return "SELECT count(*) FROM " + from;
        }
    }

    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public List<?> select(String sql);

    @SelectProvider(type = PureSqlProvider.class, method = "count")
    public Integer count(String from);

    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public Integer execute(String query);
}