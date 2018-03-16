package com.jd.bluedragon.distribution.base.dao;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 *
 * @ClassName: DmsBaseDictDao
 * @Description: 数据字典--Dao接口
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
public interface DmsBaseDictDao extends Dao<DmsBaseDict> {

    /**
     * 根据查询条件获取数据字典数据
     * @param pagerCondition
     * @return
     */
    public List<DmsBaseDict> queryByCondition(PagerCondition pagerCondition);
    /**
     * 查询所有的分组信息
     * @return
     */
	public List<DmsBaseDict> queryAllGroups();
}
