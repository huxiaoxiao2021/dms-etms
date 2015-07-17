package com.jd.bluedragon.common.dao;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

public class BaseDao<T> {

    private SqlSession sqlSession;
    
    private SqlSessionTemplate sqlSessionRead;

    public SqlSession getSqlSession() {
        return this.sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Integer add(String namespace, T entity) {
        return this.getSqlSession().insert(namespace + ".add", entity);
    }

    @SuppressWarnings("unchecked")
    public T get(String namespace, Long pk) {
        return (T) this.getSqlSession().selectOne(namespace + ".get", pk);
    }

    public Integer update(String namespace, T entity) {
        return this.getSqlSession().update(namespace + ".update", entity);
    }

	public SqlSessionTemplate getSqlSessionRead() {
		return sqlSessionRead;
	}

	public void setSqlSessionRead(SqlSessionTemplate sqlSessionRead) {
		this.sqlSessionRead = sqlSessionRead;
	}

}
