package com.jd.bluedragon.distribution.urban.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.urban.dao.TransbillMDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 城配运单M表--Service接口实现
 * 
 * @ClassName: TransbillMServiceImpl
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月28日 13:30:01
 *
 */
@Service("transbillMService")
@SuppressWarnings("all")
public class TransbillMServiceImpl implements TransbillMService {

	private static final Log logger= LogFactory.getLog(UrbanWaybillServiceImpl.class);

	@Autowired
	private TransbillMDao transbillMDao;
	
	@Override
	public TransbillM getByWaybillCode(String waybillCode) {
		// TODO Auto-generated method stub
		if(StringHelper.isNotEmpty(waybillCode)){
			return transbillMDao.findByWaybillCode(waybillCode);
		}
		return null;
	}

	@Override
	public List<TransbillM> getListByScheduleBillCode(String scheduleBillCode) {
		// TODO Auto-generated method stub
		if(StringHelper.isNotEmpty(scheduleBillCode)){
			return transbillMDao.findByScheduleBillCode(scheduleBillCode);
		}
		return null;
	}
}
