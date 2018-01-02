package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.distribution.transport.domain.ArFlightInfo;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;

import java.util.*;

/**
 *
 * @ClassName: ArSendRegisterServiceImpl
 * @Description: 发货登记表--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Service("arSendRegisterService")
public class ArSendRegisterServiceImpl extends BaseService<ArSendRegister> implements ArSendRegisterService {

	@Autowired
	@Qualifier("arSendRegisterDao")
	private ArSendRegisterDao arSendRegisterDao;

	@Override
	public Dao<ArSendRegister> getDao() {
		return this.arSendRegisterDao;
	}

	/**
	 * 从发货登记表中获取所有的已经登记的始发城市的信息
	 * @return
     */
	@Override
	public List<City> queryStartCityInfo(){
		List<ArSendRegister> arSendRegisters = arSendRegisterDao.queryStartCityInfo();
		List<City> cities = new ArrayList<City>();
		if(arSendRegisters == null){
			logger.error("从发货登记表找查询到的始发城市信息为空！");
			return null;
		}

		for(ArSendRegister arSendRegister : arSendRegisters){
			//如果城市的id和name有一个为空，则无法对应，直接舍弃该条记录
			if(arSendRegister.getStartCityId() == null || StringHelper.isEmpty(arSendRegister.getStartCityName())){
				logger.error("发货登记表中的始发城市信息城市id或者城市名称为空，不组装，城市id:"+arSendRegister.getStartCityId()+
						"，城市名称："+arSendRegister.getStartCityName());
				continue;
			}
			cities.add(new City(arSendRegister.getStartCityId(),arSendRegister.getStartCityName()));
		}

		return cities;
	}

	/**
	 * 从发货登记表中获取所有的已经登记的目的城市的信息
	 * @return
	 */
	@Override
	public List<City> queryEndCityInfo(){
		List<ArSendRegister> arSendRegisters = arSendRegisterDao.queryEndCityInfo();
		List<City> cities = new ArrayList<City>();
		if(arSendRegisters == null){
			logger.error("从发货登记表找查询到的目的城市信息为空！");
			return null;
		}

		for(ArSendRegister arSendRegister : arSendRegisters){
			//如果城市的id和name有一个为空，则无法对应，直接舍弃该条记录
			if(arSendRegister.getEndCityId()== null || StringHelper.isEmpty(arSendRegister.getEndCityName())){
				logger.error("发货登记表中的目的城市信息城市id或者城市名称为空，不组装，城市id:"+arSendRegister.getEndCityId()+
						"，城市名称："+arSendRegister.getEndCityName());
				continue;
			}
			cities.add(new City(arSendRegister.getEndCityId(),arSendRegister.getEndCityName()));
		}

		return cities;
	}

	/**
	 * 从发货登记表中获取24小时内到达所选城市的航班/铁路信息
	 * @param arSendRegister
	 * @return
	 */
	public List<ArSendRegister> queryWaitReceive(ArSendRegister arSendRegister){
		return arSendRegisterDao.queryWaitReceive(arSendRegister);
	}

	@Override
	public ArFlightInfo getFlightInfoByOrderCode(String orderCode) {
		// 调用TMS接口，根据航空单号获取航班信息
		ArFlightInfo arFlightInfo = new ArFlightInfo();
		arFlightInfo.setAirlineCompany("四川航空");
		arFlightInfo.setStartCityId(1);
		arFlightInfo.setStartCityName("北京");
		arFlightInfo.setEndCityId(2);
		arFlightInfo.setEndCityName("上海");
		arFlightInfo.setPlanStartTime(new Date());
		arFlightInfo.setPlanEndTime(new Date());
		return arFlightInfo;
	}

}
