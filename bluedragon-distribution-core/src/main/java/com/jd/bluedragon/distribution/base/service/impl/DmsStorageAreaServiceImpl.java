package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ErpUserClient;
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

	@Override
	public Dao<DmsStorageArea> getDao() {
		return this.dmsStorageAreaDao;
	}

	@Cache(key = "sysconfig.cache.@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public DmsStorageArea findByProAndCity( Integer dmsSiteCode,Integer dmsProvinceCode,Integer dmsCityCode){

		return dmsStorageAreaDao.findByProAndCity(dmsSiteCode,dmsProvinceCode,dmsCityCode);
	}

	public  DmsStorageArea getUserInfo(DmsStorageArea dmsStorageArea){
		//获得登陆人信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		BaseStaffSiteOrgDto baseStaffByErpNoCache = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
		dmsStorageArea.setStorageType(1);
		dmsStorageArea.setDmsSiteCode(baseStaffByErpNoCache.getDmsId());
		dmsStorageArea.setDmsSiteName(baseStaffByErpNoCache.getDmsName());
		if(dmsStorageArea.getId() == null){
			dmsStorageArea.setCreateUserCode(erpUser.getUserId());
			dmsStorageArea.setCreateUserName(erpUser.getUserName());
			dmsStorageArea.setUpdateUserCode(erpUser.getUserId());
			dmsStorageArea.setUpdateUserName(erpUser.getUserName());
			dmsStorageArea.setCreateTime(new Date());
			dmsStorageArea.setUpdateTime(new Date());
		}else {
			dmsStorageArea.setUpdateTime(new Date());
			dmsStorageArea.setUpdateUserCode(erpUser.getUserId());
			dmsStorageArea.setUpdateUserName(erpUser.getUserName());
		}
		return dmsStorageArea;
	}

	public Boolean importExcel(List<DmsStorageArea> dataList, String createUserCode, String createUserName, Date createTime){
		List<DmsStorageArea> bufferList = new ArrayList<DmsStorageArea>();
		for(DmsStorageArea dmsStorageArea : dataList){
			dmsStorageArea.setCreateUserCode(Integer.parseInt(createUserCode));
			dmsStorageArea.setCreateUserName(createUserName);
			dmsStorageArea.setUpdateUserName(createUserName);
			dmsStorageArea.setUpdateTime(createTime);
			dmsStorageArea.setCreateTime(createTime);
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
