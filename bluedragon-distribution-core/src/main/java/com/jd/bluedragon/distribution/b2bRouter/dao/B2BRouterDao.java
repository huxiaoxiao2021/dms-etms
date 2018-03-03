package com.jd.bluedragon.distribution.b2bRouter.dao;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.B2BRouterRequest;
import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;
import com.jd.bluedragon.distribution.cross.dao.CrossSortingDao;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Map;

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
     * 新增一条路由
     *
     * @param router
     * @return
     */
    public Integer addB2BRouter(B2BRouter router) {
        return this.getSqlSession().insert(B2BRouterDao.namespace + ".addB2BRouter", router);
    }

    /**
     * 删除一条路由
     * @param router
     * @return
     */
    public int deleteById(B2BRouter router) {
        return this.getSqlSession().update(B2BRouterDao.namespace + ".deleteById", router);
    }


    /**
     * 更新路由信息
     * @param router
     * @return
     */
    public int updateById(B2BRouter router){
        return this.getSqlSession().update(B2BRouterDao.namespace + ".updateById", router);
    }

    /**
     * 根据条件查询路由信息
     * @param params
     * @return
     */
    public List<B2BRouter> queryByCondition(Map<String,Object> params) {
        return this.getSqlSessionRead().selectList(B2BRouterDao.namespace + ".queryByCondition",params);
    }

    /**
     * 统计符合条件的路由数量
     * @param params
     * @return
     */
    public Integer countByCondition(Map<String, Object> params) {
        return (Integer) this.getSqlSessionRead().selectOne(B2BRouterDao.namespace + ".countByCondition", params);
    }

    /**
     * 根据id获取路由信息
     * @param id
     * @return
     */
    public B2BRouter getRouterById(int id){
        return this.getSqlSessionRead().selectOne(B2BRouterDao.namespace + ".getRouterById", id);
    }

    /**
     * 根据完整线路获取对应的路由信息
     * @param siteIdFullLine
     * @return
     */
    public List<B2BRouter> selectByFullLine(String siteIdFullLine){
        return this.getSqlSessionRead().selectList(B2BRouterDao.namespace + ".selectByFullLine", siteIdFullLine);
    }
}
