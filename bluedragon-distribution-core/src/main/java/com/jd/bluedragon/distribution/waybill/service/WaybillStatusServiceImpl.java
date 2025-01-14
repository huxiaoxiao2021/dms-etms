package com.jd.bluedragon.distribution.waybill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.TerminalManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.domain.PackageHalfReasonTypeEnum;
import com.jd.bluedragon.distribution.half.domain.PackageHalfResultTypeEnum;
import com.jd.bluedragon.distribution.inventory.service.PackageStatusService;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.common.Result;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillParameter;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.OrderShipsDto;
import com.jd.etms.waybill.handler.PackageSyncPartParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameterExtend;
import com.jd.etms.waybill.handler.WaybillSyncPartParameter;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("waybillStatusService")
public class WaybillStatusServiceImpl implements WaybillStatusService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final Integer SITE_TYPE_SPWMS = 903;
	private static final String MERGE_WAYBILL_RETURN_COUNT = "mergeWaybillReturnCount";

	@Autowired
	private WaybillSyncApi waybillSyncApi;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SiteService siteService;
    
    @Autowired
    private SendDatailDao sendDatailDao;

	@Autowired
	private BoxService boxService;

	@Autowired
	private SortingService sortingService;

	@Autowired
	private StoragePackageMService storagePackageMService;

	@Autowired
	private PackageStatusService packageStatusService;
	
    @Autowired
    private WaybillCancelService waybillCancelService;

	@Autowired
	private TerminalManager terminalManager;

	@Autowired
	private JyOperateFlowService jyOperateFlowService;

	public void sendModifyWaybillStatusNotify(List<Task> tasks) throws Exception{
		if (tasks.isEmpty()) {
			return;
		}
		List<WaybillSyncParameter> parameterList = this.parseWaybillSyncParameter(tasks);
		if(log.isInfoEnabled()){
			log.info(JSON.toJSONString(parameterList));
		}

		CallerInfo info = Profiler.registerInfo("DMSWORKER.WaybillStatusService.batchUpdateWaybillByOperateCode",Constants.UMP_APP_NAME_DMSWORKER,false, true);

		Map<Long, Result> results;
		try{
			results = this.waybillSyncApi.batchUpdateWaybillByOperateCode(parameterList);
		}catch (Exception e){
			Profiler.functionError(info);
			throw e;
		}finally {
			Profiler.registerInfoEnd(info);
		}

		if (results == null || results.isEmpty()) {
            if(log.isInfoEnabled()){
                log.info("回传运单状态数据转换为空:{}",JsonHelper.toJson(tasks));
            }
			return;
		}

		for (Map.Entry<Long, Result> mResult : results.entrySet()) {

			if (mResult != null) {
				Long taskId = mResult.getKey();
				Result result = mResult.getValue();
				log.info("回传运单状态taskId:{}->resultCode:{}->resultMessage{}",taskId,result.getCode(),result.getMessage());
				if (true == result.isFlag()) {
					try{
						packageStatusService.recordPackageStatus(parameterList,null);
						packageStatusService.filterAndSendDmsHasnoPresiteWaybillMq(parameterList,null);
					}catch (Exception e){
						log.error("包裹状态发送MQ消息异常:{}" , JSON.toJSONString(parameterList),e);
					}
				} else if (WaybillStatus.RESULT_CODE_PARAM_IS_NULL == result.getCode()
						|| WaybillStatus.RESULT_CODE_REPEAT_TASK == result.getCode()) {
					this.log.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
//					this.taskService.doError(this.findTask(tasks, taskId, Task.TASK_TYPE_WAYBILL));
				} else {
                    this.log.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
//					this.taskService.doRevert(this.findTask(tasks, taskId, Task.TASK_TYPE_WAYBILL));
				}
			}
		}


		//额外做一些处理 ，此环节可针对某些特定更新运单状态时处理
		doSomethingOnWaybillStatus(tasks);
	}

	/**
	 * 额外业务逻辑
	 * 1、当更新运单的状态为发货时，修改暂存上架的暂存状态
	 * @param tasks
	 */
	private void doSomethingOnWaybillStatus(List<Task> tasks){
		for (Task task : tasks) {
			if (StringHelper.isEmpty(task.getBody())) {
				continue;
			}
			if (task.getYn()!=null && task.getYn().equals(0)) {
				continue;
			}

			WaybillStatus waybillStatus = JsonHelper.fromJson(task.getBody(), WaybillStatus.class);

			//发货节点
			if(waybillStatus!=null && waybillStatus.getOperateType()!=null && WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY.equals(waybillStatus.getOperateType())){
				//同步暂存上架的运单状态
				storagePackageMService.updateStatusOnSend(waybillStatus.getWaybillCode(),waybillStatus.getPackageCode());
			}

		}
	}

	public void sendModifyWaybillStatusFinished(Task task) throws Exception{

		BaseEntity<Boolean> result = this.waybillSyncApi.batchUpdateWaybillByWaybillCode(this.parseWaybillParameter(task), 8);

		if (result != null && result.getData() == true) {
//			this.taskService.doDone(task);
			if(log.isDebugEnabled()){
				log.debug("开始妥投运单成功:{}" , JsonHelper.toJson(task));
			}
		} else {
			log.error("置妥投状态失败:{}" , JsonHelper.toJson(task) );
            throw new Exception("置妥投状态失败\n" + JsonHelper.toJson(result) + "\n");
//			this.taskService.doError(task);
		}
	}

	private void toWaybillStatus(WaybillStatus tWaybillStatus,
			BdTraceDto bdTraceDto) {
		bdTraceDto.setOperateType(tWaybillStatus.getOperateType());
		bdTraceDto.setOperatorSiteId(tWaybillStatus.getCreateSiteCode());
		bdTraceDto.setOperatorSiteName(tWaybillStatus.getCreateSiteName());
		bdTraceDto.setOperatorTime(tWaybillStatus.getOperateTime());
		bdTraceDto.setOperatorUserName(tWaybillStatus.getOperator());
		bdTraceDto.setPackageBarCode(tWaybillStatus.getPackageCode());
		bdTraceDto.setWaybillCode(tWaybillStatus.getWaybillCode());
        bdTraceDto.setOperatorUserId(null!=tWaybillStatus.getOperatorId()?tWaybillStatus.getOperatorId():0);
        setExtendParameter(tWaybillStatus,bdTraceDto);

	}
	
	// 没有注入运单号和包裹号 (封车)
	private void toWaybillStatus2(WaybillStatus tWaybillStatus, BdTraceDto bdTraceDto) {
		bdTraceDto.setOperateType(tWaybillStatus.getOperateType());
		bdTraceDto.setOperatorSiteId(tWaybillStatus.getCreateSiteCode());
		bdTraceDto.setOperatorSiteName(tWaybillStatus.getCreateSiteName());
		bdTraceDto.setOperatorTime(tWaybillStatus.getOperateTime());
		bdTraceDto.setOperatorUserName(tWaybillStatus.getOperator());
        bdTraceDto.setOperatorUserId(null!=tWaybillStatus.getOperatorId()?tWaybillStatus.getOperatorId():0);
        setExtendParameter(tWaybillStatus,bdTraceDto);
	}
	/**
	 * 设置扩展属性
	 * @param tWaybillStatus
	 * @param bdTraceDto
	 */
	private void setExtendParameter(WaybillStatus tWaybillStatus, BdTraceDto bdTraceDto) {
        if(tWaybillStatus.getOperatorData() != null
        		&& OperatorTypeEnum.AUTO_MACHINE.getCode().equals(tWaybillStatus.getOperatorData().getOperatorTypeCode())) {
			tWaybillStatus.putExtendParamMap(WaybillStatus.EXTEND_PARAMETER_EQUIPMENT_CODE, tWaybillStatus.getOperatorData().getOperatorId());
        }
		if(tWaybillStatus.getExtendParamMap() != null && !tWaybillStatus.getExtendParamMap().isEmpty()){
			bdTraceDto.setExtendParameter(tWaybillStatus.getExtendParamMap());
		}
	}
	/**
	 * tWaybillStatus转换成运单的对象WaybillSyncParameter
	 * @param tWaybillStatus
	 * @return
	 */
	private WaybillSyncParameter toWaybillSyncParameter(WaybillStatus tWaybillStatus) {
		if(tWaybillStatus == null) {
			return null;
		}
		WaybillSyncParameter parameter = new WaybillSyncParameter();
		if(tWaybillStatus.getOperatorData() != null
        		&& OperatorTypeEnum.AUTO_MACHINE.getCode().equals(tWaybillStatus.getOperatorData().getOperatorTypeCode())) {
			tWaybillStatus.putExtendParamMap(WaybillStatus.EXTEND_PARAMETER_EQUIPMENT_CODE, tWaybillStatus.getOperatorData().getOperatorId());
        }
		if(tWaybillStatus.getExtendParamMap() != null && !tWaybillStatus.getExtendParamMap().isEmpty()){
			parameter.setExtendSyncParam(tWaybillStatus.getExtendParamMap());
		}
		return parameter;
	}
	// 没有注入运单号和包裹号 (解封车)
	private void toWaybillStatus3(WaybillStatus tWaybillStatus, BdTraceDto bdTraceDto) {
		bdTraceDto.setOperateType(tWaybillStatus.getOperateType());
		bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
		bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
		bdTraceDto.setOperatorTime(tWaybillStatus.getOperateTime());
		bdTraceDto.setOperatorUserName(tWaybillStatus.getOperator());
        bdTraceDto.setOperatorUserId(null!=tWaybillStatus.getOperatorId()?tWaybillStatus.getOperatorId():0);
        setExtendParameter(tWaybillStatus,bdTraceDto);
	}

	private Task findTask(List<Task> tasks, Long taskId, Integer taskType) {
		for (Task task : tasks) {
			if (taskId.equals(task.getId())) {
				task.setType(taskType);
				return task;
			}
		}

		this.log.info("未找到对应Task, 非法任务ID:{}" , taskId);

		return null;
	}

	private List<WaybillParameter> parseWaybillParameter(Task task) {

		if(log.isDebugEnabled()){
			log.debug("开始妥投自营订单:{}",JsonHelper.toJson(task));
		}
		if (StringHelper.isEmpty(task.getBody())) {
				return  null;
			}
			if (task.getYn()!=null && task.getYn().equals(0)) {
				return  null;
			}
		if(log.isDebugEnabled()){
			log.debug("置妥投状态:{}",JsonHelper.toJson(task));
		}
		WaybillParameter[] arrays = JsonHelper.jsonToArray(task.getBody(),
				WaybillParameter[].class);

		List<WaybillParameter> params = Arrays.asList(arrays);
		if(log.isDebugEnabled()){
			log.debug("传送运单参数:{}",JsonHelper.toJson(params));
		}

		return params;
	}

	private List<WaybillSyncParameter> parseWaybillSyncParameter(List<Task> tasks) {
		List<WaybillSyncParameter> params = new ArrayList<WaybillSyncParameter>();

		for (Task task : tasks) {
			if (StringHelper.isEmpty(task.getBody())) {
				continue;
			}
			if (task.getYn()!=null && task.getYn().equals(0)) {
                continue;
			}
            /**
             * 作者：wnagtingwei@jd.com
             * 逆向换单打印不需要更新运单状态，故此处过滤数据
             */
            if(null!=task.getKeyword2()&& String.valueOf(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT).equals(task.getKeyword2().trim())){
                continue;
            }
            if(null!=task.getKeyword2()&& String.valueOf(3800).equals(task.getKeyword2().trim())){
                continue;
            }
            if(null!=task.getKeyword2()&& String.valueOf(WaybillStatus.WAYBILL_TRACK_SITE_LABEL_PRINT).equals(task.getKeyword2().trim())){
                continue;
            }
			WaybillStatus waybillStatus = JsonHelper.fromJson(task.getBody(), WaybillStatus.class);
			WaybillSyncParameter param = toWaybillSyncParameter(waybillStatus);

			Integer operateType = waybillStatus.getOperateType();
			if (WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_SORTING.equals(operateType)
			        && WaybillStatusServiceImpl.SITE_TYPE_SPWMS.equals(waybillStatus
			                .getReceiveSiteType())) {
				param.setOperatorCode(waybillStatus.getWaybillCode());
			} else if (WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_DELIVERY.equals(operateType)
			        && WaybillStatusServiceImpl.SITE_TYPE_SPWMS.equals(waybillStatus
			                .getReceiveSiteType())) {
				param.setOperatorCode(waybillStatus.getWaybillCode());
			} else {
				param.setOperatorCode(waybillStatus.getPackageCode());
			}

			param.setOperatorId(waybillStatus.getOperatorId());
			param.setOperatorName(waybillStatus.getOperator());
			param.setOperateTime(waybillStatus.getOperateTime());
			param.setOrgId(waybillStatus.getOrgId());
			param.setOrgName(waybillStatus.getOrgName());
			param.setZdId(waybillStatus.getCreateSiteCode());
			param.setZdName(waybillStatus.getCreateSiteName());
			param.setZdType(waybillStatus.getCreateSiteType());
			param.setRemark(waybillStatus.getRemark());

			if(WaybillStatus.WAYBILL_STATUS_SHREVERSE.equals(operateType)){
				//退货完成增加扩展属性
				Map<String, Object> extendSyncParam  =  param.getExtendSyncParam()==null?new HashMap<String, Object>():param.getExtendSyncParam();
				//默认设置成全收
				extendSyncParam.put(
						WaybillStatus.WAYBILL_RETURN_FLAG_NAME,
						waybillStatus.getReturnFlag()==null?WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_ALL:waybillStatus.getReturnFlag());

				param.setExtendSyncParam(extendSyncParam);
			}

			WaybillSyncParameterExtend extend = new WaybillSyncParameterExtend();
			extend.setTaskId(task.getId());
			extend.setOperateType(operateType);
			extend.setBoxId(waybillStatus.getBoxCode());
			extend.setBatchId(waybillStatus.getSendCode());
			extend.setArriveZdId(waybillStatus.getReceiveSiteCode());
			extend.setArriveZdName(waybillStatus.getReceiveSiteName());
			extend.setArriveZdType(waybillStatus.getReceiveSiteType());
			extend.setThirdWaybillCode(task.getBoxCode());//第三方订单号
			extend.setReasonId(waybillStatus.getReasonId());
			param.setWaybillSyncParameterExtend(extend);
			params.add(param);
			// 发送操作轨迹
			sendOperateTrack(waybillStatus);
		}
        if(log.isInfoEnabled()){
            log.info("回传运单消息体：{}",JsonHelper.toJson(params));
        }
		return params;
	}

	/**
	 * 发送操作轨迹
	 * 按理说验货、分拣、发货三个节点都应该使用这个方法，并且此消息中要存放jy_operate_flow表的主键flowId，
	 * 当按照运单分拣的时候，发送全程跟踪只按运单调用了一次，jy_operate_flow表却存放的是包裹维度，导致使用本方法时不知道放哪个flowId，所以目前写在了doSorting方法里
	 * 验货和发货都拆成了包裹维度，不存在此问题
	 * 之后新增节点时，可以参照此场景决定代码放在何处
	 */
	private void sendOperateTrack(WaybillStatus waybillStatus) {
		// 操作码
		Integer operateType = waybillStatus.getOperateType();
		// 如果是验货、发货、配送异常、收货
		if (WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_INSPECTION.equals(operateType)
				|| WaybillStatus.WAYBILL_STATUS_CODE_FORWORD_DELIVERY.equals(operateType)
				|| WaybillStatus.WAYBILL_TRACK_QC.equals(operateType)
				|| WaybillStatus.WAYBILL_TRACK_SH.equals(operateType)) {
			jyOperateFlowService.sendOperateTrack(waybillStatus);
		}
	}

	private String resultToString(Long taskId, Result result, String message) {
		return message + "，任务ID为：" + taskId + "，处理结果为：" + result.isFlag()
		        + Constants.SEPARATOR_COMMA + result.getCode() + Constants.SEPARATOR_COMMA
		        + result.getMessage();
	}

	@Override
	public void sendModifyWaybillTrackNotify(List<Task> tasks) throws Exception{
		if (tasks.isEmpty()) {
			return;
		}

		this.log.info("向运单系统回传全程跟踪，共计条目数：{}" , tasks.size());
		for (Task task : tasks) {
			WaybillStatus tWaybillStatus = JsonHelper.fromJson(task.getBody(),
					WaybillStatus.class);
			this.log.info("向运单系统回传全程跟踪，task.getKeyword2()：{}" , task.getKeyword2());
			BdTraceDto bdTraceDto = new BdTraceDto();
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_BH))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				//操作单位更改为收货单位
				bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
				bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
				if(StringUtils.isBlank(tWaybillStatus.getRemark())){
					bdTraceDto.setOperatorDesp(tWaybillStatus.getReceiveSiteName()
							+ "已驳回");
				}else{
					bdTraceDto.setOperatorDesp(tWaybillStatus.getReceiveSiteName()
							+ "已驳回，原因："+tWaybillStatus.getRemark());
				}
				this.log.info("向运单系统回传全程跟踪，已驳回调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，已驳回调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_BHS))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getCreateSiteName()
						+ "已收货");
				this.log.info("向运单系统回传全程跟踪，已收货1调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
//				this.taskService.doDone(task);
				task.setYn(0);
			}
            //驻场验货 发全程跟踪  新增节点 1150
            if(tWaybillStatus.getOperateType().equals(1150)){
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getCreateSiteName()+ "已验货");
                if(null==tWaybillStatus.getCreateSiteCode()){
                    tWaybillStatus.setCreateSiteCode(task.getCreateSiteCode());
                    tWaybillStatus.setCreateSiteName(siteService.getSite(task.getCreateSiteCode()).getSiteName());
                }
                this.log.info("向运单系统回传全程跟踪，驻场 验货：" );
                waybillQueryManager.sendBdTrace(bdTraceDto);
