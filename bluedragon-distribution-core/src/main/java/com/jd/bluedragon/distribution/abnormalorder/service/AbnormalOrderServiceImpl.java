package com.jd.bluedragon.distribution.abnormalorder.service;

import com.jd.bluedragon.core.message.MessageDestinationConstant;
import com.jd.bluedragon.distribution.abnormalorder.dao.AbnormalOrderDao;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;
import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrderMq;
import com.jd.bluedragon.distribution.api.response.RefundReason;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.message.produce.client.MessageClient;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.etms.waybill.handler.WaybillSyncParameterExtend;
import com.jd.etms.waybill.wss.WaybillAddWS;
import com.jd.etms.waybill.wss.WaybillSyncWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("AbnormalOrderService")
public class AbnormalOrderServiceImpl implements AbnormalOrderService {

	private static final String FXM_MQ_ADDRESS = "pushFXM";
	
	private static final Integer OPERATE_TYPE_CODE = 180;
	
	private static final Integer SITE_CENTER_CODE = 64;

	private Logger log = Logger.getLogger(AbnormalOrderServiceImpl.class);

	@Autowired
	IPushPackageToMqService pushMqService;
	
	@Autowired
	AbnormalOrderDao abnormalOrderDao;
	
    @Autowired
    WaybillSyncWS waybillSyncWebService;
    
    @Autowired
    BaseService baseService;

	@Autowired
	private MessageClient messageClient;

	@Autowired
	private WaybillAddWS waybillAddWS;

