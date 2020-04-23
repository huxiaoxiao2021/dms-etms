package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpaceCondition;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpaceTransportTypeEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.dao.ArBookingSpaceDao;
import com.jd.bluedragon.distribution.transport.service.ArBookingSpaceService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: ArBookingSpaceServiceImpl
 * @Description: 空铁项目-订舱表--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Service("arBookingSpaceService")
public class ArBookingSpaceServiceImpl extends BaseService<ArBookingSpace> implements ArBookingSpaceService {

	@Autowired
	@Qualifier("arBookingSpaceDao")
	private ArBookingSpaceDao arBookingSpaceDao;

	@Override
	public Dao<ArBookingSpace> getDao() {
		return this.arBookingSpaceDao;
	}

	@Override
	public List<List<Object>> getExportData(ArBookingSpaceCondition arBookingSpaceCondition) {

		List<List<Object>> resList = new ArrayList<List<Object>>();

		List<Object> heads = new ArrayList<Object>();

		heads.add("预计起飞日期");
		heads.add("运力名称");
		heads.add("运力类型");
		heads.add("起飞城市");
		heads.add("落地城市");
		heads.add("航班优先级");
		heads.add("可获取舱位kg");
		heads.add("计划订舱位kg");
		heads.add("实际订舱位kg");
		heads.add("预计起飞日期");
		heads.add("预计落地日期");
		heads.add("订舱日期");
		heads.add("供应商名称");
		heads.add("联系电话");
		heads.add("备注");

		heads.add("操作时间");
		heads.add("操作人");
		heads.add("分拣名称");

		resList.add(heads);


		arBookingSpaceCondition.setLimit(-1);
		PagerResult<ArBookingSpace> queryByPagerCondition = this.queryByPagerCondition(arBookingSpaceCondition);

		List<ArBookingSpace> rows = queryByPagerCondition.getRows();

		for(ArBookingSpace arbs : rows){
			List<Object> body = new ArrayList<Object>();
			//预计起飞日期
			body.add(DateHelper.formatDate(arbs.getPlanStartDate(),Constants.DATE_FORMAT));

			//运力名称
			body.add(arbs.getTransportName());
			//运力类型
			body.add(arbs.getTransportType()==null?"":ArBookingSpaceTransportTypeEnum.getNameByKey(arbs.getTransportType().toString()));
			//起飞城市
			body.add(arbs.getStartCityName());
			//落地城市
			body.add(arbs.getEndCityName());
			//航班优先级
			body.add(arbs.getPriority());

			//可获取舱位kg
			body.add(arbs.getGainSpace());
			//计划订舱位kg
			body.add(arbs.getPlanSpace());
			//实际订舱位kg
			body.add(arbs.getRealSpace());
			//预计起飞日期
			body.add(DateHelper.formatDate(arbs.getPlanStartTime(),"HH:mm"));
			//预计落地日期
			body.add(DateHelper.formatDate(arbs.getPlanEndTime(),"HH:mm"));

			//订舱日期
			body.add(DateHelper.formatDate(arbs.getBookingSpaceTime()));
			//供应商名称
			body.add(arbs.getSupplierName());
			//联系电话
			body.add(arbs.getPhone());
			//备注
			body.add(arbs.getRemark());
			//操作时间
			body.add(DateHelper.formatDate(arbs.getOperateTime(), Constants.DATE_TIME_FORMAT));
			//操作人
			body.add(arbs.getOperatorErp());
			//分拣名称
			body.add(arbs.getCreateSiteName());

			resList.add(body);
		}

		return resList;
	}


	@Transactional(propagation = Propagation.REQUIRED)
	public boolean saveOrUpdate(ArBookingSpace arBookingSpace,String userCode,String userName,Long createSiteCode,String createSiteName) {



		ArBookingSpace oldData = this.find(arBookingSpace);

		arBookingSpace.setOperatorErp(userCode);
		arBookingSpace.setOperatorName(userName);
		arBookingSpace.setUpdateUser(userCode);

		if(oldData != null){
			arBookingSpace.setId(oldData.getId());
			return this.getDao().update(arBookingSpace);
		}else{
			arBookingSpace.setCreateSiteCode(createSiteCode);
			arBookingSpace.setCreateSiteName(createSiteName);
			arBookingSpace.setCreateUser(userCode);
			return this.getDao().insert(arBookingSpace);
		}

	}

	/**
	 * 批量导入 整体事物提交
	 * @param list
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean importExcel(List<ArBookingSpace> list,String userCode,String userName,Long createSiteCode,String createSiteName) {
		List<ArBookingSpace> bufferList = new ArrayList<ArBookingSpace>();
		for(ArBookingSpace arBookingSpace : list){

			arBookingSpace.setOperatorErp(userCode);
			arBookingSpace.setOperatorName(userName);
			arBookingSpace.setUpdateUser(userCode);
			arBookingSpace.setCreateSiteCode(createSiteCode);
			arBookingSpace.setCreateSiteName(createSiteName);
			arBookingSpace.setCreateUser(userCode);
			//批量插入 默认值
			initObjectValue(arBookingSpace);

			bufferList.add(arBookingSpace);

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

	/**
	 * 批量导入
	 * 初始化字段
	 * 转换枚举类型数据
	 * @param obj
	 */
	private void initObjectValue(ArBookingSpace obj){

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
				}else{
					if(field.getName().equals("transportTypeForExcel")){
						//特殊处理转换一下运力类型
						ArBookingSpaceTransportTypeEnum _e = ArBookingSpaceTransportTypeEnum.getEnumByValue(v.toString());

						if(_e==null){
							throw new IllegalArgumentException("运力类型数据转换错误！请检查该字段");
						}
						String changeV = _e.getCode();
						obj.setTransportType(new Integer(changeV));
					}
				}

			} catch (IllegalAccessException e) {
				log.error("initObjectValue fail!   ",e);
			}

		}

	}

}
