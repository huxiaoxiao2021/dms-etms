package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.dao.DmsAbnormalEclpDao;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;

/**
 *
 * @ClassName: DmsAbnormalEclpServiceImpl
 * @Description: ECLP外呼申请表--Service接口实现
 * @author wuyoude
 * @date 2018年03月14日 16:31:20
 *
 */
@Service("dmsAbnormalEclpService")
public class DmsAbnormalEclpServiceImpl extends BaseService<DmsAbnormalEclp> implements DmsAbnormalEclpService {

	@Autowired
	@Qualifier("dmsAbnormalEclpDao")
	private DmsAbnormalEclpDao dmsAbnormalEclpDao;

	@Override
	public Dao<DmsAbnormalEclp> getDao() {
		return this.dmsAbnormalEclpDao;
	}

}
