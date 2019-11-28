package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SealVehicleResponse;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.service.BatchSendService;
import com.jd.bluedragon.distribution.seal.dao.SealVehicleDao;
import com.jd.bluedragon.distribution.seal.dao.SealVehicleReadDao;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service("sealVehicleService")
public class SealVehicleServiceImpl implements SealVehicleService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final int SEAL_CODE_SAME = 10; 
	private static final int SEAL_CODE_UNSAME = 20;
	
	private static final Integer states = 1;

	private static final Integer YN = 2; //撤销封车

	@Autowired
	private SealVehicleDao sealVehicleDao;
	
	@Autowired
	private SealVehicleReadDao sealVehicleReadDao;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
    private BaseMajorManager baseMajorManager;

	@Autowired
    private BatchSendService  batchSendService;

//	@Override
//	@Profiled(tag = "SealVehicleService.addSealVehicle")
//	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
//	public Integer add(SealVehicle sealVehicle) {
//		Assert.notNull(sealVehicle, "sealVehicle must not be null");
//		return this.sealVehicleDao.add(SealVehicleDao.namespace, sealVehicle);
//	}

//	@Profiled(tag = "SealVehicleService.updateSealVehicle")
//	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
//	public Integer update(SealVehicle sealVehicle) {
//		Assert.notNull(sealVehicle, "seal must not be null");
//		return this.sealVehicleDao
//				.update(SealVehicleDao.namespace, sealVehicle);
//	}

	@Override
	public SealVehicle findBySealCode(String sealCode) {
		Assert.notNull(sealCode, "sealCode must not be null");
		return this.sealVehicleDao.findBySealCode(sealCode);
	}

//	@Profiled(tag = "SealVehicleService.doSealVehicle")
//	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
//	public void doSealVehicle(Task task) {
//		this.taskService.doLock(task);
//		this.taskToSealVehicle(task);
//		this.taskService.doDone(task);
//	}

//	private void taskToSealVehicle(Task task) {
//		String json = task.getBody();
//		SealVehicleRequest[] array = JsonHelper.jsonToArray(json,
//				SealVehicleRequest[].class);
//		Set<SealVehicleRequest> sealBoxes = new CollectionHelper<SealVehicleRequest>()
//				.toSet(array);
//		for (SealVehicleRequest request : sealBoxes) {
//			SealVehicle seal = SealVehicle.toSealVehicle(request);
//			this.add(seal);
//		}
//	}

