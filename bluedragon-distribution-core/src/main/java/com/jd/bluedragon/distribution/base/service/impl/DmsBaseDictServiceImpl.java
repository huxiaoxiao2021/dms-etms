package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.dao.DmsBaseDictDao;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDictCondition;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @ClassName: DmsBaseDictServiceImpl
 * @Description: 数据字典--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
@Service("dmsBaseDictService")
public class DmsBaseDictServiceImpl extends BaseService<DmsBaseDict> implements DmsBaseDictService {

	@Autowired
	@Qualifier("dmsBaseDictDao")
	private DmsBaseDictDao dmsBaseDictDao;

	@Override
	public Dao<DmsBaseDict> getDao() {
		return this.dmsBaseDictDao;
	}


    /**
     * 根据查询条件获取数据字典数据
     * @param pagerCondition
     * @return
     */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<DmsBaseDict> queryByCondition(PagerCondition pagerCondition) {
		return dmsBaseDictDao.queryByCondition(pagerCondition);
	}

    /**
     * 根据parentId和typeGroup查找分拣基础数据
     * @param parentId
     * @param typeGroup
     * @return
     */
    @Cache(key = "DmsBaseDictService.queryByParentIdAndTypeGroup@args0@args1", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<DmsBaseDict> queryByParentIdAndTypeGroup(Integer parentId, Integer typeGroup) {
        DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setParentId(parentId);
        dmsBaseDictCondition.setTypeGroup(typeGroup);
        return queryByCondition(dmsBaseDictCondition);
    }
}
