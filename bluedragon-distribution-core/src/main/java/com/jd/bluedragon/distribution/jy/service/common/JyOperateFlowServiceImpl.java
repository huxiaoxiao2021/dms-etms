package com.jd.bluedragon.distribution.jy.service.common;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.board.domain.BindBoardRequest;
import com.jd.bluedragon.distribution.jy.dao.common.JyOperateFlowDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.ComboardTaskDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.transboard.api.dto.BoardBoxResult;
import com.jd.transboard.api.dto.BoxDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		jyOperateFlowMqProducer.sendOnFailPersistent(mqData.getOperateBizKey(), JsonHelper.toJson(mqData));
		return 1;
	}
	
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.service.JyOperateFlowServiceImpl.sendMqList", mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public int sendMqList(List<JyOperateFlowMqData> mqDataList) {
		if(!Boolean.TRUE.equals(dmsConfigManager.getPropertyConfig().getSendJyOperateFlowMqSwitch())) {
			return 0;
		}
		List<Message> msgList = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(mqDataList)) {
			for(JyOperateFlowMqData mqData : mqDataList) {
				msgList.add(new Message(jyOperateFlowMqProducer.getTopic(), JsonHelper.toJson(mqData), mqData.getOperateBizKey()));
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
			// 消息业务ID格式：操作码+单号
			String businessId = waybillStatus.getOperateType() + waybillStatus.getPackageCode();
			dmsOperateTrackProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(waybillStatus));
			logger.info("sendOperateTrack|发送分拣操作轨迹:waybillStatus={}", JsonHelper.toJson(waybillStatus));
		} catch (Exception e) {
			logger.error("sendOperateTrack|发送分拣操作轨迹出现异常:waybillStatus={}", JsonHelper.toJson(waybillStatus), e);
		}
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
			logger.error("发送组板操作流水消息出现异常:request={}", JsonHelper.toJson(t), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendReceiveOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendReceiveOperateFlowData(Receive receive) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData boardFlowMq = BeanConverter.convertToJyOperateFlowMqData(receive);
			// 业务子类型
			boardFlowMq.setOperateBizSubType(OperateBizSubTypeEnum.RECEIVE.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			boardFlowMq.setId(operateFlowId);
			sendMq(boardFlowMq);
			receive.setOperateFlowId(operateFlowId);
		} catch (Exception e) {
			logger.error("发送收货操作流水消息出现异常:request={}", JsonHelper.toJson(receive), e);
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
		} catch (Exception e) {
			logger.error("发送分拣操作流水消息出现异常:request={}", JsonHelper.toJson(waybillStatus), e);
		}
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER, jKey = "DMS.service.JyOperateFlowServiceImpl.sendDeliveryOperateFlowData", mState = {JProEnum.TP, JProEnum.FunctionError})
	public void sendDeliveryOperateFlowData(SendDetail sendDetail, WaybillStatus waybillStatus, OperateBizSubTypeEnum subTypeEnum) {
		try {
			// 组装操作流水实体
			JyOperateFlowMqData sortingCancelFlowMq = BeanConverter.convertToJyOperateFlowMqData(sendDetail);
			// 业务子类型
			sortingCancelFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			sortingCancelFlowMq.setId(operateFlowId);
			sendMq(sortingCancelFlowMq);
			waybillStatus.setOperateFlowId(operateFlowId);
		} catch (Exception e) {
			logger.error("发送发货操作流水消息出现异常:request={}", JsonHelper.toJson(waybillStatus), e);
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
			logger.error("发送异常处理操作流水出现异常:abnormalWayBill={}", JsonHelper.toJson(abnormalWayBill), e);
		}
	}

	private Long sendBoardCore(BindBoardRequest bindBoardRequest, BoardBoxResult boardBoxResult, OperateBizSubTypeEnum subTypeEnum) {
		try {
			if (bindBoardRequest == null) {
				return null;
			}
			if (boardBoxResult != null && boardBoxResult.getId() != null) {
				bindBoardRequest.setOperateKey(String.valueOf(boardBoxResult.getId()));
			}
			// 组装操作流水实体
			JyOperateFlowMqData boardFlowMq = BeanConverter.convertToJyOperateFlowMqData(bindBoardRequest);
			// 业务子类型
			boardFlowMq.setOperateBizSubType(subTypeEnum.getCode());
			// 提前生成操作流水表业务主键
			Long operateFlowId = sequenceGenAdaptor.newId(Constants.TABLE_JY_OPERATE_FLOW);
			boardFlowMq.setId(operateFlowId);
			sendMq(boardFlowMq);
			return operateFlowId;
		} catch (Exception e) {
			logger.error("发送组板操作流水消息核心逻辑出现异常:request={},boardBoxResult={},subTypeEnum={}",
					JsonHelper.toJson(bindBoardRequest), JsonHelper.toJson(boardBoxResult), subTypeEnum.getCode(), e);
		}
		return null;
	}


	private BindBoardRequest createBindBoardRequest(ComboardScanReq request) {
		BindBoardRequest bindBoardRequest = new BindBoardRequest();
		bindBoardRequest.setBarcode(request.getBarCode());
		com.jd.bluedragon.distribution.board.domain.OperatorInfo operatorInfo = new com.jd.bluedragon.distribution.board.domain.OperatorInfo();
		operatorInfo.setSiteCode(request.getCurrentOperate().getSiteCode());
		bindBoardRequest.setOperatorInfo(operatorInfo);
		com.jd.bluedragon.common.dto.base.request.OperatorData originOperatorData = request.getCurrentOperate().getOperatorData();
		com.jd.bluedragon.distribution.api.domain.OperatorData destOperatorData = new com.jd.bluedragon.distribution.api.domain.OperatorData();
		org.springframework.beans.BeanUtils.copyProperties(originOperatorData, destOperatorData);
		bindBoardRequest.setOperatorData(destOperatorData);
		return bindBoardRequest;
	}

	private BindBoardRequest createBindBoardRequest(BoardCombinationRequest request) {
		BindBoardRequest bindBoardRequest = new BindBoardRequest();
		bindBoardRequest.setBarcode(request.getBoxOrPackageCode());
		com.jd.bluedragon.distribution.board.domain.OperatorInfo operatorInfo = new com.jd.bluedragon.distribution.board.domain.OperatorInfo();
		operatorInfo.setSiteCode(request.getSiteCode());
		bindBoardRequest.setOperatorInfo(operatorInfo);
		bindBoardRequest.setOperatorData(request.getOperatorData());
		return bindBoardRequest;
	}

	private BindBoardRequest createBindBoardRequest(ComboardTaskDto request) {
		BindBoardRequest bindBoardRequest = new BindBoardRequest();
		bindBoardRequest.setBarcode(request.getBarCode());
		com.jd.bluedragon.distribution.board.domain.OperatorInfo operatorInfo = new com.jd.bluedragon.distribution.board.domain.OperatorInfo();
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
					for (BoxDto boxDto : boxList) {
						bindBoardRequest.setBarcode(boxDto.getBoxCode());
						bindBoardRequest.setOperateKey(String.valueOf(boxDto.getId()));
						Long operateFlowId = sendBoardCore(bindBoardRequest, boardBoxResult, subTypeEnum);
						map.put(boxDto.getBoxCode(), operateFlowId);
					}
				}
			}
		} catch (Exception e) {
			logger.error("批量发送组板操作流水消息出现异常:request={},boardBoxResult={},subTypeEnum={}",
					JsonHelper.toJson(bindBoardRequest), JsonHelper.toJson(boardBoxResult), subTypeEnum.getCode(), e);
		}
		return map;
	}

}
