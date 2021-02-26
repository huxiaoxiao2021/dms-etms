package com.jd.bluedragon.distribution.newseal.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
		            	//根据线路创建时间和运力编码对应的发车时间计算标准发车时间
		            	Date departTime = calculateDepartTime(tmsVehicleRoute.getCreateTime(),transResult.getData());
		            	if(departTime != null) {
	            			tmsVehicleRoute.setDepartTime(departTime);
		            	}
		            }else {
		            	logger.warn("加载运力信息失败！transportCode={}", tmsVehicleRoute.getTransportCode());
		            }
				}else {
					//加载线路信息
					JdResult<List<TransportResource>> transportResult = tmsServiceManager.loadTransportResources(beginNodeCode, endNodeCode, Constants.CUAN_BAI_LINE_TYPES);
					if(transportResult != null 
							&& CollectionUtils.isNotEmpty(transportResult.getData())) {
		            	//根据线路创建时间和运力编码对应的发车时间计算标准发车时间
		            	Date departTime = calculateDepartTime(tmsVehicleRoute.getCreateTime(),transportResult.getData());
		            	if(departTime != null) {
	            			tmsVehicleRoute.setDepartTime(departTime);
		            	}
					}else {
						logger.warn("加载线路信息为空！{}->{}", beginNodeCode,endNodeCode);
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
	/**
	 * 根据运力及创建时间计算距离最近的发车时间
	 * @param createTime
	 * @param transportResources
	 * @return
	 */
	private Date calculateDepartTime(Date createTime, List<TransportResource> transportResources) {
    	if(createTime != null) {
    		//只有一个运力
    		if(transportResources.size() == 1) {
    			return calculateDepartTime(createTime,transportResources.get(0));
    		}
    		List<Date> sendCarTimes = new ArrayList<Date>();
    		String dayStr = DateHelper.formatDate(createTime);
    		for(TransportResource transportResource : transportResources) {
    			Date departTime = DateHelper.parseTmsCarTime(dayStr,transportResource.getSendCarTimeStr());
    			if(departTime != null) {
    				sendCarTimes.add(departTime);
    			}
    		}
    		//按发车时间排序
    		Collections.sort(sendCarTimes, new Comparator<Date>() {
				@Override
				public int compare(Date o1, Date o2) {
					return DateHelper.compare(o1, o2);
				}
    		});
    		Date departTime = null;
    		for(Date sendCarTime : sendCarTimes) {
        		if(DateHelper.compare(sendCarTime, createTime) >= 0) {
        			departTime = sendCarTime;
        			break;
        		}
    		}
    		if(departTime == null && !sendCarTimes.isEmpty()) {
    			departTime = DateHelper.addDate(sendCarTimes.get(0), 1);
    		}
    		return departTime;
    	}
		return null;
	}
	/**
	 * 根据运力及创建时间计算发车时间
	 * @param createTime
	 * @param transportResource
	 * @return
	 */
	private Date calculateDepartTime(Date createTime, TransportResource transportResource) {
    	if(createTime != null) {
    		//先获取线路创建时间，根据运力编码对应的时间计算发车时间
    		String dayStr = DateHelper.formatDate(createTime);
    		String sendCarTimeStr = transportResource.getSendCarTimeStr();
    		Date departTime = DateHelper.parseTmsCarTime(dayStr,sendCarTimeStr);
    		if(departTime == null) {
    			return null;
    		}
    		//发车时间小于创建时间，增加一天
    		if(DateHelper.compare(departTime, createTime) < 0) {
    			departTime = DateHelper.addDate(departTime, 1);
    		}
    		return departTime;
    	}
		return null;
	}
}
