package com.jd.bluedragon.utils.converter;

import java.util.Date;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.jy.dto.User;
import com.jd.bluedragon.distribution.receive.domain.ArReceive;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.etms.vos.dto.SealCarDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.board.domain.BindBoardRequest;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionAS;
import com.jd.bluedragon.distribution.jy.dto.comboard.CancelComboardSendTaskDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowData;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizTypeEnum;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;

/**
 * 对象转换工具类
 *
 * @author wuyoude
 * @copyright jd.com 京东物流JDL
 * @time 2023-09-12
 */
public class BeanConverter {
	private static final Logger log = LoggerFactory.getLogger(BeanConverter.class);
	/**
	 * 转换成OperatorData对象,返回值不会为空
	 * @param currentOperate
	 * @return
	 */
	public static OperatorData convertToOperatorData(CurrentOperate currentOperate) {
		if(currentOperate == null) {
			return new OperatorData();
		}
		OperatorData operatorData = toOperatorData(currentOperate.getOperatorData());
    	if(operatorData == null) {
    		operatorData = new OperatorData();
    		operatorData.setOperatorTypeCode(currentOperate.getOperatorTypeCode());
    		operatorData.setOperatorId(currentOperate.getOperatorId());
    	}		
		return operatorData;
	}
	/**
	 * 转换成OperatorData对象,返回值不会为空
	 * @param operatorInfo
	 * @return
	 */
	public static OperatorData convertToOperatorData(OperatorInfo operatorInfo) {
		if(operatorInfo == null) {
			return new OperatorData();
		}
    	OperatorData operatorData = toOperatorData(operatorInfo.getOperatorData());
    	if(operatorData == null) {
    		operatorData = new OperatorData();
    		operatorData.setOperatorTypeCode(operatorInfo.getOperatorTypeCode());
    		operatorData.setOperatorId(operatorInfo.getOperatorId());
    	}		
		return operatorData;
	}
	private static OperatorData toOperatorData(com.jd.bluedragon.common.dto.base.request.OperatorData operatorDataForPda) {
		if(operatorDataForPda == null) {
			return null;
		}
		try {
			OperatorData operatorData = new OperatorData();
            org.apache.commons.beanutils.BeanUtils.copyProperties(operatorData, operatorDataForPda);
            return operatorData;
        } catch (Exception e) {
            log.error("BeanConverter.toOperatorData error!", e);
        }
		return null;
	}
	private static com.jd.bluedragon.common.dto.base.request.OperatorData toPdaOperatorData(
			OperatorData operatorData) {
		if(operatorData == null) {
			return null;
		}
		try {
			com.jd.bluedragon.common.dto.base.request.OperatorData operatorDataForPda = new com.jd.bluedragon.common.dto.base.request.OperatorData();
            org.apache.commons.beanutils.BeanUtils.copyProperties(operatorDataForPda , operatorData);
            return operatorDataForPda;
        } catch (Exception e) {
            log.error("BeanConverter.toOperatorData error!", e);
        }
		return null;
	}	
	public static OperatorData convertToOperatorData(InspectionRequest requestBean) {
		if(requestBean.getOperatorData() != null) {
			return requestBean.getOperatorData();
		}
		OperatorData operatorData = null;
		if(StringUtils.isNotBlank(requestBean.getOperatorDataJson())) {
			operatorData = JsonHelper.fromJson(requestBean.getOperatorDataJson(), OperatorData.class);
		}
		if(operatorData == null) {
			operatorData = new OperatorData();
	        operatorData.setOperatorId(requestBean.getOperatorId());
	        operatorData.setOperatorTypeCode(requestBean.getOperatorTypeCode());
		}
		return operatorData;
	}
	public static OperatorData convertToOperatorDataForReceive(ReceiveRequest requestBean) {
		if(requestBean.getOperatorData() != null) {
			return requestBean.getOperatorData();
		}
		OperatorData operatorData = null;
		if(StringUtils.isNotBlank(requestBean.getOperatorDataJson())) {
			operatorData = JsonHelper.fromJson(requestBean.getOperatorDataJson(), OperatorData.class);
		}
		if(operatorData == null) {
			operatorData = new OperatorData();
			operatorData.setOperatorId(requestBean.getOperatorId());
			operatorData.setOperatorTypeCode(requestBean.getOperatorTypeCode());
		}
		return operatorData;
	}



	public static OperatorData convertToOperatorDataForAuto(InspectionRequest requestBean) {
		return convertToOperatorDataForAuto(requestBean.getMachineCode());
	}