//                this.taskService.doDone(task);
                task.setYn(0);
            }
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_SHREVERSE))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				//操作单位更改为收货单位
				bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
				bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
				
				bdTraceDto.setOperatorDesp(tWaybillStatus.getReceiveSiteName()
						+ "已收货");
				this.log.info("向运单系统回传全程跟踪，已收货调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，已收货调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}

			//包裹补打 客户改址 发全程跟踪 新增节点2400
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_WAYBILL_BD))
                    || task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE))) {
				this.log.info("向运单系统回传全程跟踪，调用sendOrderTrace：" );

                BdTraceDto packagePrintBdTraceDto = getPackagePrintBdTraceDto(tWaybillStatus);
				//单独发送全程跟踪消息，供其给前台消费
                waybillQueryManager.sendBdTrace(packagePrintBdTraceDto);
//				this.taskService.doDone(task);
				task.setYn(0);
			}

			//包裹补打 发全程跟踪 新增节点1000
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_MSGTYPE_PACK_REPRINT))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getOperator()
						+ "包裹补打");
				this.log.info("向运单系统回传全程跟踪，包裹补打调用：{}", JsonHelper.toJson(bdTraceDto));
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，包裹补打调用sendOrderTrace：" );
				task.setYn(0);
			}

			//签单返回合单 旧运单号发全程跟踪
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_OLD))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp("返单合单 ，新运单号"+tWaybillStatus.getSendCode());

				this.log.info("向运单系统回传全程跟踪，签单返回合单调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，签单返回合单调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}

			//签单返回合单 新运单号发全程跟踪
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_NEW))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				List<String> list = JSONArray.parseArray(tWaybillStatus.getRemark(), String.class);
				int maxSize = Integer.valueOf(PropertiesHelper.newInstance().getValue(MERGE_WAYBILL_RETURN_COUNT));
				String temp = "";
				if(list.size() > maxSize ){
					for(int i=0; i<maxSize; i++){
						if(i == maxSize-1){
							temp += list.get(i)+"等";
							break;
						}else {
							temp += list.get(i)+",";
						}
					}
					this.log.warn("签单返还新单号{}对应的旧单号有：{}",bdTraceDto.getWaybillCode(), JSON.toJSONString(list));

				}else{
					for(int i=0;i<list.size();i++){
						if(i == list.size()-1){
							temp += list.get(i);
						}else {
							temp += list.get(i)+",";
						}
					}
				}
				bdTraceDto.setOperatorDesp("返单合单，原运单号"+temp);
				this.log.info("向运单系统回传全程跟踪，签单返回合单调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，签单返回合单调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}

			//备件库售后 交接拆包
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_AMS_BH))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				//操作单位更改为收货单位
				bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
				bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
				
				bdTraceDto.setOperatorDesp(tWaybillStatus.getReceiveSiteName()
						+ "已驳回【备件库售后交接拆包】");
				this.log.info("向运单系统回传全程跟踪，已驳回调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，已驳回调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_AMS_SHREVERSE))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				//操作单位更改为收货单位
				bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
				bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
				
				bdTraceDto.setOperatorDesp(tWaybillStatus.getReceiveSiteName()
						+ "已收货【备件库售后交接拆包】");
				this.log.info("向运单系统回传全程跟踪，已收货调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.log.info("向运单系统回传全程跟踪，已收货调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}
            /**
             * 作者：wangtingwei@jd.com;
             * 回传逆向换单打印全程跟踪至运单中心
             */
            if(null!=task.getKeyword2()&&String.valueOf(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT).equals(task.getKeyword2())){
                if(WaybillUtil.isPackageCode(tWaybillStatus.getWaybillCode())){
                    tWaybillStatus.setPackageCode(tWaybillStatus.getWaybillCode());
                    tWaybillStatus.setWaybillCode(WaybillUtil.getWaybillCode(tWaybillStatus.getWaybillCode()));

                }
                tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT);

                if(null==tWaybillStatus.getCreateSiteCode()){
                    tWaybillStatus.setCreateSiteCode(task.getCreateSiteCode());
                    tWaybillStatus.setCreateSiteName(siteService.getSite(task.getCreateSiteCode()).getSiteName());
                }
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                Map<String,Object> map = bdTraceDto.getExtendParameter() == null ? Maps.<String, Object>newHashMap() : bdTraceDto.getExtendParameter();
                /**
                 * create by: yangwenshu
                 * description:把新运单号加入ExtendParameter  Map里，用于换单打印场景
                 * create time:
                 *
                  * @Param: tasks
                 * @return
                 */
                boolean hasReturnWaybillCode = false;
				if(StringUtils.isNotBlank(tWaybillStatus.getReturnWaybillCode())){
					map.put("returnWaybillCode",tWaybillStatus.getReturnWaybillCode());
					hasReturnWaybillCode = true;
				}
				/**
				 * 查询是否有全量接单失败，接到过14全量接单失败拦截消息，分拣在现有的换单打印节点写全程跟踪时，加个拦截类型的字段extendParameter 扩展属性里 interceptType
				 */
				boolean isFullOrderFail = waybillCancelService.isFullOrderFail(bdTraceDto.getWaybillCode());
				/**
				 * 判断一次原单号是否全量接单拦截
				 */
				if(!isFullOrderFail && hasReturnWaybillCode) {
					isFullOrderFail = waybillCancelService.isFullOrderFail(tWaybillStatus.getReturnWaybillCode());
				}
				if(isFullOrderFail) {
					map.put("interceptType",WaybillCancelInterceptTypeEnum.FULL_ORDER_FAIL.getCode());
				}
				if(!map.isEmpty()) {
					bdTraceDto.setExtendParameter(map);
				}
                this.log.info("向运单系统回传全程跟踪，逆向换单打印调用：{}", JsonHelper.toJson(bdTraceDto));
                waybillQueryManager.sendBdTrace(bdTraceDto);
//                this.taskService.doDone(task);
                task.setYn(0);
            }

			if(task.getKeyword2()!=null&&String.valueOf(WaybillStatus.WAYBILL_TRACK_SITE_LABEL_PRINT).equals(task.getKeyword2())){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_SITE_LABEL_PRINT);

				if(null==tWaybillStatus.getCreateSiteCode()){
					tWaybillStatus.setCreateSiteCode(task.getCreateSiteCode());
					tWaybillStatus.setCreateSiteName(siteService.getSite(task.getCreateSiteCode()).getSiteName());
				}
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				this.log.info("向运单系统回传全程跟踪，站点标签打印调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
//				this.taskService.doDone(task);
				task.setYn(0);
			}
			
            /**
             * 全程跟踪:回传封车信息至运单中心
             */
            if(null!=task.getKeyword2()&&String.valueOf(WaybillStatus.WAYBILL_TRACK_SEAL_VEHICLE).equals(task.getKeyword2())){
                toWaybillStatus2(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                this.log.info("向运单系统回传全程跟踪，封车：" );
                String sendCodes = tWaybillStatus.getSendCode();
                if(sendCodes != null){
                	String[] sendCodeArray = tWaybillStatus.getSendCode().split(",");
                    for(int i = 0; i < sendCodeArray.length; i++){
                        List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(sendCodeArray[i]);
                        if(null != sendDetailList && sendDetailList.size() > 0){
                        	for(SendDetail sendDetail : sendDetailList){
                        		bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
                        		bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
                                waybillQueryManager.sendBdTrace(bdTraceDto);
                        	}
                        }
                    }
                } else {
                    this.log.error("向运单系统回传全程跟踪，封车：批次号为空" );
                }
//                this.taskService.doDone(task);
                task.setYn(0);
            }
            
            /**
             * 全程跟踪:回传 解 封车信息至运单中心
             */
            if(null!=task.getKeyword2()&&String.valueOf(WaybillStatus.WAYBILL_TRACK_UNSEAL_VEHICLE).equals(task.getKeyword2())){
                toWaybillStatus3(tWaybillStatus, bdTraceDto);              
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                this.log.info("向运单系统回传全程跟踪，解封车：" );
                String sendCodes = tWaybillStatus.getSendCode();
                if(sendCodes != null){
                	String[] sendCodeArray = tWaybillStatus.getSendCode().split(",");
                    for(int i = 0; i < sendCodeArray.length; i++){
                        List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(sendCodeArray[i]);
                        if(null != sendDetailList && sendDetailList.size() > 0){
                        	for(SendDetail sendDetail : sendDetailList){
                        		bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
                        		bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
                                waybillQueryManager.sendBdTrace(bdTraceDto);
                        	}
                        }
                    }
                } else {
                    this.log.error("向运单系统回传全程跟踪，解封车：批次号为空" );
                }
//                this.taskService.doDone(task);
                task.setYn(0);
            }
            
            /**
             * 全程跟踪:回传 撤销封车信息至运单中心
             */
            if(null!=task.getKeyword2()&&String.valueOf(WaybillStatus.WAYBILL_TRACK_CANCEL_VEHICLE).equals(task.getKeyword2())){
                toWaybillStatus2(tWaybillStatus, bdTraceDto);              
                bdTraceDto.setOperatorDesp("货物已取消封车");
                this.log.info("向运单系统回传全程跟踪，已撤销封车：" );
                String sendCodes = tWaybillStatus.getSendCode();
                if(sendCodes != null){
                	List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(tWaybillStatus.getSendCode());
                    if(null != sendDetailList && sendDetailList.size() > 0){
	                    for(SendDetail sendDetail : sendDetailList){
	                        bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
	                        bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
	                        waybillQueryManager.sendBdTrace(bdTraceDto);
	                    }
                    }
                } else {
                    this.log.error("向运单系统回传全程跟踪，解封车：批次号为空" );
                }
//                this.taskService.doDone(task);
                task.setYn(0);
            }
            
            if(null!=task.getKeyword2()&&String.valueOf(3800).equals(task.getKeyword2())){
                tWaybillStatus.setOperateType(3800);

                if(null==tWaybillStatus.getCreateSiteCode()){
                    tWaybillStatus.setCreateSiteCode(task.getCreateSiteCode());
                    tWaybillStatus.setCreateSiteName(siteService.getSite(task.getCreateSiteCode()).getSiteName());
                }
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                this.log.info("向运单系统回传全程跟踪，取消发货：" );
                waybillQueryManager.sendBdTrace(bdTraceDto);
				// 发送操作轨迹
				jyOperateFlowService.sendOperateTrack(tWaybillStatus);
//                this.taskService.doDone(task);
                task.setYn(0);
            }
            
            
            /**
             * 全球购取消预装载的订单回传全称跟踪
             * */
            if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_CANCLE_LOADBILL))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp("由保税仓出库，预装载申请审核未通过或被海关抽查扣留");
				waybillQueryManager.sendBdTrace(bdTraceDto);
