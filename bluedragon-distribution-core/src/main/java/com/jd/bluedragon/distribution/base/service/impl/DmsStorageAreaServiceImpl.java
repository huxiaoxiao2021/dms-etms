package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.dao.DmsStorageAreaDao;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;

/**
 *
 * @ClassName: DmsStorageAreaServiceImpl
 * @Description: 分拣中心库位--Service接口实现
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
@Service("dmsStorageAreaService")
public class DmsStorageAreaServiceImpl extends BaseService<DmsStorageArea> implements DmsStorageAreaService {

	@Autowired
	@Qualifier("dmsStorageAreaDao")
	private DmsStorageAreaDao dmsStorageAreaDao;

	@Override
	public Dao<DmsStorageArea> getDao() {
		return this.dmsStorageAreaDao;
	}

}
