package com.jd.bluedragon.distribution.base.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ProvinceAndCity;
import com.jd.bluedragon.distribution.base.dao.DmsStorageAreaDao;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.bluedragon.distribution.base.service.ProvinceAndCityService;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;

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
	public DmsStorageArea findByDmsSiteAndWaybillAdress( Integer dmsSiteCode,Integer dmsProvinceCode,Integer dmsCityCode){
		if(dmsSiteCode != null
				&&(dmsProvinceCode != null || dmsCityCode != null)){
			//没有城市，查询省
			if(dmsCityCode  == null){
				return dmsStorageAreaDao.findByDesProvinceCode(dmsSiteCode,dmsProvinceCode);
			}else {
				//有城市，先查询城市后查询省的配置
				if(dmsProvinceCode == null){
					 return null;
				}
				DmsStorageArea dmsStorageArea = dmsStorageAreaDao.findByDesProvinceAndCityCode(dmsSiteCode,dmsProvinceCode,dmsCityCode);
				if(dmsStorageArea == null){
					return dmsStorageAreaDao.findByDesProvinceCode(dmsSiteCode,dmsProvinceCode);
				}
				return dmsStorageArea;
			}
		}
		return null;
	}

	public String  checkExportData(List<DmsStorageArea> dataList,Integer dmsSiteCode,String dmsSiteName) {
		String errorString = "";
		int rowIndex = 1;
		Set<String> keys = new HashSet<String>();
		for (DmsStorageArea dmsStorageArea : dataList){
			String msgPre = "第"+rowIndex+"行";
			dmsStorageArea.setDmsSiteCode(dmsSiteCode);
			dmsStorageArea.setDmsSiteName(dmsSiteName);
			Integer proId = AreaHelper.getProIdByProName(dmsStorageArea.getDesProvinceName());
			String key = dmsStorageArea.getDesProvinceName() + "_"+dmsStorageArea.getDesCityName();
			if(keys.contains(key)){
				errorString = msgPre + "，表格中同一省市存在多条库位配置！";
				break;
			}else{
				keys.add(key);
			}
			if(proId == -1){
				errorString = msgPre + "，省份不存在！";
				break;
			}else {
				dmsStorageArea.setDesProvinceCode(proId);
				//城市不为空，设置城市id
				if(StringHelper.isNotEmpty(dmsStorageArea.getDesCityName())){
					List<ProvinceAndCity> cityList = provinceAndCityService.getCityByProvince(proId);
					for (ProvinceAndCity c : cityList){
						if(c.getAssortName().equals(dmsStorageArea.getDesCityName())){
							dmsStorageArea.setDesCityCode(Integer.parseInt(c.getAssortCode()));
							break;
						}
					}
					if(dmsStorageArea.getDesCityCode() == null){
						errorString = msgPre + "，城市不存在！";
						break;
					}
				}
				if(isExist(dmsStorageArea)){
					errorString = msgPre + "，已存在该省市的库位配置！";
					break;
				}
			}
			++ rowIndex;
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
					if(type == String.class){
						field.set(obj,"");
					}
				}
			} catch (IllegalAccessException e) {
				log.error("initObjectValue fail!   ",e);
			}
		}
	}

	@Override
	public boolean isExist(DmsStorageArea dmsStorageArea) {
		Integer dmsSiteCode = dmsStorageArea.getDmsSiteCode();
		Integer desProvinceCode = dmsStorageArea.getDesProvinceCode();
		Integer desCityCode = dmsStorageArea.getDesCityCode();
		DmsStorageArea oldObject = null;
		if(dmsSiteCode != null
			  &&desProvinceCode != null){
			if(dmsStorageArea.getDesCityCode()  == null){
				oldObject = dmsStorageAreaDao.findByDesProvinceCode(dmsSiteCode,desProvinceCode);
			}else {
				oldObject = dmsStorageAreaDao.findByDesProvinceAndCityCode(dmsSiteCode,desProvinceCode,desCityCode);
			}
		}
		return oldObject!=null;
	}
}
