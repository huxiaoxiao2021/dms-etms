package com.jd.bluedragon.distribution.newseal.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.tms.TmsServiceManager;
import com.jd.bluedragon.core.jsf.tms.TransportResource;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.dao.TmsVehicleRouteDao;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteCondition;
import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.print.utils.StringHelper;

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
    
	@Autowired
	private BaseMajorManager baseMajorManager;
    
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
	public boolean syncToDb(TmsVehicleRoute tmsVehicleRoute,String beginNodeCode,String endNodeCode) {
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
				boolean loadTransport = false;
				if(StringHelper.isNotEmpty(tmsVehicleRoute.getTransportCode())) {
					//加载始发和目的信息，计算发车时间
					JdResult<TransportResource> transResult = tmsServiceManager.getTransportResourceByTransCode(tmsVehicleRoute.getTransportCode());
		            if (transResult != null
		            		&& transResult.isSucceed()
		            		&& transResult.getData() != null) {
		            	loadTransport = true;
		            	tmsVehicleRoute.setOriginalSiteCode(transResult.getData().getStartNodeId());
		            	tmsVehicleRoute.setDestinationSiteCode(transResult.getData().getEndNodeId());
		            	if(tmsVehicleRoute.getCreateTime() != null) {
		            		//先获取当前日期,增加小时及分钟
		            		Date dayTime = DateHelper.parseDate(DateHelper.formatDate(tmsVehicleRoute.getCreateTime()));
		            		Date departTime = DateHelper.add(dayTime, Calendar.HOUR_OF_DAY, transResult.getData().getSendCarHour());
		            		departTime = DateHelper.add(dayTime, Calendar.MINUTE, transResult.getData().getSendCarMin());
			            	tmsVehicleRoute.setDepartTime(departTime);
		            	}
		            }else {
		            	logger.warn("加载运力信息失败！transportCode={}", tmsVehicleRoute.getTransportCode());
		            }
				}
				//加载运力失败，通过始发和目的加载站点信息
				if(!loadTransport) {
					//加载站点名称
					if(beginNodeCode != null ) {
						BaseStaffSiteOrgDto startSiteInfo= baseMajorManager.getBaseSiteByDmsCode(beginNodeCode);
						if(startSiteInfo != null) {
							tmsVehicleRoute.setOriginalSiteCode(startSiteInfo.getSiteCode());
						}else {
							logger.warn("加载始发信息为空！{}", beginNodeCode);
						}
					}
					if(endNodeCode != null ) {
						BaseStaffSiteOrgDto endSiteInfo= baseMajorManager.getBaseSiteByDmsCode(endNodeCode);
						if(endSiteInfo != null) {
							tmsVehicleRoute.setDestinationSiteCode(endSiteInfo.getSiteCode());
						}else {
							logger.warn("加载目的信息为空！{}", endNodeCode);
						}
					}
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
