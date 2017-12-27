package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.dao.DmsBaseDictDao;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;

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

}
