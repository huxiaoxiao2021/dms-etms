package com.jd.bluedragon.distribution.base.dao.impl;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDictCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.dao.DmsBaseDictDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: DmsBaseDictDaoImpl
 * @Description: 数据字典--Dao接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
@Repository("dmsBaseDictDao")
public class DmsBaseDictDaoImpl extends BaseDao<DmsBaseDict> implements DmsBaseDictDao {

    /**
     * 根据查询条件获取数据字典数据
     * @param pagerCondition
     * @return
     */
    @Override
    public List<DmsBaseDict> queryByCondition(PagerCondition pagerCondition) {
        return  this.sqlSession.selectList(getNameSpace()+".queryByCondition", pagerCondition);
    }
    /**
     * 查询所有的分组信息
     */
	@Override
	public List<DmsBaseDict> queryAllGroups() {
		return this.sqlSession.selectList(getNameSpace()+".queryAllGroups");
	}

    /**
     * 带顺查询
     * @param pagerCondition
     * @return
     */
    @Override
    public List<DmsBaseDict> queryOrderByCondition(PagerCondition pagerCondition) {
        return this.sqlSession.selectList(getNameSpace()+".queryOrderByCondition",pagerCondition);
    }


}
