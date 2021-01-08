package com.jd.bluedragon.distribution.newseal.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.tms.TmsServiceManager;
import com.jd.bluedragon.core.jsf.tms.TransportResource;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.dao.TmsVehicleRouteDao;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteCondition;
import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.DateHelper;

/**
 * @ClassName: TmsVehicleRouteServiceImpl
 * @Description: 运输任务线路表--Service接口实现
 * @author wuyoude
 * @date 2020年12月31日 21:24:16
 *
 */
@Service("tmsVehicleRouteService")
public class TmsVehicleRouteServiceImpl implements TmsVehicleRouteService {

	private static final Logger logger = LoggerFactory.getLogger(TmsVehicleRouteServiceImpl.class);

	@Autowired
	@Qualifier("tmsVehicleRouteDao")
	private TmsVehicleRouteDao tmsVehicleRouteDao;
	
    @Autowired
    private TmsServiceManager tmsServiceManager;
    
	@Override
	public boolean insert(TmsVehicleRoute tmsVehicleRoute) {
		return tmsVehicleRouteDao.insert(tmsVehicleRoute)==1;
	}

	@Override
	public boolean update(TmsVehicleRoute tmsVehicleRoute) {
		return tmsVehicleRouteDao.update(tmsVehicleRoute)==1;
	}

	@Override
	public boolean logicalDeleteById(Long id) {
		return tmsVehicleRouteDao.logicalDeleteById(id)==1;
	}

	@Override
	public TmsVehicleRoute queryByVehicleRouteCode(String vehicleRouteCode) {
		return tmsVehicleRouteDao.queryByVehicleRouteCode(vehicleRouteCode);
	}

	@Override
	public boolean syncToDb(TmsVehicleRoute tmsVehicleRoute) {
		TmsVehicleRoute oldData = queryByVehicleRouteCode(tmsVehicleRoute.getVehicleRouteCode());
		if(oldData != null) {
			//已存在无效数据，不需要做任何处理
			if(Constants.YN_NO.equals(oldData.getYn())) {
				return true;
			}else if(Constants.YN_NO.equals(tmsVehicleRoute.getYn())){
				//取消操作，更新为无效
				return this.logicalDeleteById(oldData.getId());
			}else {
				//数据只会插入和取消一次，不做处理
				return true;
			}
		}else {
			if(Constants.YN_YES.equals(tmsVehicleRoute.getYn())){
				//加载始发和目的信息，计算发车时间
				JdResult<TransportResource> transResult = tmsServiceManager.getTransportResourceByTransCode(tmsVehicleRoute.getTransportCode());
	            if (transResult != null
	            		&& transResult.isSucceed()
	            		&& transResult.getData() != null) {
	            	tmsVehicleRoute.setOriginalSiteCode(transResult.getData().getStartNodeId());
	            	tmsVehicleRoute.setDestinationSiteCode(transResult.getData().getEndNodeId());
	            	if(tmsVehicleRoute.getCreateTime() != null) {
	            		String departTimeStr = DateHelper.formatDate(tmsVehicleRoute.getCreateTime()) +" "+ transResult.getData().getSendCarTimeStr();
		            	tmsVehicleRoute.setDepartTime(DateHelper.parseDate(departTimeStr, departTimeStr));
	            	}
	            }else {
	            	logger.warn("加载运力信息失败！transportCode={}", tmsVehicleRoute.getTransportCode());
	            }
			}
			//无论yn是否取消都要插入数据
			return this.insert(tmsVehicleRoute);
		}
	}

	@Override
	public List<TmsVehicleRoute> queryByCondition(TmsVehicleRouteCondition condition) {
		return tmsVehicleRouteDao.queryByCondition(condition);
	}
}
