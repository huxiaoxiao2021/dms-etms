package com.jd.bluedragon.distribution.jy.service.common;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.board.domain.BindBoardRequest;
import com.jd.bluedragon.distribution.board.domain.OperatorInfo;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.jy.dao.common.JyOperateFlowDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.ComboardTaskDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowData;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.transboard.api.dto.BoardBoxResult;
import com.jd.transboard.api.dto.BoxDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 拣运-附件接口实现类
 *
 * @author hujiping
 * @date 2023/4/19 8:48 PM
 */
@Service("jyOperateFlowService")
public class JyOperateFlowServiceImpl implements JyOperateFlowService {
	
	private final Logger logger = LoggerFactory.getLogger(JyOperateFlowServiceImpl.class);
	
    @Autowired
    private JyOperateFlowDao jyOperateFlowDao;
    
    @Autowired
    @Qualifier("jyOperateFlowMqProducer")
    private DefaultJMQProducer jyOperateFlowMqProducer;
    
    @Autowired
    DmsConfigManager dmsConfigManager;

	@Autowired
	private SequenceGenAdaptor sequenceGenAdaptor;

	@Autowired
	@Qualifier("dmsOperateTrackProducer")
	private DefaultJMQProducer dmsOperateTrackProducer;
    
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.insert", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public int insert(JyOperateFlowDto data) {
		if(data == null || StringUtils.isBlank(data.getOperateBizKey())) {
			logger.warn("jyOperateFlowService-insert-fail! operateBizKey值不能为空！");
			return 0;
		}
		return jyOperateFlowDao.insert(data);
	}

	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.sendMq", mState = {JProEnum.TP, JProEnum.FunctionError})
	public int sendMq(JyOperateFlowMqData mqData) {
		if(!Boolean.TRUE.equals(dmsConfigManager.getPropertyConfig().getSendJyOperateFlowMqSwitch())) {
			return 0;
		}
		if (logger.isInfoEnabled()) {
			logger.info("jyOperateFlowMqProducer-sendMq|发送流水:mqData={}", JsonHelper.toJsonMs(mqData));
		}
		jyOperateFlowMqProducer.sendOnFailPersistent(mqData.getOperateBizKey(), JsonHelper.toJson(mqData));
		return 1;
	}
	
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.sendMqList", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public int sendMqList(List<JyOperateFlowMqData> mqDataList) {
		if (!Boolean.TRUE.equals(dmsConfigManager.getPropertyConfig().getSendJyOperateFlowMqSwitch())) {
			return 0;
		}
		List<Message> msgList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(mqDataList)) {
			for (JyOperateFlowMqData mqData : mqDataList) {
				if (mqData == null) {
					continue;
				}
				msgList.add(new Message(jyOperateFlowMqProducer.getTopic(), JsonHelper.toJson(mqData), mqData.getOperateBizKey()));
				if (logger.isInfoEnabled()) {
					logger.info("jyOperateFlowMqProducer-sendMqList|发送流水:mqData={}", JsonHelper.toJsonMs(mqData));
				}
			}
			jyOperateFlowMqProducer.batchSendOnFailPersistent(msgList);
			return mqDataList.size();
		}
		return 0;
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.createOperateFlowId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public Long createOperateFlowId() {
		try {
			return sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
		} catch (Exception e) {
			logger.error("createOperateFlowId|生成操作流水主键出现异常:", e);
		}
		return null;
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendOperateTrack", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendOperateTrack(WaybillStatus waybillStatus) {
		try {
			if (!dmsConfigManager.getPropertyConfig().isOperateFlowNewSwitch()) {
				return;
			}
			// 不污染源对象，重新复制一份
			WaybillStatus dmsOperateTrack = new WaybillStatus();
			BeanUtils.copyProperties(waybillStatus, dmsOperateTrack);
			// 组装参数
			assembleData(dmsOperateTrack);
			// 获取有效单号
			String barCode = getBarCode(dmsOperateTrack);
			// 消息业务ID格式：操作码+单号
			String businessId = dmsOperateTrack.getOperateType() + barCode;
			dmsOperateTrackProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(dmsOperateTrack));
			if (logger.isInfoEnabled()) {
				logger.info("sendOperateTrack|发送操作轨迹:businessId={},waybillStatus={}", businessId, JsonHelper.toJsonMs(dmsOperateTrack));
			}
		} catch (Exception e) {
			logger.error("sendOperateTrack|发送操作轨迹出现异常:waybillStatus={}", JsonHelper.toJsonMs(waybillStatus), e);
		}
	}

	private void assembleData(WaybillStatus waybillStatus) {
		// 原始操作时间
		Date operateTime = waybillStatus.getOperateTime();
		// 操作信息对象
		OperatorData operatorData = waybillStatus.getOperatorData();
		if (operatorData != null) {
			// 记录原始操作时间
			operatorData.setOriginOperateTime(operateTime);
		}
		if (operateTime != null) {
			// 时间转换，跟运单数据库保持一致
			Date newOperateTime = getRealUpdateTime(operateTime);
			waybillStatus.setOperateTime(newOperateTime);
		}
	}

	private String getBarCode(WaybillStatus waybillStatus) {
		String packageCode = waybillStatus.getPackageCode();
		String waybillCode = waybillStatus.getWaybillCode();
		String boxCode = waybillStatus.getBoxCode();
		if (StringUtils.isNotEmpty(packageCode)) {
			return packageCode;
		}
		if (StringUtils.isNotEmpty(waybillCode)) {
			return waybillCode;
		}
		if (StringUtils.isNotEmpty(boxCode)) {
			return boxCode;
		}
		return null;
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendBoardOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public <T> void sendBoardOperateFlowData(T t, BoardBoxResult boardBoxResult, OperateBizSubTypeEnum subTypeEnum) {
		BindBoardRequest bindBoardRequest;
		try {
			if ( t instanceof BindBoardRequest) {
				bindBoardRequest = (BindBoardRequest) t;
				Long operateFlowId = sendBoardCore(bindBoardRequest, boardBoxResult, subTypeEnum);
				bindBoardRequest.setOperateFlowId(operateFlowId);
			} else if (t instanceof ComboardScanReq) {
				ComboardScanReq comboardScanReq = (ComboardScanReq) t;
				// 参数转换
				bindBoardRequest = createBindBoardRequest(comboardScanReq);
				Long operateFlowId = sendBoardCore(bindBoardRequest, boardBoxResult, subTypeEnum);
				comboardScanReq.setOperateFlowId(operateFlowId);
			} else if (t instanceof BoardCombinationRequest) {
				BoardCombinationRequest boardCombinationRequest = (BoardCombinationRequest) t;
				// 参数转换
				bindBoardRequest = createBindBoardRequest(boardCombinationRequest);
				Long operateFlowId = sendBoardCore(bindBoardRequest, boardBoxResult, subTypeEnum);
				boardCombinationRequest.setOperateFlowId(operateFlowId);
			} else if (t instanceof ComboardTaskDto) {
				ComboardTaskDto comboardTaskDto = (ComboardTaskDto) t;
				// 参数转换
				bindBoardRequest = createBindBoardRequest(comboardTaskDto);
				// <包裹号, 操作流水表主键>
				Map<String, Long> map = batchSendBoardCore(bindBoardRequest, boardBoxResult, subTypeEnum);
				comboardTaskDto.setOperateFlowMap(map);
			}
		} catch (Exception e) {
			logger.error("发送组板操作流水消息出现异常:request={}", JsonHelper.toJsonMs(t), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendUnsealOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendUnsealOperateFlowData(SealCarDto sealCarDto, NewSealVehicleRequest request) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData unsealFlowMq = BeanConverter.convertToJyOperateFlowMqData(sealCarDto);
			JyOperateFlowData jyOperateFlowData = unsealFlowMq.getJyOperateFlowData();
			// 填充操作信息对象
			OperatorData operatorData = request.getOperatorData();
			jyOperateFlowData.setOperatorData(operatorData);
			// 业务子类型
			unsealFlowMq.setOperateBizSubType(request.getBizType());
			sendMq(unsealFlowMq);
		} catch (Exception e) {
			logger.error("发送解封车操作流水消息出现异常:sealCarDto={},request={}", JsonHelper.toJsonMs(sealCarDto),
					JsonHelper.toJson(request), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendInspectOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendInspectOperateFlowData(List<Inspection> inspectionList, OperateBizSubTypeEnum subTypeEnum) {
		// 用于异常时日志打印
		Inspection originInspection = null;
		try {
			// 验货操作流水消息集合
			List<JyOperateFlowMqData> inspectionFlowMqList = new ArrayList<>();
			for (Inspection inspection : inspectionList) {
				if (logger.isInfoEnabled()) {
					logger.info("sendInspectOperateFlowData|发送验货流水:inspection={}", JsonHelper.toJsonMs(inspection));
				}
				originInspection = inspection;
				// 组装操作流水实体
				JyOperateFlowMqData inspectionFlowMq = BeanConverter.convertToJyOperateFlowMqData(inspection);
				// 业务子类型
				inspectionFlowMq.setOperateBizSubType(subTypeEnum.getCode());
				// 设置操作流水表业务主键
				inspectionFlowMq.setId(inspection.getOperateFlowId());
				inspectionFlowMqList.add(inspectionFlowMq);
			}
			// 批量发送验货操作流水消息
			sendMqList(inspectionFlowMqList);
		} catch (Exception e) {
			logger.error("发送验货操作流水消息出现异常:inspection={}", JsonHelper.toJsonMs(originInspection), e);
		}
	}


	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendReceiveOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendReceiveOperateFlowData(Receive receive, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData boardFlowMq = BeanConverter.convertToJyOperateFlowMqData(receive);
			// 业务子类型
			boardFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			boardFlowMq.setId(operateFlowId);
			sendMq(boardFlowMq);
			receive.setOperateFlowId(operateFlowId);
		} catch (Exception e) {
			logger.error("发送收货操作流水消息出现异常:request={}", JsonHelper.toJsonMs(receive), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendSoringOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendSoringOperateFlowData(Sorting sorting, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData sortingCancelFlowMq = BeanConverter.convertToJyOperateFlowMqData(sorting);
			// 业务子类型
			sortingCancelFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			sortingCancelFlowMq.setId(operateFlowId);
			sendMq(sortingCancelFlowMq);
			waybillStatus.setOperateFlowId(operateFlowId);
			if (logger.isInfoEnabled()) {
				logger.info("sendSoringOperateFlowData|发送分拣流水:sorting={},sortingMQ={},waybillStatus={}", JsonHelper.toJsonMs(sorting),
						JsonHelper.toJsonMs(sortingCancelFlowMq), JsonHelper.toJsonMs(waybillStatus));
			}
		} catch (Exception e) {
			logger.error("发送分拣操作流水消息出现异常:request={}", JsonHelper.toJsonMs(waybillStatus), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendDeliveryOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public JyOperateFlowMqData createDeliveryOperateFlowData(SendDetail sendDetail, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData deliveryCancelFlowMq = BeanConverter.convertToJyOperateFlowMqData(sendDetail);
			// 业务子类型
			deliveryCancelFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			deliveryCancelFlowMq.setId(operateFlowId);
			waybillStatus.setOperateFlowId(operateFlowId);
			if (logger.isInfoEnabled()) {
				logger.info("createDeliveryOperateFlowData|发送发货流水:sendDetail={},deliveryMQ={},waybillStatus={}", JsonHelper.toJsonMs(sendDetail),
						JsonHelper.toJsonMs(deliveryCancelFlowMq), JsonHelper.toJsonMs(waybillStatus));
			}
			return deliveryCancelFlowMq;
		} catch (Exception e) {
			logger.error("发送发货操作流水消息出现异常:request={}", JsonHelper.toJsonMs(waybillStatus), e);
		}
		return null;
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendDeliveryOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendDeliveryOperateFlowData(SendDetail sendDetail, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData deliveryCancelFlowMq = createDeliveryOperateFlowData(sendDetail, waybillStatus, subTypeEnum);
			if (deliveryCancelFlowMq == null) {
				return;
			}
			sendMq(deliveryCancelFlowMq);
		} catch (Exception e) {
			logger.error("发送发货操作流水消息出现异常:request={}", JsonHelper.toJsonMs(waybillStatus), e);
		}
	}


	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendAbnormalOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendAbnormalOperateFlowData(AbnormalWayBill abnormalWayBill, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData sortingCancelFlowMq = BeanConverter.convertToJyOperateFlowMqData(abnormalWayBill);
			// 业务子类型
			sortingCancelFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			sortingCancelFlowMq.setId(abnormalWayBill.getOperateFlowId());
			sortingCancelFlowMq.setOperateKey(String.valueOf(abnormalWayBill.getId()));
			sendMq(sortingCancelFlowMq);
		} catch (Exception e) {
			logger.error("发送异常处理操作流水出现异常:abnormalWayBill={}", JsonHelper.toJsonMs(abnormalWayBill), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendWeightVolumeOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendWeightVolumeOperateFlowData(WeightVolumeEntity entity, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData weightVolumeFlowMq = BeanConverter.convertToJyOperateFlowMqData(entity);
			// 业务子类型
			weightVolumeFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			sendMq(weightVolumeFlowMq);
		} catch (Exception e) {
			logger.error("发送称重操作流水出现异常:weightVolumeEntity={}", JsonHelper.toJsonMs(entity), e);
		}
	}

	private Long sendBoardCore(BindBoardRequest bindBoardRequest, BoardBoxResult boardBoxResult, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData boardFlowMq = createBoardOperateFlowMqData(bindBoardRequest, boardBoxResult, subTypeEnum);
			if (boardFlowMq == null) {
				return null;
			}
			// 发送
			sendMq(boardFlowMq);
			// 返回操作流水表主键
			return boardFlowMq.getId();
		} catch (Exception e) {
			logger.error("发送组板操作流水消息核心逻辑出现异常:request={},boardBoxResult={},subTypeEnum={}",
					JsonHelper.toJsonMs(bindBoardRequest), JsonHelper.toJsonMs(boardBoxResult), subTypeEnum.getCode(), e);
		}
		return null;
	}

	private JyOperateFlowMqData createBoardOperateFlowMqData(BindBoardRequest bindBoardRequest, BoardBoxResult boardBoxResult,
															 OperateBizSubTypeEnum subTypeEnum) {
		// 操作流水消息对象
		JyOperateFlowMqData boardFlowMq = null;
		try {
			if (bindBoardRequest == null) {
				return null;
			}
			if (boardBoxResult != null && boardBoxResult.getId() != null) {
				bindBoardRequest.setOperateKey(String.valueOf(boardBoxResult.getId()));
			}
			// 组装操作流水实体
			boardFlowMq = BeanConverter.convertToJyOperateFlowMqData(bindBoardRequest);
			// 业务子类型
			boardFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			boardFlowMq.setId(operateFlowId);
		} catch (Exception e) {
			logger.error("构建组板操作流水消息对象出现异常:request={},boardBoxResult={},subTypeEnum={}",
					JsonHelper.toJsonMs(bindBoardRequest), JsonHelper.toJsonMs(boardBoxResult), subTypeEnum.getCode(), e);
		}
		return boardFlowMq;
	}


	private BindBoardRequest createBindBoardRequest(ComboardScanReq request) {
		BindBoardRequest bindBoardRequest = new BindBoardRequest();
		bindBoardRequest.setBarcode(request.getBarCode());
		OperatorInfo operatorInfo = new OperatorInfo();
		operatorInfo.setSiteCode(request.getCurrentOperate().getSiteCode());
		bindBoardRequest.setOperatorInfo(operatorInfo);
		com.jd.bluedragon.common.dto.base.request.OperatorData originOperatorData = request.getCurrentOperate().getOperatorData();
		OperatorData destOperatorData = new OperatorData();
		if (originOperatorData != null) {
			BeanUtils.copyProperties(originOperatorData, destOperatorData);
		}
		bindBoardRequest.setOperatorData(destOperatorData);
		return bindBoardRequest;
	}

	private BindBoardRequest createBindBoardRequest(BoardCombinationRequest request) {
		BindBoardRequest bindBoardRequest = new BindBoardRequest();
		bindBoardRequest.setBarcode(request.getBoxOrPackageCode());
		OperatorInfo operatorInfo = new OperatorInfo();
		operatorInfo.setSiteCode(request.getSiteCode());
		bindBoardRequest.setOperatorInfo(operatorInfo);
		bindBoardRequest.setOperatorData(request.getOperatorData());
		return bindBoardRequest;
	}

	private BindBoardRequest createBindBoardRequest(ComboardTaskDto request) {
		BindBoardRequest bindBoardRequest = new BindBoardRequest();
		bindBoardRequest.setBarcode(request.getBarCode());
		OperatorInfo operatorInfo = new OperatorInfo();
		operatorInfo.setSiteCode(request.getStartSiteId());
		bindBoardRequest.setOperatorInfo(operatorInfo);
		bindBoardRequest.setOperatorData(request.getOperatorData());
		return bindBoardRequest;
	}

	private Map<String, Long> batchSendBoardCore(BindBoardRequest bindBoardRequest, BoardBoxResult boardBoxResult, OperateBizSubTypeEnum subTypeEnum) {
		Map<String, Long> map = new HashMap<>();
		try {
			if (boardBoxResult != null) {
				List<BoxDto> boxList = boardBoxResult.getBoxList();
				if (CollectionUtils.isNotEmpty(boxList)) {
					List<JyOperateFlowMqData> boardOperateFlowMqList = new ArrayList<>(boxList.size());
					for (BoxDto boxDto : boxList) {
						bindBoardRequest.setBarcode(boxDto.getBoxCode());
						bindBoardRequest.setOperateKey(String.valueOf(boxDto.getId()));
						// 操作流水表主键
						Long operateFlowId = null;
						// 组装操作流水消息对象
						JyOperateFlowMqData boardOperateFlowMq = createBoardOperateFlowMqData(bindBoardRequest, boardBoxResult, subTypeEnum);
						if (boardOperateFlowMq != null) {
							operateFlowId = boardOperateFlowMq.getId();
							boardOperateFlowMqList.add(boardOperateFlowMq);
						}
						map.put(boxDto.getBoxCode(), operateFlowId);
					}
					// 批量发送操作流水消息
					sendMqList(boardOperateFlowMqList);
				}
			}
		} catch (Exception e) {
			logger.error("批量发送组板操作流水消息出现异常:request={},boardBoxResult={},subTypeEnum={}",
					JsonHelper.toJsonMs(bindBoardRequest), JsonHelper.toJsonMs(boardBoxResult), subTypeEnum.getCode(), e);
		}
		return map;
	}

	/**
	 * mysql在5.6.4之前的版本中是不保存毫秒数，直接舍弃掉；在5.6.4以后毫秒数在低于500的时候会舍弃掉，大于等于500会进位，这个可以在mysql官方文档中找到。
	 * 视频追溯也遇到这个问题，我们给运单传的全程跟踪操作时间是毫秒，运单接收到直接存mysql，遇到毫秒数大于500自动加了1秒
	 * 所以此处需要转换成跟运单库里一样的时间
	 */
	private Date getRealUpdateTime(Date operateDate) {
		long operateTime = operateDate.getTime();
		String operateTimeStr = String.valueOf(operateTime);
		// 获取当前时间戳的毫秒数
		long milliSeconds = Long.parseLong(operateTimeStr.substring(operateTimeStr.length() - Constants.CONSTANT_NUMBER_THREE));
		// 如果大于500则进位
		if (milliSeconds >= Constants.CONSTANT_FIVE_HUNDRED) {
			return new Date(operateTime + (Constants.CONSTANT_ONE_THOUSAND - milliSeconds));
		} else {
			// 如果小于500则返回原值
			return operateDate;
		}
	}

}
