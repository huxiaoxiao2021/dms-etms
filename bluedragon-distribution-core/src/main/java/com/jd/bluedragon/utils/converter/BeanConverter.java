package com.jd.bluedragon.utils.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.send.domain.SendM;

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
	public static OperatorData convertToOperatorData(SendM tSendM) {
		if(tSendM.getOperatorData() != null) {
			return tSendM.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(tSendM.getOperatorId());
        operatorData.setOperatorTypeCode(tSendM.getOperatorTypeCode());
        tSendM.setOperatorData(operatorData);
		return operatorData;
	}
	public static OperatorData convertToOperatorData(BoardCommonRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
        request.setOperatorData(operatorData);
		return operatorData;
	}
	public static OperatorData convertToOperatorData(BoardCombinationRequest request) {
		if(request.getOperatorData() != null) {
			return request.getOperatorData();
		}
        OperatorData operatorData = new OperatorData();
        operatorData.setOperatorId(request.getOperatorId());
        operatorData.setOperatorTypeCode(request.getOperatorTypeCode());
        request.setOperatorData(operatorData);
		return operatorData;
	}
}
