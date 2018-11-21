package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArSendFlightRealtime;
import com.jd.bluedragon.distribution.transport.dao.ArSendFlightRealtimeDao;
import com.jd.bluedragon.distribution.transport.service.ArSendFlightRealtimeService;

/**
 *
 * @ClassName: ArSendFlightRealtimeServiceImpl
 * @Description: --Service接口实现
 * @author wuyoude
 * @date 2018年11月21日 11:11:13
 *
 */
@Service("arSendFlightRealtimeService")
public class ArSendFlightRealtimeServiceImpl extends BaseService<ArSendFlightRealtime> implements ArSendFlightRealtimeService {

	@Autowired
	@Qualifier("arSendFlightRealtimeDao")
	private ArSendFlightRealtimeDao arSendFlightRealtimeDao;

	@Override
	public Dao<ArSendFlightRealtime> getDao() {
		return this.arSendFlightRealtimeDao;
	}

}
