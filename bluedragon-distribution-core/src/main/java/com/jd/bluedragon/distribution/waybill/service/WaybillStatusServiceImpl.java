package com.jd.bluedragon.distribution.waybill.service;

import java.text.MessageFormat;
import java.util.*;

import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.domain.PackageHalfReasonTypeEnum;
import com.jd.bluedragon.distribution.half.domain.PackageHalfResultTypeEnum;
import com.jd.etms.waybill.api.WaybillSyncApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.WaybillParameter;
import com.jd.etms.waybill.dto.OrderShipsDto;
import com.jd.etms.waybill.handler.PackageSyncPartParameter;
import com.jd.etms.waybill.handler.WaybillSyncPartParameter;
import com.jd.fastjson.JSON;
import com.jd.fastjson.JSONArray;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.common.Result;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameterExtend;

@Service("waybillStatusService")
public class WaybillStatusServiceImpl implements WaybillStatusService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final Integer SITE_TYPE_SPWMS = 903;
	private static final String MERGE_WAYBILL_RETURN_COUNT = "mergeWaybillReturnCount";

	@Autowired
	private TaskService taskService;
	
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
	private ReversePrintService reversePrintService;
	public void sendModifyWaybillStatusNotify(List<Task> tasks) throws Exception{
		if (tasks.isEmpty()) {
			return;
		}

		Map<Long, Result> results = this.waybillSyncApi.batchUpdateWaybillByOperateCode(this
				.parseWaybillSyncParameter(tasks));

		if (results == null || results.isEmpty()) {
            if(logger.isInfoEnabled()){
                logger.info(MessageFormat.format("回传运单状态数据转换为空:{0}",JsonHelper.toJson(tasks)));
            }
			return;
		}

		for (Map.Entry<Long, Result> mResult : results.entrySet()) {

			if (mResult != null) {
				Long taskId = mResult.getKey();
				Result result = mResult.getValue();
                if(logger.isInfoEnabled()){
                    logger.info(MessageFormat.format("回传运单状态taskId:{0}->resultCode:{1}->resultMessage{2}",taskId,result.getCode(),result.getMessage()));
                }
				if (true == result.isFlag()) {
//					this.taskService.doDone(this.findTask(tasks, taskId, Task.TASK_TYPE_WAYBILL));
				} else if (WaybillStatus.RESULT_CODE_PARAM_IS_NULL == result.getCode()
						|| WaybillStatus.RESULT_CODE_REPEAT_TASK == result.getCode()) {
					this.logger.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
//					this.taskService.doError(this.findTask(tasks, taskId, Task.TASK_TYPE_WAYBILL));
				} else {
                    this.logger.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
//					this.taskService.doRevert(this.findTask(tasks, taskId, Task.TASK_TYPE_WAYBILL));
				}
			}
		}

	}

	public void sendModifyWaybillStatusFinished(Task task) throws Exception{
		logger.debug("开始妥投运单调用运单接口:");
		BaseEntity<Boolean> result = this.waybillSyncApi.batchUpdateWaybillByWaybillCode(this.parseWaybillParameter(task), 8);
		logger.debug("开始妥投运单成功:");
		if (result != null && result.getData() == true) {
//			this.taskService.doDone(task);
			logger.debug("开始妥投运单成功:" + JsonHelper.toJson(task));
		} else {
			logger.error("买卖宝置妥投状态失败\n" + JsonHelper.toJson(task) + "\n");
            throw new Exception("买卖宝置妥投状态失败\n" + JsonHelper.toJson(result) + "\n");
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

	}
	
	// 没有注入运单号和包裹号 (封车)
	private void toWaybillStatus2(WaybillStatus tWaybillStatus, BdTraceDto bdTraceDto) {
		bdTraceDto.setOperateType(tWaybillStatus.getOperateType());
		bdTraceDto.setOperatorSiteId(tWaybillStatus.getCreateSiteCode());
		bdTraceDto.setOperatorSiteName(tWaybillStatus.getCreateSiteName());
		bdTraceDto.setOperatorTime(tWaybillStatus.getOperateTime());
		bdTraceDto.setOperatorUserName(tWaybillStatus.getOperator());
        bdTraceDto.setOperatorUserId(null!=tWaybillStatus.getOperatorId()?tWaybillStatus.getOperatorId():0);
	}
	
	// 没有注入运单号和包裹号 (解封车)
	private void toWaybillStatus3(WaybillStatus tWaybillStatus, BdTraceDto bdTraceDto) {
		bdTraceDto.setOperateType(tWaybillStatus.getOperateType());
		bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
		bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
		bdTraceDto.setOperatorTime(tWaybillStatus.getOperateTime());
		bdTraceDto.setOperatorUserName(tWaybillStatus.getOperator());
        bdTraceDto.setOperatorUserId(null!=tWaybillStatus.getOperatorId()?tWaybillStatus.getOperatorId():0);
	}

	private Task findTask(List<Task> tasks, Long taskId, Integer taskType) {
		for (Task task : tasks) {
			if (taskId.equals(task.getId())) {
				task.setType(taskType);
				return task;
			}
		}

		this.logger.info("未找到对应Task, 非法任务ID:" + taskId);

		return null;
	}

	private List<WaybillParameter> parseWaybillParameter(Task task) {

		logger.debug("开始妥投自营订单:"+JsonHelper.toJson(task));
			if (StringHelper.isEmpty(task.getBody())) {
				return  null;
			}
			if (task.getYn()!=null && task.getYn().equals(0)) {
				return  null;
			}
			logger.debug("置妥投状态:"+JsonHelper.toJson(task));
		WaybillParameter[] arrays = JsonHelper.jsonToArray(task.getBody(),
				WaybillParameter[].class);

		List<WaybillParameter> params = Arrays.asList(arrays);
			logger.debug("传送运单参数:"+JsonHelper.toJson(params));

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
			WaybillSyncParameter param = new WaybillSyncParameter();

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

			WaybillSyncParameterExtend extend = new WaybillSyncParameterExtend();
			extend.setTaskId(task.getId());
			extend.setOperateType(operateType);
			extend.setBoxId(waybillStatus.getBoxCode());
			extend.setBatchId(waybillStatus.getSendCode());
			extend.setArriveZdId(waybillStatus.getReceiveSiteCode());
			extend.setArriveZdName(waybillStatus.getReceiveSiteName());
			extend.setArriveZdType(waybillStatus.getReceiveSiteType());
			extend.setThirdWaybillCode(task.getBoxCode());//第三方订单号

			param.setWaybillSyncParameterExtend(extend);
			params.add(param);
		}
        if(logger.isInfoEnabled()){
            logger.info(MessageFormat.format("回传运单消息体：{0}",JsonHelper.toJson(params)));
        }
		return params;
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

		this.logger.info("向运单系统回传全程跟踪，共计条目数：" + tasks.size()); 
		for (Task task : tasks) {
			WaybillStatus tWaybillStatus = JsonHelper.fromJson(task.getBody(),
					WaybillStatus.class);
			this.logger.info("向运单系统回传全程跟踪，task.getKeyword2()：" + task.getKeyword2());
			BdTraceDto bdTraceDto = new BdTraceDto();
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_BH))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				//操作单位更改为收货单位
				bdTraceDto.setOperatorSiteId(tWaybillStatus.getReceiveSiteCode());
				bdTraceDto.setOperatorSiteName(tWaybillStatus.getReceiveSiteName());
				
				bdTraceDto.setOperatorDesp(tWaybillStatus.getReceiveSiteName()
						+ "已驳回");
				this.logger.info("向运单系统回传全程跟踪，已驳回调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.logger.info("向运单系统回传全程跟踪，已驳回调用sendOrderTrace：" );
				//单独发送全程跟踪消息，供其给前台消费
				waybillQueryManager.sendOrderTrace(bdTraceDto.getWaybillCode(),
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_CCSHBH,
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_CCSHBH_MSG,
						bdTraceDto.getOperatorDesp(),
						bdTraceDto.getOperatorUserName(), null);
//				this.taskService.doDone(task);
				task.setYn(0);
			}
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_BHS))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp(tWaybillStatus.getCreateSiteName()
						+ "已收货");
				this.logger.info("向运单系统回传全程跟踪，已收货1调用：" );
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
                this.logger.info("向运单系统回传全程跟踪，驻场 验货：" );
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
				this.logger.info("向运单系统回传全程跟踪，已收货调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.logger.info("向运单系统回传全程跟踪，已收货调用sendOrderTrace：" );
				//单独发送全程跟踪消息，供其给前台消费
				waybillQueryManager.sendOrderTrace(bdTraceDto.getWaybillCode(),
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_CCSHQR,
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_CCSHQR_MSG,
						bdTraceDto.getOperatorDesp(),
						bdTraceDto.getOperatorUserName(), null);
//				this.taskService.doDone(task);
				task.setYn(0);
			}

			//包裹补打 发全程跟踪 新增节点2400
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE))) {
				this.logger.info("向运单系统回传全程跟踪，调用sendOrderTrace：" );
				//单独发送全程跟踪消息，供其给前台消费
				waybillQueryManager.sendOrderTrace(tWaybillStatus.getWaybillCode(),
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE,
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE_MSG,
						WaybillStatus.WAYBILL_TRACK_MSGTYPE_UPDATE_CONTENT,
						tWaybillStatus.getOperator(), null);
//				this.taskService.doDone(task);
				task.setYn(0);
			}

			//签单返回合单 旧运单号发全程跟踪
			if (task.getKeyword2().equals(String.valueOf(WaybillStatus.WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_OLD))) {
				toWaybillStatus(tWaybillStatus, bdTraceDto);
				bdTraceDto.setOperatorDesp("返单合单 ，新运单号"+tWaybillStatus.getSendCode());

				this.logger.info("向运单系统回传全程跟踪，签单返回合单调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.logger.info("向运单系统回传全程跟踪，签单返回合单调用sendOrderTrace：" );
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
					this.logger.warn("签单返还新单号"+bdTraceDto.getWaybillCode()+"对应的旧单号有"+ JSON.toJSONString(list));

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
				this.logger.info("向运单系统回传全程跟踪，签单返回合单调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.logger.info("向运单系统回传全程跟踪，签单返回合单调用sendOrderTrace：" );
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
				this.logger.info("向运单系统回传全程跟踪，已驳回调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.logger.info("向运单系统回传全程跟踪，已驳回调用sendOrderTrace：" );
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
				this.logger.info("向运单系统回传全程跟踪，已收货调用：" );
				waybillQueryManager.sendBdTrace(bdTraceDto);
				this.logger.info("向运单系统回传全程跟踪，已收货调用sendOrderTrace：" );
//				this.taskService.doDone(task);
				task.setYn(0);
			}
            /**
             * 作者：wangtingwei@jd.com;
             * 回传逆向换单打印全程跟踪至运单中心
             */
            if(null!=task.getKeyword2()&&String.valueOf(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT).equals(task.getKeyword2())){
                if(BusinessHelper.isPackageCode(tWaybillStatus.getWaybillCode())){
                    tWaybillStatus.setPackageCode(tWaybillStatus.getWaybillCode());
                    tWaybillStatus.setWaybillCode(BusinessHelper.getWaybillCodeByPackageBarcode(tWaybillStatus.getWaybillCode()));

                }
                tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_REVERSE_PRINT);

                if(null==tWaybillStatus.getCreateSiteCode()){
                    tWaybillStatus.setCreateSiteCode(task.getCreateSiteCode());
                    tWaybillStatus.setCreateSiteName(siteService.getSite(task.getCreateSiteCode()).getSiteName());
                }
                toWaybillStatus(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());

                /**
                 * create by: yangwenshu
                 * description:把新运单号加入ExtendParameter  Map里，用于换单打印场景
                 * create time:
                 *
                  * @Param: tasks
                 * @return
                 */
				InvokeResult<RepeatPrint> target=reversePrintService.getNewWaybillCode1(tWaybillStatus.getWaybillCode(),true);
				if(target.getCode()==InvokeResult.RESULT_SUCCESS_CODE&&null!=target.getData()){
					String newWaybillCode =target.getData().getNewWaybillCode();
					Map<String,Object> map=new HashMap<String, Object>();
					map.put("returnWaybillCode",newWaybillCode);
					bdTraceDto.setExtendParameter(map);
				}
				else{
					logger.warn("根据旧运单号"+tWaybillStatus.getWaybillCode()+"获取新运单号失败");
				}

                this.logger.info("向运单系统回传全程跟踪，逆向换单打印调用：" );
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
				this.logger.info("向运单系统回传全程跟踪，站点标签打印调用：" );
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
                this.logger.info("向运单系统回传全程跟踪，封车：" );
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
                    this.logger.error("向运单系统回传全程跟踪，封车：批次号为空" );
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
                this.logger.info("向运单系统回传全程跟踪，解封车：" );
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
                    this.logger.error("向运单系统回传全程跟踪，解封车：批次号为空" );
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
                this.logger.info("向运单系统回传全程跟踪，已撤销封车：" );
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
                    this.logger.error("向运单系统回传全程跟踪，解封车：批次号为空" );
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
                this.logger.info("向运单系统回传全程跟踪，取消发货：" );
                waybillQueryManager.sendBdTrace(bdTraceDto);
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
				task.setYn(0);
			}

            /**
             * 全程跟踪:回传 空铁发货登记全程跟踪
             */
            if (null != task.getKeyword2() && String.valueOf(WaybillStatus.WAYBILL_TRACK_AR_SEND_REGISTER).equals(task.getKeyword2())) {
                toWaybillStatus2(tWaybillStatus, bdTraceDto);
                bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
                this.logger.info("向运单系统回传全程跟踪，空铁发货登记");
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
                    this.logger.error("向运单系统回传全程跟踪，空铁发货登记：批次号为空");
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

				String boxOrPackageCode = tWaybillStatus.getPackageCode();
				if (SerialRuleUtil.isMatchBoxCode(boxOrPackageCode)) {
					//先取出box表的始发，然后查sorting表
					List<Sorting> sortingList = getPackagesByBoxCode(boxOrPackageCode);
					for (Sorting sorting : sortingList) {
						tWaybillStatus.setWaybillCode(sorting.getWaybillCode());
						tWaybillStatus.setPackageCode(sorting.getPackageCode());
						toWaybillStatus(tWaybillStatus, bdTraceDto);
						bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
						waybillQueryManager.sendBdTrace(bdTraceDto);
					}

				} else {
					tWaybillStatus.setPackageCode(boxOrPackageCode);
					tWaybillStatus.setWaybillCode(BusinessHelper.getWaybillCodeByPackageBarcode(boxOrPackageCode));
					toWaybillStatus(tWaybillStatus, bdTraceDto);
					bdTraceDto.setOperatorDesp(tWaybillStatus.getRemark());
					waybillQueryManager.sendBdTrace(bdTraceDto);

				}
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

				this.logger.info(this.resultToString(taskId, result, "回传运单全程跟踪"));

				if (true == result.isFlag()) {
//					this.taskService.doDone(this.findTask(tasks, taskId,
//					        Task.TASK_TYPE_WAYBILL_TRACK));
				} else if (WaybillStatus.RESULT_CODE_PARAM_IS_NULL == result.getCode()
				        || WaybillStatus.RESULT_CODE_REPEAT_TASK == result.getCode()) {
//					this.taskService.doError(this.findTask(tasks, taskId,
//					        Task.TASK_TYPE_WAYBILL_TRACK));
                    this.logger.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
				} else {
//					this.taskService.doRevert(this.findTask(tasks, taskId,
//					        Task.TASK_TYPE_WAYBILL_TRACK));
                    this.logger.error(this.resultToString(taskId, result, "分拣数据回传运单系统"));
                    throw new Exception("分拣数据回传运单系统失败\n" + JsonHelper.toJson(result) + "\n");
				}
			}
		}
	}

	/**
	 * 根据箱号获取箱内的包裹信息
	 * @param boxCode
	 * @return
	 */
	private List<Sorting> getPackagesByBoxCode(String boxCode) {
		Box box = boxService.findBoxByCode(boxCode);
		if (box != null) {
			Sorting sorting = new Sorting();
			sorting.setBoxCode(boxCode);
			sorting.setCreateSiteCode(box.getCreateSiteCode());
			return sortingService.findByBoxCode(sorting);
		}
		return null;
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
					packageSyncPartParameter.setRemark(PackageHalfReasonTypeEnum.getNameByKey(packageHalfDetail.getReasonType().toString()));

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
				logger.debug("运单同步老接口入参："+JsonHelper.toJson(waybillParameters));
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
			logger.error("包裹半收运单接口调用失败，"+packageHalf.getWaybillCode()+" 操作码 "+waybillOpeType+" 失败原因："+e.getMessage());
			Profiler.functionError(info);
			return false;
		}finally{
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

