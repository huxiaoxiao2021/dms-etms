package com.jd.bluedragon.common.dao;

import com.jd.bluedragon.utils.JsonHelper;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDao<T> {

    private Logger logger = LoggerFactory.getLogger(BaseDao.class);

    private SqlSession sqlSession;
    
    private SqlSessionTemplate sqlSessionRead;

    public SqlSession getSqlSession() {
        return this.sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Integer add(String namespace, T entity) {
        try{
            return this.getSqlSession().insert(namespace + ".add", entity);
        }catch (Exception e){
            logger.error("BaseDao add error! namespace {} entity {}",namespace,JsonHelper.toJson(entity),e);
            throw e;
        }
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
