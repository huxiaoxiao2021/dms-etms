package com.jd.bluedragon.distribution.b2bRouter.dao;

import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouterNodeDao {
    private SqlSession sqlSession;

    private SqlSessionTemplate sqlSessionRead;

    public static final String namespace = B2BRouterNodeDao.class.getName();

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public SqlSessionTemplate getSqlSessionRead() {
        return sqlSessionRead;
    }

    public void setSqlSessionRead(SqlSessionTemplate sqlSessionRead) {
        this.sqlSessionRead = sqlSessionRead;
    }


    public Integer addB2BRouterNodes(List<B2BRouterNode> nodes){
        return this.getSqlSession().insert(B2BRouterNodeDao.namespace+".addB2BRouterNodes", nodes);
    }

}
