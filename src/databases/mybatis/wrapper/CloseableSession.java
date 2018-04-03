package databases.mybatis.wrapper;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author EuiJin.Ham
 * @version 1.0.0
 * @description Try-with-Resources 블럭에 이용하기 위해 SqlSession을 랩핑한 클래스
 */
public class CloseableSession implements SqlSession, Closeable{

    private SqlSession session;

    public CloseableSession(SqlSession sqlSession){
        this.session = sqlSession;
    }

    @Override
    public <T> T selectOne(String s) {
        return this.session.selectOne(s);
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return this.session.selectOne(s, o);
    }

    @Override
    public <E> List<E> selectList(String s) {
        return this.session.selectList(s);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return this.session.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return this.session.selectList(s, o, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return this.session.selectMap(s, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return this.session.selectMap(s, o, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return this.session.selectMap(s, o, s1, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s) {
        return this.session.selectCursor(s);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o) {
        return this.session.selectCursor(s, o);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
        return this.session.selectCursor(s, o, rowBounds);
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {
        this.session.select(s, o, resultHandler);
    }

    @Override
    public void select(String s, ResultHandler resultHandler) {
        this.session.select(s, resultHandler);
    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
        this.session.select(s, o, rowBounds, resultHandler);
    }

    @Override
    public int insert(String s) {
        return this.session.insert(s);
    }

    @Override
    public int insert(String s, Object o) {
        return this.session.insert(s, o);
    }

    @Override
    public int update(String s) {
        return this.session.update(s);
    }

    @Override
    public int update(String s, Object o) {
        return this.session.update(s, o);
    }

    @Override
    public int delete(String s) {
        return this.session.delete(s);
    }

    @Override
    public int delete(String s, Object o) {
        return this.session.delete(s, o);
    }

    @Override
    public void commit() {
        this.session.commit();
    }

    @Override
    public void commit(boolean b) {
        this.session.commit(b);
    }

    @Override
    public void rollback() {
        this.session.rollback();
    }

    @Override
    public void rollback(boolean b) {
        this.session.rollback(b);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return this.session.flushStatements();
    }

    @Override
    public void close() {
        this.session.close();
    }

    @Override
    public void clearCache() {
        this.session.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return this.session.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return this.session.getMapper(aClass);
    }

    @Override
    public Connection getConnection() {
        return this.session.getConnection();
    }
}