//	@Override
//	@Profiled(tag = "SealVehicleService.saveOrUpdate")
//	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
//	public void saveOrUpdate(SealVehicle sealVehicle) {
//		if (Constants.NO_MATCH_DATA == this.update(sealVehicle)) {
//			this.add(sealVehicle);
//		}
//	}

	@Override
	@JProfiler(jKey= "DMSWEB.SealVehicleServiceImpl.addSealVehicle",mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addSealVehicle(SealVehicle sealVehicle) {
		// 增加封车信息：先根据封车号、有效性查询封车信息是否存在
		// 若不存在，则直接增加，增加成功后，返回成功；
		// 先根据封车号、创建站点、有效性更新有效性为失效，后插入一条有效的数据；
		// 若更新不成功，则插入一条失效的数据；
		
		int result = Constants.RESULT_FAIL;
		
		if (sealVehicle == null || StringUtils.isBlank(sealVehicle.getCode())
				|| StringUtils.isBlank(sealVehicle.getVehicleCode())
				|| sealVehicle.getCreateSiteCode() == null) {
			this.log
					.error("SealVehicleServiceImpl addSealVehicle -->增加封车信息验证参数有误");
			return result;
		}

		this.log
				.info("SealVehicleServiceImpl addSealVehicle -->增加封车信息开始，封车号【"
						+ sealVehicle.getCode() + "】");

		// 验证参数，去掉更新时间、更新人相关字段
		if (sealVehicle.getCreateTime() == null) {
			sealVehicle.setCreateTime(new Date());
		}
		if (sealVehicle.getUpdateTime() != null) {
			sealVehicle.setUpdateTime(null);
		}
		if (StringUtils.isNotBlank(sealVehicle.getUpdateUser())) {
			sealVehicle.setUpdateUser(null);
		}
		if (sealVehicle.getUpdateUserCode() != null) {
			sealVehicle.setUpdateUserCode(null);
		}
		if (sealVehicle.getReceiveSiteCode() != null) {
			sealVehicle.setReceiveSiteCode(null);
		}
		
		sealVehicle.setYn(Constants.YN_YES);

		SealVehicle sealVehicleTemp = this.sealVehicleDao
				.findBySealCode(sealVehicle.getCode());

		
		if (sealVehicleTemp == null) {
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->不存在封车号【{}】，直接增加，增加成功后，返回成功；",sealVehicle.getCode());
			this.sealVehicleDao.add(SealVehicleDao.namespace, sealVehicle);
			result = Constants.RESULT_SUCCESS;
		} else {
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->存在封车号【{}】，返回失败的提示，进行后续操作",sealVehicle.getCode());
//			if (sealVehicle.getCreateSiteCode().equals(
//					sealVehicleTemp.getCreateSiteCode())) {
//				this.log
//						.info("SealVehicleServiceImpl addSealVehicle -->同一站点重复封车，封车号【"
//								+ sealVehicle.getCode() + "】");
//				this.sealVehicleDao.updateDisable(sealVehicleTemp);
//				result = Constants.RESULT_SUCCESS;
//			} else {
				this.log.warn("SealVehicleServiceImpl addSealVehicle -->不同站点重复封车，封车号【{}】",sealVehicle.getCode());
				sealVehicle.setYn(Constants.YN_NO);
//			}
			this.sealVehicleDao.add(SealVehicleDao.namespace, sealVehicle);
		}
		return result;
	}

	/**
	 * 一车一单的封车功能,添加了发货批次号,体积,重量,件数四个字段
	 * 
	 */
	@Override
	@JProfiler(jKey= "DMSWEB.SealVehicleServiceImpl.addSealVehiclel2",mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addSealVehicle2(SealVehicle sealVehicle) {
		// 增加封车信息：先根据封车号、有效性查询封车信息是否存在
		// 若不存在，则直接增加，增加成功后，返回成功；
		// 先根据封车号、创建站点、有效性更新有效性为失效，后插入一条有效的数据；
		// 若更新不成功，则插入一条失效的数据；
		
		int result = Constants.RESULT_FAIL;
		
		if (sealVehicle == null || StringUtils.isBlank(sealVehicle.getCode())
				|| StringUtils.isBlank(sealVehicle.getVehicleCode())
				|| sealVehicle.getCreateSiteCode() == null
				|| StringUtils.isBlank(sealVehicle.getSendCode())) {
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->增加封车信息验证参数有误");
			return result;
		}

		this.log.debug("SealVehicleServiceImpl addSealVehicle -->增加封车信息开始，封车号【{}】",sealVehicle.getCode());

		// 验证参数，去掉更新时间、更新人相关字段
		if (sealVehicle.getCreateTime() == null) {
			sealVehicle.setCreateTime(new Date());
		}
		if (sealVehicle.getUpdateTime() != null) {
			sealVehicle.setUpdateTime(null);
		}
		if (StringUtils.isNotBlank(sealVehicle.getUpdateUser())) {
			sealVehicle.setUpdateUser(null);
		}
		if (sealVehicle.getUpdateUserCode() != null) {
			sealVehicle.setUpdateUserCode(null);
		}
		if (sealVehicle.getReceiveSiteCode() != null) {
			sealVehicle.setReceiveSiteCode(null);
		}
		
		sealVehicle.setYn(Constants.YN_YES);

		SealVehicle sealVehicleTemp = this.sealVehicleReadDao.findBySealCodeFromRead(sealVehicle.getCode());

		if (sealVehicleTemp == null) {
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->不存在封车号【{}】，直接增加，增加成功后，返回成功；",sealVehicle.getCode());
			this.sealVehicleDao.add2(SealVehicleDao.namespace, sealVehicle);
			
			//将全程跟踪消息插入任务表
			taskService.add(this.toTask(sealVehicle));
			
			result = Constants.RESULT_SUCCESS;
		} else {
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->存在封车号【{}】，返回失败的提示，进行后续操作",sealVehicle.getCode());
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->不同站点重复封车，封车号【{}】",sealVehicle.getCode());
			sealVehicle.setYn(Constants.YN_NO);
			this.sealVehicleDao.add2(SealVehicleDao.namespace, sealVehicle);
		}
		return result;
	}
	
	private Task toTask(SealVehicle sealVehicle) {
		// SealVehicle --> WaybillStatus(没有运单号和包裹号,在Worker中获取) --> Task
		WaybillStatus waybillStatus = new WaybillStatus();
		waybillStatus.setOperatorId(sealVehicle.getCreateUserCode());
		waybillStatus.setOperator(sealVehicle.getCreateUser());
		waybillStatus.setOperateTime(sealVehicle.getCreateTime());
		waybillStatus.setCreateSiteCode(sealVehicle.getCreateSiteCode());
		
		BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(sealVehicle.getCreateSiteCode());
        if(bDto != null ){
        	waybillStatus.setCreateSiteName(bDto.getSiteName());
        }
        
		waybillStatus.setSendCode(sealVehicle.getSendCode());
		waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_SEAL_VEHICLE); //封车
		waybillStatus.setRemark(toSealDesp(sealVehicle));
		
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
		task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(waybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		return task;
	}

	private String toSealDesp(SealVehicle sealVehicle) {
		return "装车完毕，封车号为: " + (sealVehicle.getCode() == null ? "0" : sealVehicle.getCode())
				                   + "，车牌号为 : " 
				                   + (sealVehicle.getVehicleCode() == null ? "0" : sealVehicle.getVehicleCode());
	}

	@Override
	@JProfiler(jKey= "DMSWEB.SealVehicleServiceImpl.updateSealVehicle", mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateSealVehicle(SealVehicle sealVehicle) {
		// 增加解封车信息：先根据封车号、车牌号、创建站点（此参数是否存在）、有效性更新封车信息
		// 若更新成功，返回成功 1；
		// 若更新不成功，返回失败的提示 0，插入一条失效的数据；
		if (sealVehicle == null || StringUtils.isBlank(sealVehicle.getCode())
				|| StringUtils.isBlank(sealVehicle.getVehicleCode())
				|| sealVehicle.getReceiveSiteCode() == null) {
			this.log
					.error("SealVehicleServiceImpl updateSealVehicle -->增加解封车信息验证参数有误");
			return Constants.RESULT_FAIL;
		}

		// 验证参数，去掉创建时间、创建人相关字段
		if (sealVehicle.getUpdateTime() == null) {
			sealVehicle.setUpdateTime(new Date());
		}
		if (sealVehicle.getCreateTime() != null) {
			sealVehicle.setCreateTime(null);
		}
		if (StringUtils.isNotBlank(sealVehicle.getCreateUser())) {
			sealVehicle.setCreateUser(null);
		}
		if (sealVehicle.getCreateUserCode() != null) {
			sealVehicle.setCreateUserCode(null);
		}
		if (sealVehicle.getCreateSiteCode() != null) {
			sealVehicle.setCreateSiteCode(null);
		}

		this.log
				.info("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息开始，先执行更新操作，封车号【"
						+ sealVehicle.getCode()
						+ "】，车牌号【"
						+ sealVehicle.getVehicleCode() + "】");
		int tempUpdateCount = this.sealVehicleDao
				.updateSealVehicle(sealVehicle);
		if (tempUpdateCount <= 0) {
			this.log
					.info("SealVehicleServiceImpl updateSealVehicle-->解封车信息更新不存在，直接插入一条无效数据，封车号【"
							+ sealVehicle.getCode()
							+ "】，车牌号【"
							+ sealVehicle.getVehicleCode() + "】");
			sealVehicle.setYn(Constants.YN_NO);
			this.sealVehicleDao.add(SealVehicleDao.namespace, sealVehicle);
			return Constants.RESULT_FAIL;
		} else {
			this.log
					.info("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息成功，封车号【"
							+ sealVehicle.getCode()
							+ "】，车牌号【"
							+ sealVehicle.getVehicleCode() + "】");
			return Constants.RESULT_SUCCESS;
		}
	}

	@Override
	@JProfiler(jKey= "DMSWEB.SealVehicleServiceImpl.updateSealVehicle2",mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateSealVehicle2(SealVehicle sealVehicle) {
		// 增加解封车信息：先根据封车号、车牌号、创建站点（此参数是否存在）、有效性更新封车信息
		// 若更新成功，返回成功 1；
		// 若更新不成功，返回失败的提示 0，插入一条失效的数据；
		if (sealVehicle == null || StringUtils.isBlank(sealVehicle.getCode())
				|| StringUtils.isBlank(sealVehicle.getVehicleCode())
				|| sealVehicle.getReceiveSiteCode() == null) {
			this.log
					.error("SealVehicleServiceImpl updateSealVehicle -->增加解封车信息验证参数有误");
			return Constants.RESULT_FAIL;
		}

		// 校验:当前批次号下,封签号是否一致
		Integer result = null;
		List<SealVehicle> sealVehicleList = this.sealVehicleReadDao.findBySendCode(sealVehicle.getSendCode());
		result = this.checkSealCode(sealVehicleList, sealVehicle.getCode());
		
		// 验证参数，去掉创建时间、创建人相关字段
		if (sealVehicle.getUpdateTime() == null) {
			sealVehicle.setUpdateTime(new Date());
		}
		if (sealVehicle.getCreateTime() != null) {
			sealVehicle.setCreateTime(null);
		}
		if (StringUtils.isNotBlank(sealVehicle.getCreateUser())) {
			sealVehicle.setCreateUser(null);
		}
		if (sealVehicle.getCreateUserCode() != null) {
			sealVehicle.setCreateUserCode(null);
		}
		if (sealVehicle.getCreateSiteCode() != null) {
			sealVehicle.setCreateSiteCode(null);
		}

		this.log.debug("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息开始，先执行更新操作，封车号【{}】，车牌号【{}】"
				,sealVehicle.getCode(),sealVehicle.getVehicleCode());
		int tempUpdateCount = this.sealVehicleDao.updateSealVehicle2(sealVehicle);
		taskService.add(this.toTask(sealVehicle,sealVehicleList,result)); //将全程跟踪消息插入任务表
		if (tempUpdateCount <= 0) {
			this.log.warn("SealVehicleServiceImpl updateSealVehicle-->解封车信息更新不存在，直接插入一条无效数据，封车号【{}】，车牌号【{}】"
					,sealVehicle.getCode(),sealVehicle.getVehicleCode());
			sealVehicle.setYn(Constants.YN_NO);
			this.sealVehicleDao.add2(SealVehicleDao.namespace, sealVehicle);
			if(result != SealVehicleServiceImpl.SEAL_CODE_SAME){
				return SealVehicleServiceImpl.SEAL_CODE_UNSAME;
			}else{
				return Constants.RESULT_FAIL;
			}
		} else {
			this.log.debug("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息成功，封车号【{}】，车牌号【{}】"
					,sealVehicle.getCode(),sealVehicle.getVehicleCode());
			return Constants.RESULT_SUCCESS;
		}
	}
	
	private Task toTask(SealVehicle sealVehicle, List<SealVehicle> sealVehicleList, Integer result) {
		// SealVehicle --> WaybillStatus(没有运单号和包裹号,在Worker中获取) --> Task
		WaybillStatus waybillStatus = new WaybillStatus();
		waybillStatus.setOperatorId(sealVehicle.getUpdateUserCode());
		waybillStatus.setOperator(sealVehicle.getUpdateUser());
		waybillStatus.setOperateTime(sealVehicle.getUpdateTime());
		waybillStatus.setReceiveSiteCode(sealVehicle.getReceiveSiteCode());
		
		BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(sealVehicle.getReceiveSiteCode());
        if(bDto != null ){
        	waybillStatus.setReceiveSiteName(bDto.getSiteName());
        }
        
		waybillStatus.setSendCode(sealVehicle.getSendCode());
		waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_UNSEAL_VEHICLE); //解封车
		waybillStatus.setRemark(toUnsealDesp(sealVehicle, sealVehicleList, result)); 
		
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
		task.setReceiveSiteCode(waybillStatus.getReceiveSiteCode());
		task.setBody(JsonHelper.toJson(waybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		return task;
	}


	private String toUnsealDesp(SealVehicle sealVehicle, List<SealVehicle> sealVehicleList, Integer result) {
		StringBuilder sb = new StringBuilder();
		sb.append("开始卸货，封签号【");
		if(result == SealVehicleServiceImpl.SEAL_CODE_SAME){
			sb.append("无");
		}else{
			sb.append("有");
		}
		sb.append("异常；当前扫描封签号为");
		sb.append(sealVehicle.getCode());
		sb.append("；正常封签号为");
		for(int i = 0, length = sealVehicleList.size(); i < length; i++){
			if(i == length - 1){
				sb.append(sealVehicleList.get(i).getCode());
			}else{
				sb.append(sealVehicleList.get(i).getCode() + "，");
			}
		}
		return sb.toString();
	}

	private Integer checkSealCode(List<SealVehicle> sealVehicleList , String sealCode){
		for(SealVehicle sealVehicle : sealVehicleList){
			if(null != sealVehicle && StringUtils.isNotBlank(sealVehicle.getCode())
								   && StringUtils.isNotBlank(sealCode)
			                       && sealVehicle.getCode().equals(sealCode)){
				return SealVehicleServiceImpl.SEAL_CODE_SAME;				
			}
		}
		return SealVehicleServiceImpl.SEAL_CODE_UNSAME;
	}
	
	@Override
	public List<SealVehicle> findBySendCode(String sendCode) {
		Assert.notNull(sendCode, "sendCode must not be null");
		return this.sealVehicleReadDao.findBySendCode(sendCode);
	}

	/******************* 封车与解封车:封车号与批次号多对多关系   ***************************/
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean addSealVehicle3(List<SealVehicle> sealVehicleList) {
		for(SealVehicle sealVehicle : sealVehicleList){
			if (sealVehicle == null || StringUtils.isBlank(sealVehicle.getCode())
					|| StringUtils.isBlank(sealVehicle.getVehicleCode())
					|| sealVehicle.getCreateSiteCode() == null
					|| StringUtils.isBlank(sealVehicle.getSendCode())) {
				this.log.warn("SealVehicleServiceImpl addSealVehicle --> 增加封车信息验证参数有误");
				return false;
			}
			
			// 验证参数，去掉更新时间、更新人相关字段
			if (sealVehicle.getCreateTime() == null) {
				sealVehicle.setCreateTime(new Date());
			}
			if (sealVehicle.getUpdateTime() != null) {
				sealVehicle.setUpdateTime(null);
			}
			if (StringUtils.isNotBlank(sealVehicle.getUpdateUser())) {
				sealVehicle.setUpdateUser(null);
			}
			if (sealVehicle.getUpdateUserCode() != null) {
				sealVehicle.setUpdateUserCode(null);
			}
			if (sealVehicle.getReceiveSiteCode() != null) {
				sealVehicle.setReceiveSiteCode(null); 
			}
			sealVehicle.setYn(Constants.YN_YES);
		}

		String sealCodes = getSealCodes(sealVehicleList);
		this.log.debug("SealVehicleServiceImpl addSealVehicle -->增加封车信息开始，封车号【{}】",sealCodes);
		
		List<SealVehicle> sealVehicleTempList = this.sealVehicleReadDao.findBySealCodes(sealVehicleList);
		if(sealVehicleTempList == null || sealVehicleTempList.size() < 1){
			this.log.warn("SealVehicleServiceImpl addSealVehicle -->不存在封车号【{}】，直接增加，增加成功后，返回成功；",sealCodes);
			
			this.sealVehicleDao.addBatch(sealVehicleList);
			
			//将全程跟踪消息插入任务表
			taskService.add(this.toSealTask(sealVehicleList));
			
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean updateSealVehicle3(SealVehicle sealVehicle, String sealCodes) {
		if (sealVehicle == null 
				|| StringUtils.isBlank(sealVehicle.getVehicleCode())
				|| sealVehicle.getReceiveSiteCode() == null
				|| StringUtils.isBlank(sealCodes)) {
			this.log.warn("SealVehicleServiceImpl updateSealVehicle -->增加解封车信息验证参数有误");
			return false;
		}			
		// 验证参数，去掉创建时间、创建人相关字段
		if (sealVehicle.getUpdateTime() == null) {
			sealVehicle.setUpdateTime(new Date());
		}
		if (sealVehicle.getCreateTime() != null) {
			sealVehicle.setCreateTime(null);
		}
		if (StringUtils.isNotBlank(sealVehicle.getCreateUser())) {
			sealVehicle.setCreateUser(null);
		}
		if (sealVehicle.getCreateUserCode() != null) {
			sealVehicle.setCreateUserCode(null);
		}
		if (sealVehicle.getCreateSiteCode() != null) {
			sealVehicle.setCreateSiteCode(null);
		}

		// 根据车牌号,查询有效的批次号
		List<SealVehicle> sealVehicleDBList = this.sealVehicleReadDao.findByVehicleCode(sealVehicle.getVehicleCode());
		if(sealVehicleDBList == null || sealVehicleDBList.size() < 1){
			this.log.warn("根据车牌号{}, 查询的消息为空",sealVehicle.getVehicleCode());
			return false;
		}
		Map<String, Object> logMap = getLog(sealVehicleDBList, sealCodes);
		this.log.debug("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息开始，先执行更新操作，车牌号为[{}]，有效封车号为{}，异常封车号为{}，原始封车号为{}"
				,sealVehicleDBList.get(0).getVehicleCode(),logMap.get("valid"),logMap.get("invalid"),logMap.get("original"));
		int updateCount = this.sealVehicleDao.updateBatch(sealVehicle);
		if (updateCount == sealVehicleDBList.size()) {
			this.log.debug("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息开始，先执行更新操作，车牌号为[{}]，有效封车号为{}，异常封车号为{}，原始封车号为{}"
					,sealVehicleDBList.get(0).getVehicleCode(),logMap.get("valid"),logMap.get("invalid"),logMap.get("original"));
			taskService.add(this.toUnSealTask(sealVehicleDBList, sealVehicle)); //将全程跟踪消息插入任务表
			return true;
		} else {
			this.log.warn("SealVehicleServiceImpl updateSealVehicle-->增加解封车信息失败，车牌号为[{}]，有效封车号为{}，异常封车号为{}，原始封车号为{}"
					,sealVehicleDBList.get(0).getVehicleCode(),logMap.get("valid"),logMap.get("invalid"),logMap.get("original"));
			return false;
		}
	}

	private Map<String, Object> getLog(List<SealVehicle> sealVehicleDBList, String sealCodes) {
		Map<String, Object> logMap = new HashMap<String, Object>();
		List<String> validSealCodeList = new ArrayList<String>();
		List<String> invalidSealCodeList = new ArrayList<String>();
		Set<String> sealCodeDBSet = new HashSet<String>();
		for(SealVehicle sv : sealVehicleDBList){
			if(sv.getCode() != null && sv.getCode().length() > 1){
				sealCodeDBSet.add(sv.getCode());
			}
		}
		List<String> sealCodeList = StringHelper.parseList(sealCodes, ",");
		for(String str : sealCodeList){
			if(sealCodeDBSet.contains(str)){
				validSealCodeList.add(str);
			}else{
				invalidSealCodeList.add(str);
			}
		}
		logMap.put("original", sealCodeDBSet.toString());
		logMap.put("valid", validSealCodeList.toString());
		logMap.put("invalid", invalidSealCodeList.toString());
		return logMap;
	}

	private Task toSealTask(List<SealVehicle> sealVehicleList) {
		// SealVehicle --> WaybillStatus(没有运单号和包裹号,在Worker中获取) --> Task
		WaybillStatus waybillStatus = new WaybillStatus();
		SealVehicle sealVehicle = sealVehicleList.get(0);
		waybillStatus.setOperatorId(sealVehicle.getCreateUserCode());
		waybillStatus.setOperator(sealVehicle.getCreateUser());
		waybillStatus.setOperateTime(sealVehicle.getCreateTime());
		waybillStatus.setCreateSiteCode(sealVehicle.getCreateSiteCode());
		waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_SEAL_VEHICLE); //封车
		waybillStatus.setSendCode(getSendCodes(sealVehicleList));
		waybillStatus.setRemark(toSealDesp(sealVehicleList));
		BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(sealVehicle.getCreateSiteCode());
		if(bDto != null ){
	        waybillStatus.setCreateSiteName(bDto.getSiteName());
        }
        
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
		task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(waybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		return task;
	}
	
	private Task toUnSealTask(List<SealVehicle> sealVehicleDBList, SealVehicle sealVehicle) {
		WaybillStatus waybillStatus = new WaybillStatus();
		//SealVehicle sealVehicleDB = sealVehicleDBList.get(0);
		waybillStatus.setOperatorId(sealVehicle.getUpdateUserCode());
		waybillStatus.setOperator(sealVehicle.getUpdateUser());
		waybillStatus.setOperateTime(sealVehicle.getUpdateTime());
		waybillStatus.setReceiveSiteCode(sealVehicle.getReceiveSiteCode());
		waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_UNSEAL_VEHICLE); //解封车
		waybillStatus.setSendCode(getSendCodes(sealVehicleDBList));
		waybillStatus.setRemark(toUnSealDesp(sealVehicleDBList));
		Integer receiveSiteCode = sealVehicle.getReceiveSiteCode();
		if(receiveSiteCode != null && receiveSiteCode > 0){
			BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
			if(bDto != null ){
		        waybillStatus.setReceiveSiteName(bDto.getSiteName());
	        }
		}
        
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
		task.setReceiveSiteCode(waybillStatus.getReceiveSiteCode());
		task.setBody(JsonHelper.toJson(waybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		return task;
	}
	
	public static String getSealCodes(List<SealVehicle> sealVehicleList){
		String sealCodes = "";
		boolean isFirst = true;
		Set<String> sealCodeSet = new HashSet<String>();
		for(SealVehicle sv : sealVehicleList){
			sealCodeSet.add(sv.getCode());
		}
		for(String str : sealCodeSet){
			if(isFirst){
				sealCodes += str;
				isFirst = false;
			}else{
				sealCodes += "," + str;
			}
		}
		return sealCodes;
	}
	
	public static String getSendCodes(List<SealVehicle> sealVehicleList){
		String sendCodes = "";
		boolean isFirst = true;
		Set<String> sendCodeSet = new HashSet<String>();
		for(SealVehicle sv : sealVehicleList){
			sendCodeSet.add(sv.getSendCode());
		}
		for(String str : sendCodeSet){
			if(isFirst){
				sendCodes += str;
				isFirst = false;
			}else{
				sendCodes += "," + str;
			}
		}
		return sendCodes;
	}
	
	public static String toSealDesp(List<SealVehicle> sealVehicleList){
		return "封车完毕，封车号为: " + getSealCodes(sealVehicleList) + "，车牌号为 : " 
                + sealVehicleList.get(0).getVehicleCode();
	}
	
	public static String toUnSealDesp(List<SealVehicle> sealVehicleList){
		return "解封车完毕，封车号为: " + getSealCodes(sealVehicleList) + "，车牌号为 : " 
                + sealVehicleList.get(0).getVehicleCode();
	}

	@Override
	public List<SealVehicle> findByVehicleCode(String vehicleCode) {
		log.debug("SealVehicleServiceImpl.findByVehicleCode begin...{}",vehicleCode);
		return sealVehicleReadDao.findByVehicleCode(vehicleCode);
	}

	@Override
	public SealVehicleResponse cancelSealVehicle(SealVehicle sealVehicle) {

		sealVehicle.setCreateTime(new Date());
		//判断该批次是否已经发车
		BatchSend batchSend= batchSendService.readBySendCode(sealVehicle.getSendCode());
		if(batchSend!=null && states.equals(batchSend.getSendCarState())){
			return new SealVehicleResponse(SealVehicleResponse.CODE_2007_ERROR, SealVehicleResponse.MESSAGE_2007_ERROR);
		}
		//撤销增加yn=2的值
		sealVehicle.setYn(YN);
		this.sealVehicleDao.add2(SealVehicleDao.namespace, sealVehicle);

		//发送全程跟踪
		taskService.add(this.toCancelTask(sealVehicle));
		return new SealVehicleResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
	}

	private Task toCancelTask(SealVehicle sealVehicle) {
		WaybillStatus waybillStatus = new WaybillStatus();
		waybillStatus.setOperatorId(sealVehicle.getCreateUserCode());
		waybillStatus.setOperator(sealVehicle.getCreateUser());
		waybillStatus.setOperateTime(sealVehicle.getCreateTime());
		waybillStatus.setCreateSiteCode(sealVehicle.getCreateSiteCode());
		waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_CANCEL_VEHICLE); //撤销封车
		waybillStatus.setSendCode(sealVehicle.getSendCode());
		BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(sealVehicle.getCreateSiteCode());
        if(bDto != null ){
        	waybillStatus.setCreateSiteName(bDto.getSiteName());
        }

		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
		task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(waybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		return task;
	}

}