//				this.taskService.doDone(task);
				task.setYn(0);
			}
            /**
             * 全程跟踪-空铁提货
             */
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_AR_RECEIVE).equals(task.getKeyword2())) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);
				// 发送操作轨迹
				jyOperateFlowService.sendOperateTrack(tWaybillStatus);
				task.setYn(0);
			}

            /**
             * 全程跟踪:回传 空铁发货登记全程跟踪
             */
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_AR_SEND_REGISTER).equals(task.getKeyword2())) {
                toWaybillStatus2(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                this.log.info("向运单系统回传全程跟踪，空铁发货登记");
                String sendCode = tWaybillStatus.getSendCode();
                if (sendCode != null) {
                    List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(sendCode);
                    if (null != sendDetailList && sendDetailList.size() > 0) {
                        for (SendDetail sendDetail : sendDetailList) {
                            bdTraceDto.setWaybillCode(sendDetail.getWaybillCode());
                            bdTraceDto.setPackageBarCode(sendDetail.getPackageBarcode());
                            waybillQueryManager.sendBdTrace(bdTraceDto);
                        }
                    }
                } else {
                    this.log.warn("向运单系统回传全程跟踪，空铁发货登记：批次号为空");
                }
                task.setYn(0);
            }
			/**
			 * 全程跟踪-配送员上门收货
			 */
			if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_UP_DELIVERY).equals(task.getKeyword2())) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}
			/**
			 * 全程跟踪-配送员完成揽收
			 */
			if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_COMPLETE_DELIVERY).equals(task.getKeyword2())) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}
			/**
			 * 提交POP打印数据 回传全程跟踪
			 */
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_POP_PRINT))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);

				task.setYn(0); //设置他的原因是 不去调用 waybillSyncApi.batchUpdateStateByCode 这个方法
			}

			/**
			 * 全程跟踪:组板
			 */
			if (null != task.getKeyword2() &&
					(String.valueOf(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION).equals(task.getKeyword2())
					|| String.valueOf(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL).equals(task.getKeyword2()))) {
				// 发送板内明细全程跟踪
				sendBdTraceOfBoard(tWaybillStatus, bdTraceDto);
				task.setYn(0);
			}

			/**
			 * 全程跟踪:取消建箱
			 */
			if (task.getKeyword2() != null && String.valueOf(WaybillStatus.WAYBILL_TRACK_SORTING_CANCEL).equals(task.getKeyword2())) {
				String packageCode = tWaybillStatus.getPackageCode();
				String waybillCode = tWaybillStatus.getWaybillCode();
				//包裹号不为空时发送取消建箱的全程跟踪
				if (StringHelper.isNotEmpty(packageCode)) {
					tWaybillStatus.setPackageCode(packageCode);
					tWaybillStatus.setWaybillCode(waybillCode);
					toWaybillStatus(tWaybillStatus, bdTraceDto);
					bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
					waybillQueryManager.sendBdTrace(bdTraceDto);
					// 发送操作轨迹
					jyOperateFlowService.sendOperateTrack(tWaybillStatus);
				} else {
					log.warn("取消分拣全程跟踪失败，包裹号没空！");
				}
				task.setYn(0);
			}
			/**
			 * 全程跟踪:经济网取消建箱
			 */
			if (task.getKeyword2() != null && String.valueOf(WaybillStatus.WAYBILL_STATUS_CODE_SITE_CANCEL_SORTING).equals(task.getKeyword2())) {
				String packageCode = tWaybillStatus.getPackageCode();
				String waybillCode = tWaybillStatus.getWaybillCode();
				tWaybillStatus.setPackageCode(packageCode);
				tWaybillStatus.setWaybillCode(waybillCode);
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}
			/**
			 * 全程跟踪:揽收交接
			 */
			if (task.getKeyword2() != null && String.valueOf(WaybillStatus.WAYBILL_TRACK_RECEIVE_HANDOVERS).equals(task.getKeyword2())) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp("已操作揽收交接复核");
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}

			/**
			 * 全程跟踪:配送交接
			 */
			if (task.getKeyword2() != null && String.valueOf(WaybillStatus.WAYBILL_TRACK_SEND_HANDOVERS).equals(task.getKeyword2())) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp("已操作配送交接复核");
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}

			/**
			 * 全程跟踪:转网
			 */
			if (null != task.getKeyword2() &&
					(String.valueOf(WaybillStatus.WAYBILL_TRACK_WAYBILL_TRANSFER).equals(task.getKeyword2()))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}

			/**
			 * 重复抽检
			 */
			if (null != task.getKeyword2() &&
					(String.valueOf(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK).equals(task.getKeyword2()))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
				waybillQueryManager.sendBdTrace(bdTraceDto);
				task.setYn(0);
			}
            /**
             * 快运暂存
             */
            if (null != task.getKeyword2() &&
                    (String.valueOf(WaybillStatus.WAYBILL_STATUS_STORAGE_KYZC).equals(task.getKeyword2()))) {
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                waybillQueryManager.sendBdTrace(bdTraceDto);
                task.setYn(0);
            }

            /**
             * 企配仓订单发上架全流程跟踪
             */
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_OPE_TYPE_PUTAWAY).equals(task.getKeyword2())) {
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                waybillQueryManager.sendBdTrace(bdTraceDto);
                task.setYn(0);
            }

            // 首次打印包裹全程跟踪
            if (StringUtils.isNotBlank(task.getKeyword2())
                    && task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_PACKAGE_PRINT))) {

                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getOperator() + "包裹打印");

                if (log.isInfoEnabled()) {
                    log.info("向运单系统回传单号:{}的打印全程跟踪. {}", tWaybillStatus.getPackageCode(), JsonHelper.toJson(bdTraceDto));
                }

                waybillQueryManager.sendBdTrace(bdTraceDto);
                task.setYn(0);
            }
            /**
             * 弃件暂存
             */
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL).equals(task.getKeyword2())) {
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                waybillQueryManager.sendBdTrace(bdTraceDto);
                task.setYn(0);
            }

            /**
             * 滞留上报
             */
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_STRAND_REPORT).equals(task.getKeyword2())) {
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                waybillQueryManager.sendBdTrace(bdTraceDto);
                task.setYn(0);
            }

            // 特殊安检
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK).equals(task.getKeyword2())) {
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                if(Objects.equals(BusinessUtil.getBarCodeType(tWaybillStatus.getPackageCode()), BarCodeType.WAYBILL_CODE)){
                    bdTraceDto.setWaybillTraceType(WaybillStatus.waybillTraceType2);
                    bdTraceDto.setWaybillCode(tWaybillStatus.getPackageCode());
                    bdTraceDto.setPackageBarCode(null);
                }
                waybillQueryManager.sendBdTrace(bdTraceDto);
                task.setYn(0);
            }

		}


		Map<Long, Result> results = this.waybillSyncApi.batchUpdateStateByCode(this
		        .parseWaybillSyncParameter(tasks));
		if (results == null || results.isEmpty()) {
			return;
		}

		for (Map.Entry<Long, Result> mResult : results.entrySet()) {
			if (mResult != null) {
				Long taskId = mResult.getKey();
				Result result = mResult.getValue();

				this.log.info(this.resultToString(taskId, result, "回传运单全程跟踪"));

				if (true == result.isFlag()) {
//					this.taskService.doDone(this.findTask(tasks, taskId,
//					        Task.TASK_TYPE_WAYBILL_TRACK));
				} else if (WaybillStatus.RESULT_CODE_PARAM_IS_NULL == result.getCode()
				        || WaybillStatus.RESULT_CODE_REPEAT_TASK == result.getCode()) {
//					this.taskService.doError(this.findTask(tasks, taskId,
//					        Task.TASK_TYPE_WAYBILL_TRACK));
                    this.log.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
				} else {
//					this.taskService.doRevert(this.findTask(tasks, taskId,
//					        Task.TASK_TYPE_WAYBILL_TRACK));
                    this.log.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
				}
			}
		}
	}

	/**
	 * 发送板内明细全程跟踪
	 *
	 * @param tWaybillStatus
	 * @param bdTraceDto
	 */
	private void sendBdTraceOfBoard(WaybillStatus tWaybillStatus, BdTraceDto bdTraceDto) {
		String boxOrPackageCode = tWaybillStatus.getPackageCode();
		// left：包裹号，right：运单号
		List<ImmutablePair<String, String>> list = new ArrayList<>();
		if (BusinessUtil.isBoxcode(boxOrPackageCode)){
			tWaybillStatus.setBoxCode(boxOrPackageCode);
			// 箱号处理
			String boxCode = tWaybillStatus.getPackageCode();
			// 1.查询分拣sorting
			Box box = boxService.findBoxByCode(boxCode);
			List<Sorting> sortByDms = Lists.newArrayList();
			if(box != null){
				Sorting sorting = new Sorting();
				sorting.setBoxCode(box.getCode());
				sorting.setCreateSiteCode(box.getCreateSiteCode());
				sortByDms = sortingService.findByBoxCode(sorting);
			}
			if(CollectionUtils.isNotEmpty(sortByDms)){
				for (Sorting sortDto : sortByDms) {
					list.add(ImmutablePair.of(sortDto.getPackageCode(), sortDto.getWaybillCode()));
				}
			}else {
				// 2.无分拣记录则从终端获取发货明细
				List<SendInfoDto> sendDetailsFromZD = terminalManager.getSendDetailsFromZD(boxCode);
				if(CollectionUtils.isNotEmpty(sendDetailsFromZD)){
					for (SendInfoDto zdSendDto : sendDetailsFromZD) {
						list.add(ImmutablePair.of(zdSendDto.getPackageBarcode(), zdSendDto.getWaybillCode()));
					}
				}
			}
		}else {
			// 包裹号处理
			list.add(ImmutablePair.of(boxOrPackageCode, WaybillUtil.getWaybillCode(boxOrPackageCode)));
		}
		// 循环发送全程跟踪
		for (ImmutablePair<String, String> dto : list) {
			tWaybillStatus.setPackageCode(dto.left);
			tWaybillStatus.setWaybillCode(dto.right);
			toWaybillStatus(tWaybillStatus, bdTraceDto);
			bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
			waybillQueryManager.sendBdTrace(bdTraceDto);
			// 发送操作轨迹
			jyOperateFlowService.sendOperateTrack(tWaybillStatus);
		}
	}

	private BdTraceDto getPackagePrintBdTraceDto(WaybillStatus tWaybillStatus) {
        BdTraceDto bdTraceDto2 = new BdTraceDto();
        bdTraceDto2.setWaybillCode(tWaybillStatus.getWaybillCode());
        bdTraceDto2.setOperateType(WaybillStatus.WAYBILL_TRACK_WAYBILL_BD);
        bdTraceDto2.setOperatorDesp(WaybillStatus.WAYBILL_TRACK_WAYBILL_BD_MSG);
		bdTraceDto2.setOperatorSiteId(null!=tWaybillStatus.getCreateSiteCode()?tWaybillStatus.getCreateSiteCode():0);
		bdTraceDto2.setOperatorSiteName(tWaybillStatus.getCreateSiteName());
        bdTraceDto2.setOperatorUserName(tWaybillStatus.getOperator());
        bdTraceDto2.setOperatorUserId(null!=tWaybillStatus.getOperatorId()?tWaybillStatus.getOperatorId():0);
        bdTraceDto2.setOperatorTime(new Date());
        return bdTraceDto2;
    }

	public boolean batchUpdateWaybillPartByOperateType(PackageHalf packageHalf , List<PackageHalfDetail> packageHalfDetails, Integer waybillOpeType, Integer operatorId, String operatorName, Date operateTime,Integer orgId){
		CallerInfo info = null;
		try{
			info = Profiler.registerInfo( "DMSWEB.waybillStatusService.batchUpdateWaybillPartByOperateType",false, true);

			//妥投或者拒收 走老同步接口
			if(waybillOpeType.equals(WaybillStatus.WAYBILL_OPE_TYPE_HALF_SIGNIN)) {

				//组装更新对象
				List<WaybillSyncPartParameter> waybillSyncPartParameterList = new ArrayList<WaybillSyncPartParameter>();
				WaybillSyncPartParameter waybillSyncPartParameter = new WaybillSyncPartParameter();
				waybillSyncPartParameterList.add(waybillSyncPartParameter);
				List<PackageSyncPartParameter> packageSyncPartParameterList = new ArrayList<PackageSyncPartParameter>();
				waybillSyncPartParameter.setPackageSyncPartParameterList(packageSyncPartParameterList);
				waybillSyncPartParameter.setWaybillOperateType(waybillOpeType);
				//组装包裹 并且 获取运单的最终状态， 如果有拒收则按部分签收更新运单状态

				for (PackageHalfDetail packageHalfDetail : packageHalfDetails) {

					if (packageSyncPartParameterList.size() == 0) {
						waybillSyncPartParameter.setWaybillCode(packageHalfDetail.getWaybillCode());
						waybillSyncPartParameter.setOperateSiteName(packageHalfDetail.getOperateSiteName());
						waybillSyncPartParameter.setOperateSiteId(packageHalfDetail.getOperateSiteCode().intValue());
						waybillSyncPartParameter.setOperatorId(operatorId);
						waybillSyncPartParameter.setOperatorName(operatorName);
						waybillSyncPartParameter.setOperateTime(operateTime);
					}

					PackageSyncPartParameter packageSyncPartParameter = new PackageSyncPartParameter();
					packageSyncPartParameter.setPackageCode(packageHalfDetail.getPackageCode());
					packageSyncPartParameter.setPackageOperateType(getPackageOperateTypeByResultType(packageHalfDetail.getResultType()));
					if(packageHalfDetail.getReasonType()==null || packageHalfDetail.getReasonType().toString().equals("-1")){
						packageSyncPartParameter.setRemark("");
					}else{
						packageSyncPartParameter.setRemark(PackageHalfReasonTypeEnum.getNameByKey(packageHalfDetail.getReasonType().toString()));
					}
					packageSyncPartParameterList.add(packageSyncPartParameter);
				}


				BaseEntity<Map<String, String>> result = this.waybillSyncApi.batchUpdateWaybillPartByOperateType(waybillSyncPartParameterList);
				if (result.getResultCode() == 1) {
					//成功
					return true;
				} else {
					//失败的包裹号
					Set<Map.Entry<String, String>> mapSet = result.getData().entrySet();
					for (Map.Entry<String, String> entry : mapSet) {
						entry.getKey();
						entry.getValue();
					}
					return false;
				}
			}else{
				List<WaybillParameter> waybillParameters = new ArrayList<WaybillParameter>();
				WaybillParameter waybillParameter = new WaybillParameter();
				waybillParameter.setWaybillCode(packageHalf.getWaybillCode());
				waybillParameter.setOrgId(orgId);
				waybillParameter.setPsyId(operatorId);
				waybillParameter.setZdName(packageHalf.getOperateSiteName());
				waybillParameter.setZdId(packageHalf.getOperateSiteCode().intValue());
				waybillParameter.setOperatorId(operatorId);
				waybillParameter.setOperatorName(operatorName);
				waybillParameter.setOperatorType(waybillOpeType);
				waybillParameter.setOperateTime(operateTime);
				waybillParameters.add(waybillParameter);
				OrderShipsDto orderShipDto = new OrderShipsDto();
				waybillParameter.setOrderShipsDto(orderShipDto);
				//妥投 分拣中心 现阶段 只能操作整单妥投  不可以操作整单拒收。
				if(waybillOpeType.equals(WaybillStatus.WAYBILL_OPE_TYPE_DELIVERED)){
					orderShipDto.setAmount(0);
					orderShipDto.setDistanceType(0);
					orderShipDto.setLocalTimeState(operateTime);
					orderShipDto.setPayWayId(-2); //在线支付
					orderShipDto.setType(0);
				}
				if(log.isDebugEnabled()){
					log.debug("运单同步老接口入参：{}",JsonHelper.toJson(waybillParameters));
				}
				//老同步接口
				BaseEntity<Boolean> result = this.waybillSyncApi.batchUpdateWaybillByWaybillCode(waybillParameters, waybillOpeType);
				if (result.getResultCode() == 1) {
					//成功
					return true;
				} else {
					return false;
				}
			}
		}catch (Exception e){
			log.error("包裹半收运单接口调用失败，{} 操作码 {}",packageHalf.getWaybillCode(),waybillOpeType,e);
			Profiler.functionError(info);
			return false;
		}finally{
			Profiler.registerInfoEnd(info);
		}

	}

	@Override
	public void sendWaybillTrackByOperatorCode(List<WaybillSyncParameter> waybillSyncParameters, Integer operatorType) {
		CallerInfo info = null;

		try{
			if(log.isInfoEnabled()){
				log.info("根据操作号批量同步运单数据 入参-{}，operatorType-{}",JSON.toJSONString(waybillSyncParameters),operatorType);
			}
			info = Profiler.registerInfo( "DMSWEB.waybillStatusService.sendWaybillTrackByOperatorCode",false, true);
			BaseEntity<List<String>> response = waybillSyncApi.batchUpdateWaybillByOperatorCode(waybillSyncParameters, operatorType);
			if(log.isInfoEnabled()){
				log.info("根据操作号批量同步运单数据 出参-{}",JSON.toJSONString(response));

			}
		}catch (Exception e){
			log.error("根据操作号批量同步运单数据（异步更新）异常!",e);
			Profiler.functionError(info);

		}finally {
			Profiler.registerInfoEnd(info);
		}
	}

	/**
	 * 根据配送类型 获取相应运单操作码
	 * @param resultType
	 * @return
	 */
	private Integer getPackageOperateTypeByResultType(Integer resultType){
		Integer result = new Integer(0);
		if(PackageHalfResultTypeEnum.DELIVERED_1.getCode().equals(resultType.toString())){
			result = WaybillStatus.WAYBILL_OPE_TYPE_DELIVERED;
		}else if(PackageHalfResultTypeEnum.REJECT_2.getCode().equals(resultType.toString())){
			result = WaybillStatus.WAYBILL_OPE_TYPE_REJECT;
		}else{
			throw new RuntimeException("未识别配送结果类型 "+ resultType);
		}
		return result;
	}
}