	public static OperatorData convertToOperatorDataForAuto(String machineCode) {
		OperatorData operatorData = new OperatorData();
		operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
		operatorData.setOperatorId(machineCode);
		operatorData.setMachineCode(machineCode);
		return operatorData;
	}


	public static OperatorData convertToOperatorData(InspectionAS inspectionAs) {
		if(inspectionAs.getOperatorData() != null) {
			return inspectionAs.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(inspectionAs.getOperatorId());
        operatorData.setOperatorTypeCode(inspectionAs.getOperatorTypeCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorDataForAuto(InspectionAS inspectionAs) {
		if(inspectionAs.getOperatorData() != null) {
			return inspectionAs.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
        operatorData.setOperatorId(inspectionAs.getMachineCode());
		operatorData.setMachineCode(inspectionAs.getMachineCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorData(CenConfirm cenConfirm) {
		if(cenConfirm.getOperatorData() != null) {
			return cenConfirm.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(cenConfirm.getOperatorId());
        operatorData.setOperatorTypeCode(cenConfirm.getOperatorTypeCode());
		return operatorData;
	}	
	public static OperatorData convertToOperatorData(SortingRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorData(Sorting request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
		operatorData.setBizSource(request.getBizSource());
		return operatorData;
	}	
	public static OperatorData convertToOperatorData(SendM tSendM) {
		if(tSendM.getOperatorData() != null) {
			return tSendM.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(tSendM.getOperatorId());
        operatorData.setOperatorTypeCode(tSendM.getOperatorTypeCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorData(SendDetail tSendDetail) {
		if(tSendDetail.getOperatorData() != null) {
			return tSendDetail.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(tSendDetail.getOperatorId());
        operatorData.setOperatorTypeCode(tSendDetail.getOperatorTypeCode());
		operatorData.setBizSource(tSendDetail.getBizSource());
		return operatorData;
	}
	public static OperatorData convertToOperatorDataForAuto(BindBoardRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
		OperatorData operatorData = new OperatorData();
		operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
		operatorData.setOperatorId(request.getMachineCode());
		operatorData.setMachineCode(request.getMachineCode());
		return operatorData;
	}	
	public static OperatorData convertToOperatorData(BoardCommonRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorData(BoardCombinationRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorData(CancelComboardSendTaskDto request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
		return operatorData;
	}	
	public static OperatorData convertToOperatorData(Task task) {
		if(task.getOperatorData() != null) {
			return task.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(task.getOperatorId());
        operatorData.setOperatorTypeCode(task.getOperatorTypeCode());
		return operatorData;
	}
	public static OperatorData convertToOperatorData(PackageSendRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
		return operatorData;
	}	
	public static OperatorData convertToOperatorDataForAuto(AutoSortingPackageDto dto) {
		if(dto.getOperatorData() != null) {
			return dto.getOperatorData();
		}
		OperatorData operatorData = new OperatorData();
		operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
		operatorData.setOperatorId(dto.getMachineCode());
		operatorData.setMachineCode(dto.getMachineCode());
		return operatorData;
	}
	public static com.jd.bluedragon.common.dto.base.request.OperatorData convertToPdaOperatorDataForAuto(
			BindBoardRequest request) {
		if(request.getOperatorData() != null) {
			return toPdaOperatorData(request.getOperatorData());
		}
		com.jd.bluedragon.common.dto.base.request.OperatorData operatorData = new com.jd.bluedragon.common.dto.base.request.OperatorData();
		operatorData.setOperatorTypeCode(OperatorTypeEnum.AUTO_MACHINE.getCode());
		operatorData.setOperatorId(request.getMachineCode());
		operatorData.setMachineCode(request.getMachineCode());
		return operatorData;
	}
	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(Inspection inspection) {
		if(inspection == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(inspection.getPackageBarcode());
		mqData.setOperateBizType(OperateBizTypeEnum.INSPECTION.getCode());
		mqData.setOperateKey(StringHelper.getStringValue(inspection.getInspectionId()));
		mqData.setOperateTime(inspection.getOperateTime());
		mqData.setOperateSiteCode(inspection.getCreateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		data.setOperatorData(inspection.getOperatorData());
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("inspection-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}

	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(Receive receive) {
		if (receive == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(receive.getBoxCode());
		mqData.setOperateBizType(OperateBizTypeEnum.RECEIVE.getCode());
		mqData.setOperateKey(StringHelper.getStringValue(receive.getReceiveId()));
		mqData.setOperateTime(receive.getUpdateTime());
		mqData.setOperateSiteCode(receive.getCreateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		data.setOperatorData(receive.getOperatorData());
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("receive-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}

	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(ArReceive arReceive) {
		if (arReceive == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(arReceive.getBoxCode());
		mqData.setOperateBizType(OperateBizTypeEnum.AR_RECEIVE.getCode());
		mqData.setOperateKey(StringHelper.getStringValue(arReceive.getReceiveId()));
		mqData.setOperateTime(arReceive.getUpdateTime());
		mqData.setOperateSiteCode(arReceive.getCreateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		data.setOperatorData(arReceive.getOperatorData());
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("arReceive-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}

	public static JyOperateFlowMqData convertToUnsealJyOperateFlowMqData(SealCarDto sealCarDto) {
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(sealCarDto.getSealCarCode());
		mqData.setOperateBizType(OperateBizTypeEnum.UNSEAL.getCode());
		mqData.setOperateKey(sealCarDto.getSealCarCode());
		mqData.setOperateTime(sealCarDto.getDesealCarTime());
		mqData.setOperateSiteCode(sealCarDto.getDesealSiteId());
		JyOperateFlowData data = new JyOperateFlowData();
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("unseal-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}

	public static JyOperateFlowMqData convertToSealJyOperateFlowMqData(SealCarDto sealCarDto) {
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(sealCarDto.getSealCarCode());
		mqData.setOperateBizType(OperateBizTypeEnum.SEAL.getCode());
		mqData.setOperateKey(sealCarDto.getSealCarCode());
		mqData.setOperateTime(sealCarDto.getSealCarTime());
		mqData.setOperateSiteCode(sealCarDto.getSealSiteId());
		JyOperateFlowData data = new JyOperateFlowData();
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("seal-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}

	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(SendDetail tSendDetail) {
		if(tSendDetail == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(tSendDetail.getPackageBarcode());
		mqData.setOperateBizType(OperateBizTypeEnum.SEND_D.getCode());
		mqData.setOperateKey(StringHelper.getStringValue(tSendDetail.getSendDId()));
		mqData.setOperateTime(tSendDetail.getOperateTime());
		mqData.setOperateSiteCode(tSendDetail.getCreateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		OperatorData operatorData = tSendDetail.getOperatorData();
		if (operatorData != null && tSendDetail.getBizSource() != null) {
			operatorData.setBizSource(tSendDetail.getBizSource());
		}
		data.setOperatorData(operatorData);
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("tSendDetail-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}
	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(Sorting sorting) {
		if(sorting == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(sorting.getPackageCode());
		mqData.setOperateBizType(OperateBizTypeEnum.SORTING.getCode());
		mqData.setOperateKey(StringHelper.getStringValue(sorting.getId()));
		mqData.setOperateTime(sorting.getOperateTime());
		mqData.setOperateSiteCode(sorting.getCreateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		OperatorData operatorData = sorting.getOperatorData();
		if (operatorData != null && sorting.getBizSource() != null) {
			operatorData.setBizSource(sorting.getBizSource());
		}
		data.setOperatorData(operatorData);
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("soring-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(BoardCommonRequest request) {
		if(request == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(request.getBarCode());
		mqData.setOperateBizType(OperateBizTypeEnum.BOARD.getCode());
		mqData.setOperateKey(request.getBarCode());
		if(request.getOperateTime() != null) {
			mqData.setOperateTime(new Date(request.getOperateTime()));
		}else {
			mqData.setOperateTime(new Date());
		}
		mqData.setOperateSiteCode(request.getOperateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		data.setOperatorData(request.getOperatorData());
		mqData.setJyOperateFlowData(data);
		return mqData;
	}
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(BindBoardRequest request) {
		if(request == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(request.getBarcode());
		mqData.setOperateBizType(OperateBizTypeEnum.BOARD.getCode());
		mqData.setOperateKey(request.getOperateKey());
		if(request.getOperatorInfo() != null && request.getOperatorInfo().getOperateTime() != null) {
			mqData.setOperateTime(request.getOperatorInfo().getOperateTime());
		}else {
			mqData.setOperateTime(new Date());
		}
		mqData.setOperateSiteCode(request.getOperatorInfo().getSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		data.setOperatorData(request.getOperatorData());
		mqData.setJyOperateFlowData(data);
		return mqData;
	}
	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(AbnormalWayBill abnormalWayBill) {
		if (abnormalWayBill == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		if (StringUtils.isBlank(abnormalWayBill.getPackageCode())) {
			mqData.setOperateBizKey(abnormalWayBill.getWaybillCode());
		} else {
			mqData.setOperateBizKey(abnormalWayBill.getPackageCode());
		}
		mqData.setOperateBizType(OperateBizTypeEnum.ABNORMAL_DELIVERY.getCode());
		mqData.setOperateKey(StringHelper.getStringValue(abnormalWayBill.getId()));
		mqData.setOperateTime(abnormalWayBill.getOperateTime());
		mqData.setOperateSiteCode(abnormalWayBill.getCreateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		data.setOperatorData(abnormalWayBill.getOperatorData());
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("abnormalWayBill-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}
	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowMqData convertToJyOperateFlowMqData(WeightVolumeEntity entity) {
		if (entity == null) {
			return null;
		}
		JyOperateFlowMqData mqData = new JyOperateFlowMqData();
		mqData.setOperateBizKey(entity.getBarCode());
		mqData.setOperateBizType(OperateBizTypeEnum.WEIGHT_VOLUME.getCode());
		mqData.setOperateKey(entity.getBarCode());
		mqData.setOperateTime(entity.getOperateTime() == null ? new Date() : entity.getOperateTime());
		mqData.setOperateSiteCode(entity.getOperateSiteCode());
		JyOperateFlowData data = new JyOperateFlowData();
		OperatorData operatorData = entity.getOperatorData();
		if (operatorData == null || operatorData.getOperatorTypeCode() == null) {
			if (StringUtils.isNotBlank(entity.getMachineCode())) {
				operatorData = convertToOperatorDataForAuto(entity.getMachineCode());
			}
		}
		data.setOperatorData(operatorData);
		mqData.setJyOperateFlowData(data);
		if(log.isDebugEnabled()) {
			log.debug("WeightVolumeEntity-convertToJyOperateFlowMqData:{}",JsonHelper.toJson(mqData));
		}
		return mqData;
	}
	/**
	 * 对象转换为数据库实体
	 * @param jyOperateFlow
	 * @return
	 */
	public static JyOperateFlowDto convertToJyOperateFlowDto(JyOperateFlowMqData jyOperateFlow) {
		if(jyOperateFlow == null) {
			return null;
		}
		JyOperateFlowDto dto = new JyOperateFlowDto();
		try {
			org.apache.commons.beanutils.BeanUtils.copyProperties(dto , jyOperateFlow);
			if (Constants.LONG_ZERO.equals(dto.getId())) {
				dto.setId(null);
			}
			if (log.isInfoEnabled()) {
				log.info("BeanConverter:转换jyOperateFlowDto={}", JsonHelper.toJson(dto));
			}
			dto.setOperateValue(JsonHelper.toJson(jyOperateFlow.getJyOperateFlowData()));
			if (dto.getOperateBizKey() == null) {
				dto.setOperateBizKey(Constants.EMPTY_FILL);
			}
			if (dto.getOperateKey() == null) {
				dto.setOperateKey(Constants.EMPTY_FILL);
			}
			if (dto.getOperateValue() == null) {
				dto.setOperateValue(Constants.EMPTY_FILL);
			}
		} catch (Exception e) {
			log.error("BeanConverter.convertToJyOperateFlowDto error!", e);
		}

		return dto;
	}

	/**
	 * 用户数据转换
	 *
	 * @param userRaw
	 * @return
	 */
	public static User convertToSdkUser(com.jd.bluedragon.common.dto.base.request.User userRaw) {
		if (userRaw == null) {
			return null;
		}
		User user = new User();
		try {
			org.apache.commons.beanutils.BeanUtils.copyProperties(user, userRaw);
		} catch (Exception e) {
			log.error("BeanConverter.convertToSdkUser error!", e);
		}
		return user;
	}

	/**
	 * 用户操作场地数据转换
	 *
	 * @param currentOperateRaw
	 * @return
	 */
	public static com.jd.bluedragon.distribution.jy.dto.CurrentOperate convertToSdkCurrentOperate(com.jd.bluedragon.common.dto.base.request.CurrentOperate currentOperateRaw) {
		if (currentOperateRaw == null) {
			return null;
		}
		com.jd.bluedragon.distribution.jy.dto.CurrentOperate currentOperate = new com.jd.bluedragon.distribution.jy.dto.CurrentOperate();
		try {
			org.apache.commons.beanutils.BeanUtils.copyProperties(currentOperate, currentOperateRaw);
		} catch (Exception e) {
			log.error("BeanConverter.convertToSdkCurrentOperate error!", e);
		}
		return currentOperate;
	}

}
