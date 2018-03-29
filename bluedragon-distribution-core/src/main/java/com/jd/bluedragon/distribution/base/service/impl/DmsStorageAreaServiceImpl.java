package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.distribution.base.service.ProvinceAndCityService;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.dao.DmsStorageAreaDao;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: DmsStorageAreaServiceImpl
 * @Description: 分拣中心库位--Service接口实现
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
@Service("dmsStorageAreaService")
public class DmsStorageAreaServiceImpl extends BaseService<DmsStorageArea> implements DmsStorageAreaService {

	@Autowired
	@Qualifier("dmsStorageAreaDao")
	private DmsStorageAreaDao dmsStorageAreaDao;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private ProvinceAndCityService provinceAndCityService;

	@Override
	public Dao<DmsStorageArea> getDao() {
		return this.dmsStorageAreaDao;
	}

	@Cache(key = "dmsStorageAreaServiceImpl.findByProAndCity@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	public DmsStorageArea findByProAndCity( Integer dmsSiteCode,Integer dmsProvinceCode,Integer dmsCityCode){
		if(dmsCityCode  == null){
			return dmsStorageAreaDao.findByPro(dmsSiteCode,dmsProvinceCode);
		}else {
			return dmsStorageAreaDao.findByProAndCity(dmsSiteCode,dmsProvinceCode,dmsCityCode);
		}
	}

	public String  checkExportData(List<DmsStorageArea> dataList,Integer dmsSiteCode,String dmsSiteName) {
		String errorString = "";
		for (DmsStorageArea dmsStorageArea : dataList){
			dmsStorageArea.setDmsSiteCode(dmsSiteCode);
			dmsStorageArea.setDmsSiteName(dmsSiteName);
			Integer proId = AreaHelper.getProIdByProName(dmsStorageArea.getDesProvinceName());
			if(proId == -1){
				errorString = "导入的省不存在！";
				return errorString;
			}else {
				dmsStorageArea.setDesProvinceCode(proId);
				List<ProvinceAndCity> cityList = provinceAndCityService.getCityByProvince(proId);
				for (ProvinceAndCity c : cityList){
					if(c.getAssortName().equals(dmsStorageArea.getDesCityName())){
						dmsStorageArea.setDesCityCode(Integer.parseInt(c.getAssortCode()));
						DmsStorageArea byProAndCity = findByProAndCity(dmsSiteCode, dmsStorageArea.getDesProvinceCode(), dmsStorageArea.getDesCityCode());
						if(byProAndCity != null){
							String oldStorageCode = dmsStorageArea.getStorageCode().trim();
							String newStorageCode = byProAndCity.getStorageCode().trim();
							errorString = "已存在相同的省市"+dmsStorageArea.getDesProvinceName()+";"+dmsStorageArea.getDesCityName();
							return errorString;
						}else {
							break;
						}
					}
				}
				if(dmsStorageArea.getDesCityCode() == null){
					errorString = "导入的市不存在！";
					return errorString;
				}
			}
		}
		return errorString;
	}


	public Boolean importExcel(List<DmsStorageArea> dataList, String createUser, String createUserName, Date createTime){
		List<DmsStorageArea> bufferList = new ArrayList<DmsStorageArea>();
		for(DmsStorageArea dmsStorageArea : dataList){
			dmsStorageArea.setStorageType(1);
			dmsStorageArea.setCreateUser(createUser);
			dmsStorageArea.setCreateUserName(createUserName);
			dmsStorageArea.setCreateTime(createTime);
			dmsStorageArea.setUpdateUser(createUser);
			dmsStorageArea.setUpdateUserName(createUserName);
			dmsStorageArea.setUpdateTime(createTime);
			//批量插入 默认值
			initObjectValue(dmsStorageArea);
			bufferList.add(dmsStorageArea);
			if(bufferList.size()==10){
				if(this.getDao().batchInsert(bufferList)){
					bufferList.clear();
				}else{
					return false;
				}
			}
		}
		if(bufferList.size() > 0){
			return this.getDao().batchInsert(bufferList);
		}
		return true;
	}

	private void initObjectValue(DmsStorageArea obj){
		Class clazz = obj.getClass();
		Field[] declaredFields = clazz.getDeclaredFields();
		for(Field field : declaredFields){
			Class type = field.getType();
			try {
				field.setAccessible(true);
				Object v = field.get(obj);
				if(v==null){
					if(type == Integer.class){
						field.set(obj,new Integer(0));
					}else if(type == Double.class){
						field.set(obj,new Double(0));
					}else if(type == Long.class){
						field.set(obj,new Long(0));
					}else if(type == String.class){
						field.set(obj,"");
					}
				}
			} catch (IllegalAccessException e) {
				logger.error("initObjectValue fail!   "+e.getMessage().toString());
			}
		}
	}
}
