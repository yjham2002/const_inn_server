package databases.mybatis;


import java.io.IOException;
import java.io.Reader;

import databases.mybatis.config.Constants;
import databases.mybatis.wrapper.CloseableSession;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBSessionWrapper {

    private static SqlSessionFactory sessionFactory	= null;

    public DBSessionWrapper() {
        if(sessionFactory == null) {
            this.makeSessionFactory();
        }
    }

    private void makeSessionFactory() {
        String resource = Constants.DB_RESOURCE;
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(resource);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        sessionFactory = sqlSessionFactory;
    }

    public void closeSession(SqlSession session) {
        if(session != null) {
            session.close();
        }
    }

    public SqlSession getSession() {
        SqlSession session = sessionFactory.openSession();
        return new CloseableSession(session);
    }

}
