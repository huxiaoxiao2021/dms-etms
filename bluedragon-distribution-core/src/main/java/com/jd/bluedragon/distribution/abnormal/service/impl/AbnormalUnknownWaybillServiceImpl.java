package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.dao.AbnormalUnknownWaybillDao;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;

/**
 *
 * @ClassName: AbnormalUnknownWaybillServiceImpl
 * @Description: 三无订单申请--Service接口实现
 * @author wuyoude
 * @date 2018年05月08日 15:16:15
 *
 */
@Service("abnormalUnknownWaybillService")
public class AbnormalUnknownWaybillServiceImpl extends BaseService<AbnormalUnknownWaybill> implements AbnormalUnknownWaybillService {

	@Autowired
	@Qualifier("abnormalUnknownWaybillDao")
	private AbnormalUnknownWaybillDao abnormalUnknownWaybillDao;

	@Override
	public Dao<AbnormalUnknownWaybill> getDao() {
		return this.abnormalUnknownWaybillDao;
	}

}
