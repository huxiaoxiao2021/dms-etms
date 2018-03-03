package com.jd.bluedragon.distribution.b2bRouter.dao;

import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouterNode;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Map;

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


    /**
     * 增加一条路由链路
     * @param nodes
     * @return
     */
    public Integer addB2BRouterNodes(List<B2BRouterNode> nodes){
        return this.getSqlSession().insert(B2BRouterNodeDao.namespace+".addB2BRouterNodes", nodes);
    }

    /**
     * 逻辑删除一条路由链路
     * @param params
     * @return
     */
    public Integer deleteRouterNodeByChainId(Map<String,Object> params){
        return this.getSqlSession().update(B2BRouterNodeDao.namespace +".deleteRouterNodeByChainId", params);
    }

    /**
     * 更新一条路由线路
     * @param params
     * @return
     */
    public Integer updateByChainId(Map<String,Object> params){
        return this.getSqlSession().update(B2BRouterNodeDao.namespace +".updateByChainId",params);
    }

    /**
     * 获得当前网点可达的下一个网点
     * @param node
     * @return
     */
    public List<B2BRouterNode> getNextNode(B2BRouterNode node){
        return this.getSqlSession().selectList(B2BRouterNodeDao.namespace +".getNextNode",node);
    }
}
