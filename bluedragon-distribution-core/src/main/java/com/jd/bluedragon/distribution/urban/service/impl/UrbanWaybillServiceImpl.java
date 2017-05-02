package com.jd.bluedragon.distribution.urban.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.urban.dao.UrbanWaybillDao;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.distribution.urban.service.UrbanWaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 城配运单同步表--Service接口实现
 * 
 * @ClassName: UrbanWaybillServiceImpl
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月17日 16:55:58
 *
 */
@Service("urbanWaybillService")
@SuppressWarnings("all")
public class UrbanWaybillServiceImpl implements UrbanWaybillService {
	private static final Log logger= LogFactory.getLog(UrbanWaybillServiceImpl.class);
	
	@Autowired
	private UrbanWaybillDao urbanWaybillDao;
	
	@Autowired
	private TransbillMService transbillMService;
	

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean save(UrbanWaybill urbanWaybill) {
		if(urbanWaybill!=null&&StringHelper.isNotEmpty(urbanWaybill.getWaybillCode())){
			UrbanWaybill oldData = urbanWaybillDao.findByWaybillCode(urbanWaybill.getWaybillCode());
			if(oldData!=null){
				urbanWaybill.setId(oldData.getId());
				urbanWaybillDao.updateBySelective(urbanWaybill);
			}else{
				urbanWaybillDao.insert(urbanWaybill);
			}
			return true;
		}else{
			logger.error("城配运单保存失败！原因"+(urbanWaybill==null?"对象为空":JsonHelper.toJson(urbanWaybill)));
		}
		return false;
	}

	@Override
	public UrbanWaybill getByWaybillCode(String waybillCode) {
		if(StringHelper.isNotEmpty(waybillCode)){
			return urbanWaybillDao.findByWaybillCode(waybillCode);
		}
		return null;
	}

	@Override
	public List<UrbanWaybill> getListByScheduleBillCode(String scheduleBillCode) {
		if(StringHelper.isNotEmpty(scheduleBillCode)){
			return urbanWaybillDao.findByScheduleBillCode(scheduleBillCode);
		}
		return null;
	}

}
