package com.jd.bluedragon.distribution.alliance.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.alliance.domain.AllianceBusiDeliveryDetail;
import com.jd.bluedragon.distribution.alliance.dao.AllianceBusiDeliveryDetailDao;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;

/**
 *
 * @ClassName: AllianceBusiDeliveryDetailServiceImpl
 * @Description: 加盟商计费交付明细表--Service接口实现
 * @author wuyoude
 * @date 2019年07月10日 15:35:36
 *
 */
@Service("allianceBusiDeliveryDetailService")
public class AllianceBusiDeliveryDetailServiceImpl extends BaseService<AllianceBusiDeliveryDetail> implements AllianceBusiDeliveryDetailService {

	@Autowired
	@Qualifier("allianceBusiDeliveryDetailDao")
	private AllianceBusiDeliveryDetailDao allianceBusiDeliveryDetailDao;

	@Override
	public Dao<AllianceBusiDeliveryDetail> getDao() {
		return this.allianceBusiDeliveryDetailDao;
	}

}
