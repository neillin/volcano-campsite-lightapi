package com.mservicetech.campsite;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisSessionManager  {

    public static final MybatisSessionManager INSTANCE = new MybatisSessionManager();
    private static final Logger log = LoggerFactory.getLogger(MybatisSessionManager.class);

    private SqlSessionFactory sqlSessionFactory;
    private ThreadLocal<List<SessionWrapper>> threadLocal = new ThreadLocal<List<SessionWrapper>>() {
        @Override
        protected List<SessionWrapper> initialValue() {
            return new ArrayList<>();
        }
    };

    private MybatisSessionManager() {
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            log.error("Failed to read in mybatis-config.xml", e);
            throw new RuntimeException("Failed to read in mybatis-config.xml", e);
        }
    }

    /**
     * The purpose of this method is to make sure all database operations happen within this function call will use
     * same SQL session, they will be committed or roll backed in same transaction. It wil create a new SQL
     * session if there is not SQL session associated with current thread, otherwise will use existing SQL session
     * associated with current thread. Any exception thrown inside this function call will cause all operations rollback.
     * 
     * @param <T>
     * @param func
     * @return
     */
    public <T> T executeWithSession( Function<SqlSession, T> func) {
        var session = this.getCurrentSession();
        if (session != null) {
            try {
                return func.apply(session);
            } catch( RuntimeException e) {
                session.rollback();
                throw e;
            }
        } else {
            return this.executeWithNewSession(func);
        }
    }

    public <T> T executeWithNewSession( Function<SqlSession, T> func) {

        var session = this.sqlSessionFactory.openSession(false);
        var sessions = this.threadLocal.get();
        var wrapper = new SessionWrapper(session);
        sessions.add(0, wrapper);
        boolean rb = false;
        try {
            T v = func.apply(session);
            if (!wrapper.isRollback()) {
                session.commit();
            } else {
                session.rollback();
                rb = true;
                throw new IllegalStateException("Session was marked as ROLLBACK .");
            }
            return v;
        } catch( RuntimeException e) {
            if (!rb) {
                session.rollback();
            }
            throw e;
        } finally {
            sessions.remove(wrapper);
            session.close();
        }
    }

    private SqlSession getCurrentSession() {
        var sessions = this.threadLocal.get();
        return sessions.isEmpty() ? null : sessions.get(0);
    }

    private static class SessionWrapper implements SqlSession {
    	
        public final SqlSession nested;

        private boolean rollback = false;

		SessionWrapper(SqlSession nested) {
            this.nested = nested;
        }
        
        public <T> T selectOne(String statement) {
			return nested.selectOne(statement);
		}

		public <T> T selectOne(String statement, Object parameter) {
			return nested.selectOne(statement, parameter);
		}

		public <E> List<E> selectList(String statement) {
			return nested.selectList(statement);
		}

		public <E> List<E> selectList(String statement, Object parameter) {
			return nested.selectList(statement, parameter);
		}

		public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
			return nested.selectList(statement, parameter, rowBounds);
		}

		public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
			return nested.selectMap(statement, mapKey);
		}

		public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
			return nested.selectMap(statement, parameter, mapKey);
		}

		public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
			return nested.selectMap(statement, parameter, mapKey, rowBounds);
		}

		public <T> Cursor<T> selectCursor(String statement) {
			return nested.selectCursor(statement);
		}

		public <T> Cursor<T> selectCursor(String statement, Object parameter) {
			return nested.selectCursor(statement, parameter);
		}

		public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
			return nested.selectCursor(statement, parameter, rowBounds);
		}

		public void select(String statement, Object parameter, ResultHandler handler) {
			nested.select(statement, parameter, handler);
		}

		public void select(String statement, ResultHandler handler) {
			nested.select(statement, handler);
		}

		public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
			nested.select(statement, parameter, rowBounds, handler);
		}

		public int insert(String statement) {
			return nested.insert(statement);
		}

		public int insert(String statement, Object parameter) {
			return nested.insert(statement, parameter);
		}

		public int update(String statement) {
			return nested.update(statement);
		}

		public int update(String statement, Object parameter) {
			return nested.update(statement, parameter);
		}

		public int delete(String statement) {
			return nested.delete(statement);
		}

		public int delete(String statement, Object parameter) {
			return nested.delete(statement, parameter);
		}

		public void commit() {
            if (this.rollback) {
                throw new IllegalStateException("Cannot commit this session cause it was marked as ROLLBACK");
            }
			// nested.commit();
		}

		public void commit(boolean force) {
            if (this.rollback) {
                throw new IllegalStateException("Cannot commit this session cause it was marked as ROLLBACK");
            }
			nested.commit(force);
		}

		public void rollback() {
			// nested.rollback();
            this.rollback = true;
		}

		public void rollback(boolean force) {
			nested.rollback(force);
            this.rollback = true;
		}

		public List<BatchResult> flushStatements() {
			return nested.flushStatements();
		}

		public void close() {
			nested.close();
		}

		public void clearCache() {
			nested.clearCache();
		}

		public Configuration getConfiguration() {
			return nested.getConfiguration();
		}

		public <T> T getMapper(Class<T> type) {
			return nested.getMapper(type);
		}

		public Connection getConnection() {
			return nested.getConnection();
		}

        public boolean isRollback() {
            return this.rollback;
        }
    }
}
