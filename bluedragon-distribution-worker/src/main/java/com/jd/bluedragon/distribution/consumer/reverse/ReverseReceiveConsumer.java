package com.jd.bluedragon.distribution.consumer.reverse;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WorkTaskServiceManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.Eclp2BdReceiveDetail;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.*;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveService;
import com.jd.bluedragon.distribution.reverse.service.ReverseRejectService;
import com.jd.bluedragon.distribution.reverse.service.ReverseStockInDetailService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.SparsModel;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.erp.domain.OrderDeliverBody;
import com.jd.ql.erp.domain.OrderDeliverWorkTask;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("reverseReceiveConsumer")
public class ReverseReceiveConsumer extends MessageBaseConsumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/** 0拒收 1全收 2半收 -1表示此次没回传 */
    private static final Integer TYPE_RECEIVE_DEFAULT = -1;
    private static final Integer TYPE_RECEIVE_0 = 0;
    private static final Integer TYPE_RECEIVE_1 = 1;
    private static final Integer TYPE_RECEIVE_2 = 2;

	@Autowired
	private ReverseRejectService reverseRejectService;

	@Autowired
	private ReverseReceiveService reverseReceiveService;
	@Autowired
	private ReverseStockInDetailService reverseStockInDetailService;

	@Autowired
	private TaskService taskService;

	@Autowired
    private SendDatailDao sendDatailDao;
	
	@Autowired
    private ReverseSpareDao sparedao;

	@Autowired
	private SendMDao sendMDao;

	@Autowired
    private BaseMajorManager baseMajorManager;
	
    @Autowired
    private ReversePrintService reversePrintService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
    WaybillService waybillService;

	@Autowired
	WorkTaskServiceManager workTaskServiceManager;

	@Autowired
	SysConfigService sysConfigService;

	/**
	 * 移动仓内配单调终端接口参数
	 */
	private final Integer SOURCE_CODE_DMS_FINISHED = 14;  //分拣sourceCode
	private final Integer WORK_TASK_PARAM_PAY_WAY_ID = 2; //支付方式Id
	private final String WORK_TASK_PARAM_PAY_WAY_NAME = "在线支付";  //支付方式名称
	private final Integer WORK_TASK_PARAM_OPERATE_TYPE = 1; //操作类型1表示一体机
	private final Integer WORK_TASK_PARAM_AMOUNT = 0; //实收金额
	private final Integer WORK_TASK_PARAM_PRICE = 0;  //应收金额
	private final Integer WORK_TASK_PARAM_TASK_TYPE = 7;  //任务类型
	private final String WORK_TASK_PARAM_REMARK = "移动仓内配单分拣操作妥投";


	@Override
    @JProfiler(jKey = "reverseReceiveConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
	public void consume(Message message) {

		String messageContent = message.getText();
		this.log.info("逆向收货消息messageContent：{}" , messageContent);

		ReverseReceiveRequest jrequest = null;
		ReceiveRequest xrequest = null;
		ReverseReceive reverseReceive = new ReverseReceive();

		if (XmlHelper.isXml(messageContent, ReceiveRequest.class, null)) {
			xrequest = (ReceiveRequest) XmlHelper.toObject(messageContent, ReceiveRequest.class);
			this.log.debug("逆向收货消息ReverseReceiveRequest：{}" , xrequest.toString());
			reverseReceive.setSendCode(xrequest.getSendCode());
			if(StringUtils.isNotBlank(xrequest.getNewWayBill())){
				//新运单号不为空 按新运单处理
				reverseReceive.setPackageCode(xrequest.getNewWayBill());
				reverseReceive.setOrderId(xrequest.getNewWayBill());
			}else if(StringUtils.isNotBlank(xrequest.getWaybillCode())){
				//运单号字段非空  用运单号处理
				//Fixme 找时间统一orderId  packageCode waybillCode 含义 避免后续开发人员看不懂
				reverseReceive.setPackageCode(xrequest.getWaybillCode());
				reverseReceive.setOrderId(xrequest.getWaybillCode());
			}else{
				reverseReceive.setPackageCode(xrequest.getOrderId());
				reverseReceive.setOrderId(xrequest.getOrderId());
			}
			reverseReceive.setReceiveType(Integer.parseInt(xrequest.getReceiveType()));
			reverseReceive.setReceiveTime(DateHelper.parseDateTime(xrequest.getOperateTime()));
			reverseReceive.setOperatorName(xrequest.getUserName());
			reverseReceive.setCanReceive(Integer.parseInt(xrequest.getCanReceive()));
			reverseReceive.setOperatorId(xrequest.getUserCode());
			if (null != xrequest.getRejectCode()) {
				reverseReceive.setRejectCode(Integer.parseInt(xrequest.getRejectCode()));
			}
			reverseReceive.setRejectMessage(xrequest.getRejectMessage());

		} else if (JsonHelper.isJson(messageContent, ReverseReceiveRequest.class)) {
			jrequest = JsonHelper.fromJson(messageContent, ReverseReceiveRequest.class);
			this.log.debug("逆向收货消息ReverseReceiveRequest：{}" , jrequest.toString());
            reverseReceive = getReverseReceiveFromJson(jrequest);
		}

		//业务流程监控, 各系统埋点
        umpBizPoint(reverseReceive);

		//如果是移动仓内配单需要推送终端
		if(reverseReceive.getReceiveType() == 1 && waybillService.isMovingWareHouseInnerWaybill(WaybillUtil.getWaybillCode(reverseReceive.getOrderId()))) {
			if(log.isInfoEnabled()){
				log.info("wms回传移动仓内配单需要调终端接口操作妥投,reverseReceive:{}",JSON.toJSONString(reverseReceive));
			}
			movingWareHoseInnerWaybillFinish(reverseReceive);
		}

		//添加订单处理，判断是否是T单 2016-1-8
		SendDetail tsendDatail = new SendDetail();
		tsendDatail.setSendCode(reverseReceive.getSendCode());
		if (reverseReceive.getReceiveType() == 3) {//如果是备件库的,则找到其真正的send_code
			//备件库的运单号从备件库回传消息中直接截取
			if(reverseReceive.getSendCode().indexOf("-")!=-1 && reverseReceive.getSendCode().split("-").length==4){
				reverseReceive.setOrderId(reverseReceive.getSendCode().split("-")[3]);
				reverseReceive.setPackageCode(reverseReceive.getOrderId());
			}else{
				log.warn("备件库回传收货消息格式不正确，未获取到对应运单号:{}",messageContent);
				return;
			}
		}else{
			tsendDatail.setWaybillCode(Constants.T_WAYBILL + reverseReceive.getOrderId());
			List<SendDetail> sendDatailist = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
			if (sendDatailist != null && !sendDatailist.isEmpty()){
				reverseReceive.setOrderId(Constants.T_WAYBILL + reverseReceive.getOrderId());
			}
		}


		
		if (reverseReceive.getReceiveType() == 5){//如果是开放平台订单
			InvokeResult<String> newWaybilCode = reversePrintService.getNewWaybillCode(reverseReceive.getOrderId(), false);
			if (StringHelper.isNotEmpty(newWaybilCode.getData())) {
				tsendDatail.setWaybillCode(newWaybilCode.getData());
				List<SendDetail> sendDatailistMcs = this.sendDatailDao.querySendDatailsBySelective(tsendDatail);
				if (sendDatailistMcs != null && !sendDatailistMcs.isEmpty()) {
					reverseReceive.setOrderId(sendDatailistMcs.get(0).getWaybillCode());
				}
			}
		}
		this.reverseReceiveService.aftersaleReceive(reverseReceive);

		// 对于备件库系统,接受拒收消息后自动处理驳回接口
		if (reverseReceive.getReceiveType() == 3 && reverseReceive.getCanReceive() == 0) {
			ReverseReject reverseReject = new ReverseReject();
			reverseReject.setBusinessType(reverseReceive.getReceiveType());
			reverseReject.setPackageCode(reverseReceive.getPackageCode());
			reverseReject.setOrderId(reverseReceive.getPackageCode());

			if(null != xrequest.getOrgId()){
				reverseReject.setOrgId(Integer.parseInt(xrequest.getOrgId()));
			}
			if(null != xrequest.getStoreId()){
				reverseReject.setStoreId(Integer.parseInt(xrequest.getStoreId()));
			}

			reverseReject.setOperateTime(reverseReceive.getReceiveTime());
			reverseReject.setOperator(reverseReceive.getOperatorName());
			reverseReject.setOperatorCode(reverseReceive.getOperatorId());

			this.reverseRejectService.reject(reverseReject);
		}

		//添加全称跟踪
		if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5|| reverseReceive.getReceiveType() == 4 || reverseReceive.getReceiveType() == 6 || reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8 || reverseReceive.getReceiveType() == 10) {
			String sendCode = "";
			if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5 || reverseReceive.getReceiveType() == 6) {
				this.log.info("逆向添加全称跟踪sendCode:{}" , xrequest.getSendCode());
				sendCode = xrequest.getSendCode();
			} else if ((reverseReceive.getReceiveType() == 4 || reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8 || reverseReceive.getReceiveType() == 10) && jrequest != null) {
				this.log.info("逆向添加全称跟踪sendCode:{}" , jrequest.getSendCode());
				sendCode = jrequest.getSendCode();
				if(reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8  || reverseReceive.getReceiveType() == 10){
					//ECLP退备件库时
					reverseReceive.setOrderId(jrequest.getWaybillCode());
				}else{
					reverseReceive.setOrderId(reverseReceive.getPackageCode());
				}

			}
			if (reverseReceive.getReceiveType() == 3) {
				List<ReverseSpare> tReverseSpareList = sparedao.queryBySpareTranCode(sendCode);
				if (tReverseSpareList != null && tReverseSpareList.size()>0) {
					sendCode = tReverseSpareList.get(0).getSendCode();
				}else{
					return;
				}
			}

			Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(sendCode);
			if(receiveSiteCode!=null){
				WaybillStatus tWaybillStatus = new WaybillStatus();
				tWaybillStatus.setOperator(reverseReceive.getOperatorName());
				tWaybillStatus.setOperateTime(reverseReceive.getReceiveTime());
				tWaybillStatus.setWaybillCode(reverseReceive.getOrderId());
				tWaybillStatus.setPackageCode(reverseReceive.getOrderId());
				tWaybillStatus.setCreateSiteCode(receiveSiteCode);
				tWaybillStatus.setOperatorId(-1);
				tWaybillStatus.setReceiveSiteCode(receiveSiteCode);
				BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
		        if(bDto!=null ){
		        	tWaybillStatus.setCreateSiteName(bDto.getSiteName());
		        	tWaybillStatus.setReceiveSiteName(bDto.getSiteName());
		        	tWaybillStatus.setCreateSiteType(bDto.getSiteType());
		        	tWaybillStatus.setReceiveSiteType(bDto.getSiteType());
		        }

				if (reverseReceive.getReceiveType() == 3 || reverseReceive.getReceiveType() == 1 || reverseReceive.getReceiveType() == 5 || reverseReceive.getReceiveType() == 6) {
					tWaybillStatus.setSendCode(xrequest.getSendCode());
				} else if ((reverseReceive.getReceiveType() == 4 || reverseReceive.getReceiveType() == 7 ||reverseReceive.getReceiveType() == 8 || reverseReceive.getReceiveType() == 10) && jrequest != null ) {
					tWaybillStatus.setSendCode(jrequest.getSendCode());
				}

				if(reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 10){
					//ECLP退备件库时 0 代表驳回  1代表收货 2 代表部分收货
					if (reverseReceive.getCanReceive() == 0){
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
						taskService.add(this.toTask(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 1) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 2) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						tWaybillStatus.setReturnFlag(WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_HALF);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					}else {
						return;
					}
				}else if(reverseReceive.getReceiveType() == 8){
					//仓配发包裹维度的全程跟踪
					sendTraceAndUpdateWaybillStatue(jrequest,tWaybillStatus);
				}else if(reverseReceive.getReceiveType()==5 && reverseReceive.getCanReceive() == 2){
		        	//ECLP退仓半退的逻辑
					tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
					tWaybillStatus.setReturnFlag(WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_HALF);
					taskService.add(this.toTaskStatus(tWaybillStatus));
				}else if( (reverseReceive.getReceiveType() == 2 || reverseReceive.getReceiveType() == 4)
						&& reverseReceive.getCanReceive() == 1){
					//备件库售后和维修 2 4 场景的收货场景 单独处理 不回传全程跟踪由备件库系统自己实现
					if(sysConfigService.getConfigByName(SysConfigService.SYS_CONFIG_WRITER_SH_RECEIVE_WAYBILL_TRACE)){
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					}else{
						log.info("运单{}不回传收货完成全程跟踪,ReceiveType:{}",tWaybillStatus.getWaybillCode(),reverseReceive.getReceiveType());
					}
				}else{
					if (reverseReceive.getCanReceive() == 0){
						//记录驳回原因
						tWaybillStatus.setRemark(reverseReceive.getRejectMessage());
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
						taskService.add(this.toTask(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 1) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					} else if (reverseReceive.getCanReceive() == 2) {
						tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_JJREVERSE);
						taskService.add(this.toTaskStatus(tWaybillStatus));
					}
				}
			}
		}
		//更新入库单状态
		updateReverseStockInStatus(reverseReceive);

	}

    /**
     * JSON请求体的request转为逆向收货对象
     * @param jrequest
     * @return
     */
	private ReverseReceive getReverseReceiveFromJson(ReverseReceiveRequest jrequest){
        ReverseReceive reverseReceive = new ReverseReceive();
        Date date = null;
        try {
            BeanHelper.copyProperties(reverseReceive, jrequest);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(StringUtils.isNotBlank(jrequest.getReceiveTime())){
                date = sdf.parse(jrequest.getReceiveTime());
            }
            if(reverseReceive.getReceiveType() == 7 || reverseReceive.getReceiveType() == 8 || reverseReceive.getReceiveType() == 10){ //处理报文 操作人字段
                date = sdf.parse(jrequest.getOperateTime());
                reverseReceive.setOperatorName(jrequest.getOperaterName());
                reverseReceive.setOrderId(jrequest.getWaybillCode());
                reverseReceive.setPackageCode(jrequest.getWaybillCode());
                //临时方案 ECLP退备件库回传消息中的SEND_CDOE是  批次号+，+运单号
                String sendCode = jrequest.getSendCode().indexOf(",")!=-1?jrequest.getSendCode().split(",")[0]:jrequest.getSendCode();
				reverseReceive.setSendCode(sendCode);
				jrequest.setSendCode(sendCode);
            }
            reverseReceive.setReceiveTime(date);
        } catch (Exception e) {
            this.log.error("逆向收货消息转换失败：{}" ,JsonHelper.toJson(jrequest), e);
        }
        return reverseReceive;
    }

    /**
     * 业务流程监控, 各系统埋点
     * @param reverseReceive
     */
	private void umpBizPoint(ReverseReceive reverseReceive){
        try {
            //业务流程监控, 各系统埋点
            Map<String, String> data = new HashMap<String, String>();
            data.put("orderId", reverseReceive.getPackageCode());
            data.put("outboundNo", reverseReceive.getPackageCode());
            data.put("packageCode", reverseReceive.getPackageCode());

            if (reverseReceive.getReceiveType() == 1){//仓储
                Profiler.bizNode("bd_dms_reverse_receive_wms", data);
            }else if (reverseReceive.getReceiveType() == 2){//售后系统
                Profiler.bizNode("Reverse_mq_dms2stock", data);
            }else if (reverseReceive.getReceiveType() == 3){//备件库系统
                Profiler.bizNode("Reverse_mq_ams2dms", data);
            }
        } catch (Exception e) {
            this.log.error("推送UMP发生异常:{}",JsonHelper.toJson(reverseReceive), e);
        }
    }

	/**
	 * 发包裹维度的发全程跟踪
	 * @param jrequest
	 * @param tWaybillStatus
	 */
	private void sendTraceAndUpdateWaybillStatue(ReverseReceiveRequest jrequest, WaybillStatus tWaybillStatus) {

		//存放   备件条码:包裹号
		Map<String,String> sparePackCodeMap = new HashMap<String,String>();
		//回传包裹数据   0拒收 1全收 2半收 -1表示此次没回传
		Map<String,Integer> packageReceiveMap = new HashMap<>();
		//
		String waybillCode = jrequest.getWaybillCode();

		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryWaybillE(false);
		wChoice.setQueryWaybillM(false);
		wChoice.setQueryGoodList(true);
		wChoice.setQueryPackList(true);
		BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoiceNoCache(waybillCode,wChoice);
		if(baseEntity != null && baseEntity.getData() != null &&
				baseEntity.getData().getGoodsList() != null && baseEntity.getData().getGoodsList().size() > 0){
			List<Goods> goodsList = baseEntity.getData().getGoodsList();
			for (Goods goods : goodsList){
				List<SparsModel> spareList = goods.getSpareList();
				for(SparsModel sparsModel : spareList){
					sparePackCodeMap.put(sparsModel.getSpareCode(),sparsModel.getPackBarcode());
				}
			}
		}

		for(String key : sparePackCodeMap.keySet()){
			packageReceiveMap.put(sparePackCodeMap.get(key),TYPE_RECEIVE_DEFAULT);
		}

		for(Eclp2BdReceiveDetail eclp2BdReceiveDetail : jrequest.getDetailList()) {

			String spareCode = eclp2BdReceiveDetail.getBatchNo();
			String _packNo = sparePackCodeMap.get(spareCode);
			Integer quantity = eclp2BdReceiveDetail.getQuantity()==null?0:eclp2BdReceiveDetail.getQuantity(); //只有 0 1  0没收 1收了
			//只有是半收的时候才需要去判断
			if (TYPE_RECEIVE_2.equals(jrequest.getCanReceive())) {
				if (TYPE_RECEIVE_DEFAULT.equals(packageReceiveMap.get(_packNo))) {
					packageReceiveMap.put(_packNo, quantity);
				} else {
					if (!TYPE_RECEIVE_2.equals(packageReceiveMap.get(_packNo)) && !quantity.equals(packageReceiveMap.get(_packNo))) {
						packageReceiveMap.put(_packNo, TYPE_RECEIVE_2);
					}
				}
			} else {
				// 收货  和 拒收 直接给
				packageReceiveMap.put(_packNo, jrequest.getCanReceive());
			}
		}


		for(String key : packageReceiveMap.keySet()){

			tWaybillStatus.setPackageCode(key);
			if(TYPE_RECEIVE_0.equals(packageReceiveMap.get(key))){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BH);
				taskService.add(this.toTask(tWaybillStatus));
			}else if(TYPE_RECEIVE_1.equals(packageReceiveMap.get(key))){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
				taskService.add(this.toTaskStatus(tWaybillStatus));
			}else if(TYPE_RECEIVE_2.equals(packageReceiveMap.get(key))){
				tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
				tWaybillStatus.setReturnFlag(WaybillStatus.WAYBILL_RETURN_COMPLETE_FLAG_HALF);
				taskService.add(this.toTaskStatus(tWaybillStatus));
			}
		}




	}

	/**
	 * 更新入库单状态
	 *
	 * 现阶段只处理C2C入备件库场景  收货 驳回情况
	 *
	 * @param reverseReceive
	 */
	private void updateReverseStockInStatus(ReverseReceive reverseReceive){
		ReverseStockInDetail reverseStockInDetail = new ReverseStockInDetail();

		reverseStockInDetail.setWaybillCode(reverseReceive.getWaybillCode());
		reverseStockInDetail.setSendCode(reverseReceive.getSendCode());
		if(ReverseReceive.REVERSE_TYPE_C2C.equals(reverseReceive.getReceiveType())){
			reverseStockInDetail.setBusiType(ReverseStockInDetailTypeEnum.C2C_REVERSE_SPWMS.getCode());
			if(ReverseReceive.RECEIVE.equals(reverseReceive.getCanReceive())){
				reverseStockInDetailService.updateStatus(reverseStockInDetail, ReverseStockInDetailStatusEnum.REVERSE);
			}else if(ReverseReceive.REJECT.equals(reverseReceive.getCanReceive())){
				reverseStockInDetailService.updateStatus(reverseStockInDetail, ReverseStockInDetailStatusEnum.REJECT);
			}
		}
	}

    private Task toTask(WaybillStatus tWaybillStatus) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_POP);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(tWaybillStatus.getOperateType()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setOwnSign(BusinessHelper.getOwnSign());
		StringBuffer fingerprint = new StringBuffer();
		fingerprint
				.append(tWaybillStatus.getCreateSiteCode())
				.append("__")
				.append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
						: tWaybillStatus.getReceiveSiteCode())).append("_")
				.append(tWaybillStatus.getOperateType()).append("_")
				.append(tWaybillStatus.getWaybillCode()).append("_")
				.append(tWaybillStatus.getOperateTime()).append("_")
				.append(tWaybillStatus.getSendCode());
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		return task;
	}
	
	private Task toTaskStatus(WaybillStatus tWaybillStatus) {
		Task task = new Task();
		task.setTableName(Task.TABLE_NAME_WAYBILL);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword2(String.valueOf(tWaybillStatus.getWaybillCode()));
		task.setKeyword1(tWaybillStatus.getWaybillCode());
		task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
		task.setBody(JsonHelper.toJson(tWaybillStatus));

		Integer operateType = tWaybillStatus.getOperateType();
		if (operateType != null) {
			if (operateType.intValue() ==  WaybillStatus.WAYBILL_STATUS_SHREVERSE) {
				task.setType(WaybillStatus.WAYBILL_STATUS_SHREVERSE);
			} else if (operateType.intValue() ==  WaybillStatus.WAYBILL_STATUS_JJREVERSE) {
				task.setType(WaybillStatus.WAYBILL_STATUS_JJREVERSE);
			}
		}

		task.setOwnSign(BusinessHelper.getOwnSign());
		StringBuffer fingerprint = new StringBuffer();
		fingerprint
				.append(tWaybillStatus.getCreateSiteCode())
				.append("__")
				.append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
						: tWaybillStatus.getReceiveSiteCode())).append("_")
				.append(tWaybillStatus.getOperateType()).append("_")
				.append(tWaybillStatus.getWaybillCode()).append("_")
				.append(tWaybillStatus.getOperateTime()).append("_")
				.append(tWaybillStatus.getSendCode());
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		return task;
	}


	/**
	 * 移动仓内配单调终端的接口，操作妥投
	 * @param reverseReceive
	 */
	private void movingWareHoseInnerWaybillFinish(ReverseReceive reverseReceive){
		String waybillCode = WaybillUtil.getWaybillCode(reverseReceive.getOrderId());
		if(StringUtils.isBlank(waybillCode)){
			log.warn("移动仓内配单调终端接口操作妥投，接收到的wms回传报文无法获取运单号.reverseReceive: {}", JSON.toJSONString(reverseReceive));
			return;
		}
		String sendCode = reverseReceive.getSendCode();
		//通过批次号查send_m表获取操作站点和操作人信息
		Integer siteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
		Integer userCode = null;
		String userName = "";
		Date sendTime = reverseReceive.getReceiveTime();
		if(sendTime == null){
			sendTime = new Date();
		}

		SendM sendM = sendMDao.selectBySendCode(sendCode);
		if(sendM == null){
			log.warn("移动仓内配单调终端接口操作妥投,根据批次号查sendM数据为空.sendCode:{}" , sendCode);
			return ;

		}
		userCode = sendM.getCreateUserCode();
		userName = sendM.getCreateUser();

		OrderDeliverBody body = new OrderDeliverBody();
		body.setWaybillCode(waybillCode);
		body.setPayee(userName); //收款人设置为分拣中心发货人
		body.setCourierName(userName); //配送员名称设置为分拣中心发货人
		body.setPayWayId(WORK_TASK_PARAM_PAY_WAY_ID);//2表示在线支付
		body.setPayWayName(WORK_TASK_PARAM_PAY_WAY_NAME);
		body.setTimepaid(sendTime);//妥投时间
		body.setSiteId(siteCode);//操作站点设置为操作发货的分拣中心
		body.setOperatorUserId(userCode); //操作人编号
		body.setOperatorType(WORK_TASK_PARAM_OPERATE_TYPE);//操作类型
		body.setRemark(WORK_TASK_PARAM_REMARK);
		body.setAmount(WORK_TASK_PARAM_AMOUNT);//实收金额received_money
		body.setPrice(WORK_TASK_PARAM_PRICE);//应收金额rec_money
		body.setSource(SOURCE_CODE_DMS_FINISHED);//系统来源

		OrderDeliverWorkTask task = new OrderDeliverWorkTask();
		task.setRefId(waybillCode);//运单号
		task.setTaskType(WORK_TASK_PARAM_TASK_TYPE);//妥投任务，类型为7
		task.setTaskExeCount(0);
		task.setStatus(1);
		task.setCreateSiteId(siteCode);
		task.setCreateTime(sendTime);
		task.setUpdateTime(sendTime);
		task.setRemark(WORK_TASK_PARAM_REMARK);//
		task.setYn(1);
		task.setOwnsign("BASE");
		task.setOrderDeliverBodys(Arrays.asList(body));

		try {
			if(log.isDebugEnabled()){
				log.debug("移动仓内配单调用终端接口操作妥投:{}" , JSON.toJSONString(task));
			}
			if(!workTaskServiceManager.orderDeliverWorkTaskEntry(task)){
				log.warn("移动仓内配单调用终端接口操作妥投失败，返回值为false:{}" , JSON.toJSONString(task));
			}
		}catch (Exception e){
			log.error("移动仓内配单调用终端接口操作妥投异常:{}" , JSON.toJSONString(task),e);
		}
	}
}