	@Override
	public AbnormalOrder queryAbnormalOrderByOrderId(String orderId){
		return abnormalOrderDao.query(orderId);
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateResult(AbnormalOrder abnormalOrder){
		abnormalOrderDao.updateResult(abnormalOrder);
	}
	
	@Override
	@JProfiler(jKey = "DMSWEB.AbnormalOrderService.pushNewDataFromPDA", mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public HashMap<String/*运单号*/,Integer/*操作结果*/> pushNewDataFromPDA(AbnormalOrder[] abnormalOrders) {
		HashMap<String/*运单号*/,Integer/*操作结果*/> result = new HashMap<String, Integer>();
		ArrayList<AbnormalOrderMq> mqList = new ArrayList<AbnormalOrderMq>();
		List<WaybillSyncParameter> waybillList = new ArrayList<WaybillSyncParameter>();
		log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA 配送外呼处理开始");
		
		for(AbnormalOrder abnormalOrder: abnormalOrders){
			AbnormalOrder tmpvo = abnormalOrderDao.query(abnormalOrder.getOrderId());
			WaybillSyncParameter tmpWaybill = dealWaybillSyncParameter(abnormalOrder);
			
			dealFingerPrint(abnormalOrder);
			AbnormalOrderMq tmpmq = new AbnormalOrderMq(abnormalOrder);			
						
			boolean isHave = tmpvo!=null;
			String waybillcode = abnormalOrder.getOrderId();
			
			log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA waybillcode:" + waybillcode + "\t isHave:" + isHave);
			if(isHave ){
				if(tmpvo.getIsCancel().equals(AbnormalOrder.CANCEL) || tmpvo.getIsCancel().equals(AbnormalOrder.WAIT)){
					/*已取消或在等待结果*/
					log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA waybillcode:" + waybillcode + "\t IsCancel:" + tmpvo.getIsCancel() + "\t已取消或在等待结果");
					result.put(waybillcode, tmpvo.getIsCancel());
					continue;	
				}else if(tmpvo.getIsCancel().equals(AbnormalOrder.NOTCANCEL)){
					/*重新标记[等待]标志*/
					abnormalOrder.setIsCancel(AbnormalOrder.WAIT);
				}
			}
			
			int tmpresult = isHave?abnormalOrderDao.updateSome(abnormalOrder):abnormalOrderDao.insert(abnormalOrder);
			log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA waybillcode:" + waybillcode + "\t 数据库操作结果:" + tmpresult);
			result.put(waybillcode, isHave?AbnormalOrder.NOTCANCEL:AbnormalOrder.NEW);
			
			mqList.add(tmpmq);
			waybillList.add(tmpWaybill);

			/** 发质控和全程跟踪 */
			try {
				log.warn("分拣中心外呼申请发质控和全程跟踪开始。运单号" + abnormalOrder.getOrderId());
				toWaybillTraceWS(abnormalOrder);  // 推全程跟踪
				toQualityControlMQ(abnormalOrder);  // 推质控
			} catch (Exception ex) {
				log.error("分拣中心异常节点配送外呼推全程跟踪、质控发生异常。" + ex);
			}
		}
		/*推送MQ*/
		log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA pushMq... :" + mqList.size());
		pushMq(mqList);
		/*更新运单信息*/
		log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA pushWaybill... :" + waybillList.size());
		pushWaybill(waybillList);

		log.info("AbnormalOrderServiceImpl.pushNewDataFromPDA success");
		return result;
	}


	public void toQualityControlMQ(AbnormalOrder abnormalOrder){
		log.warn("分拣中心外呼申请发质控转换之前数据为" + JsonHelper.toJson(abnormalOrder));
		QualityControl qualityControl = new QualityControl();
		qualityControl.setBlameDept(abnormalOrder.getCreateSiteCode());
		qualityControl.setBlameDeptName(abnormalOrder.getCreateSiteName());
		qualityControl.setCreateTime(abnormalOrder.getOperateTime());
		qualityControl.setCreateUserId(abnormalOrder.getCreateUserCode());
		qualityControl.setCreateUserName(abnormalOrder.getCreateUser());
		qualityControl.setMessageType(QualityControl.QC_FXM);
		qualityControl.setBoxCode("null");
		qualityControl.setWaybillCode(abnormalOrder.getOrderId());
		qualityControl.setTypeCode(abnormalOrder.getAbnormalCode2() + "");
		qualityControl.setSystemName(QualityControl.SYSTEM_NAME);
		qualityControl.setExtraCode(abnormalOrder.getFingerprint());
		qualityControl.setReturnState("null");
		log.warn("分拣中心外呼申请发质控消息为" + JsonHelper.toJson(qualityControl));
		messageClient.sendMessage(MessageDestinationConstant.QualityControlFXMMQ.getName(), JsonHelper.toJson(qualityControl),abnormalOrder.getOrderId());

	}

	public void toWaybillTraceWS(AbnormalOrder abnormalOrder){
		BdTraceDto bdTraceDto = new BdTraceDto();
		bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_QC);
		bdTraceDto.setOperatorSiteId(abnormalOrder.getCreateSiteCode());
		bdTraceDto.setOperatorSiteName(abnormalOrder.getCreateSiteName());
		bdTraceDto.setOperatorTime(abnormalOrder.getOperateTime());
		bdTraceDto.setOperatorUserId(abnormalOrder.getCreateUserCode());
		bdTraceDto.setOperatorUserName(abnormalOrder.getCreateUser());
		bdTraceDto.setWaybillCode(abnormalOrder.getOrderId());
//		bdTraceDto.setOperatorDesp("包裹记录【" + abnormalOrder.getAbnormalReason2() + "】异常");
		bdTraceDto.setOperatorDesp(abnormalOrder.getTrackContent());
		waybillAddWS.sendBdTrace(bdTraceDto);
	}

	public RefundReason[] queryRefundReason(){

//		Integer[] code = {1/*订单地址超区（COD超区、奢侈品超区、合约计划超区、换新订单超区）*/,
//				15/*违禁品无法发货*/,
//				16/*货物体积、重量超限无法发货（货物体积超大、货物超长、订单重量超重、货物超宽）*/,
//				17/*超出第三方配送金额限制*/,
//				8/*丢失或少件无法发出（一单多件丢其中一件或多件、商品空盒、一单一件整件丢失）*/,
//				4/*包装损坏无法发货*/,
//				18/*商品损坏无法发货*/
//				};
		return AbnormalOrderUtil.getInstance().getRefundReason(baseService);
		
	}
	

	private void pushMq(ArrayList<AbnormalOrderMq> mqList){
		for(AbnormalOrderMq mq :mqList){
			String body = JsonHelper.toJson(mq);
			String busiId = mq.getOrderId();
			pushMqService.pubshMq(FXM_MQ_ADDRESS, body, busiId);
		}
	}
	
	private void pushWaybill(List<WaybillSyncParameter> waybillList){
		if(waybillList!=null && waybillList.size()>0){
			waybillSyncWebService.batchUpdateStateByCode(waybillList);
		}
	}
	
	private void dealFingerPrint(AbnormalOrder order){
		order.setFingerprint(Md5Helper.encode(order.getCreateSiteCode() + "_"
	                + order.getCreateUserErp() + "_" + order.getOrderId() + "_" + order.getOperateTime() + "_"
	                + System.currentTimeMillis()));
	}
	
	private WaybillSyncParameter dealWaybillSyncParameter(AbnormalOrder abnormalOrder){
		WaybillSyncParameter parameter = new WaybillSyncParameter();
		parameter.setOperatorCode(abnormalOrder.getOrderId());
		parameter.setOperatorId(abnormalOrder.getCreateUserCode());
		parameter.setOperatorName(abnormalOrder.getCreateUser());
		parameter.setZdId(abnormalOrder.getCreateSiteCode());
		parameter.setZdType(SITE_CENTER_CODE);
		parameter.setZdName(abnormalOrder.getCreateSiteName());
		parameter.setOperateTime(abnormalOrder.getOperateTime());
		parameter.setRemark(abnormalOrder.getAbnormalReason1() + "-" + abnormalOrder.getAbnormalReason2());
		
		WaybillSyncParameterExtend extend = new WaybillSyncParameterExtend();
		extend.setOperateType(OPERATE_TYPE_CODE);
		
		parameter.setWaybillSyncParameterExtend(extend);
		
		return parameter;

	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		AbnormalOrderUtil.getInstance().clear();
	}
}
