package com.jd.bluedragon.distribution.b2bRouter.dao;

import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.cross.dao.CrossSortingDao;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BRouterDao {
    private SqlSession sqlSession;
    private SqlSessionTemplate sqlSessionRead;
    public static final String namespace = B2BRouterDao.class.getName();

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
     * 新增一条路由信息
     * @param router
     * @return
     */
    public Integer addB2BRouter(B2BRouter router){
        return this.getSqlSession().insert(B2BRouterDao.namespace + ".addB2BRouter",router);
    }

    /**
     * 删除一条路由信息
     * @param router
     * @return
     */
    public int deleteById(B2BRouter router){
        return this.getSqlSession().update(B2BRouterDao.namespace + ".deleteById", router);
    }

    /**
     * 根据始发网点id和目的网点id查找完成路径
     * @router router
     * @return
     */
    public List<B2BRouter> selectFullLineBySiteCode(B2BRouter router){
        return this.getSqlSessionRead().selectList(B2BRouterDao.namespace + ".selectFullLineBySiteCode", router);
    }

    public List<String> selectFullLineId(B2BRouter router){
        return this.getSqlSessionRead().selectOne(B2BRouterDao.namespace + ".selectFullLineId", router);
    }

    public List<B2BRouter> selectAllRouter(){
        return this.getSqlSessionRead().selectList(B2BRouterDao.namespace + ".selectAllRouter");
    }

}
