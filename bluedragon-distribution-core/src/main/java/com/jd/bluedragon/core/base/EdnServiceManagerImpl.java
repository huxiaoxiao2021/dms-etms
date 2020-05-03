package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ka.edn.sdk.common.SdkRpcResult;
import com.jd.ka.edn.sdk.dto.DeliveryReceiptResDto;
import com.jd.ka.edn.sdk.enums.ReturnCodeEnum;
import com.jd.ka.edn.sdk.service.JpEdnDeliveryReceiptService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 金鹏相关服务管理类
 * @author wuyoude
 *
 */
@Service("ednServiceManager")
public class EdnServiceManagerImpl implements EdnServiceManager{
	private static final Logger log = LoggerFactory.getLogger(MrdFeedbackManagerImpl.class);
	
	@Autowired
	JpEdnDeliveryReceiptService jpEdnDeliveryReceiptService;
	/**
	 * 
	 * @param ednBatchNumList
	 * @return
	 */
    @JProfiler(jKey = "dmsWeb.rpc.client.edn.jpEdnDeliveryReceiptService.batchGetDeliveryReceipt",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<List<DmsEdnBatchVo>> batchGetDeliveryReceipt(List<String> ednBatchNumList){
		JdResult<List<DmsEdnBatchVo>> result = new JdResult<List<DmsEdnBatchVo>>();
		SdkRpcResult<List<DeliveryReceiptResDto>> rpcResult = jpEdnDeliveryReceiptService.batchGetDeliveryReceipt(ednBatchNumList);
		if(log.isDebugEnabled()){
			log.debug("调用金鹏接口失败！return:{}",JsonHelper.toJson(rpcResult));
		}
		if(rpcResult != null 
				&& rpcResult.isSuccess() 
				&& ReturnCodeEnum.SUCCESS.getCode().equals(rpcResult.getResultCode())){
			if(rpcResult.getResult() != null && !rpcResult.getResult().isEmpty()){
				List<DmsEdnBatchVo> dataList = new ArrayList<DmsEdnBatchVo>();
				result.setData(dataList);
				for(DeliveryReceiptResDto rpcObj:rpcResult.getResult()){
					DmsEdnBatchVo dataItem = new DmsEdnBatchVo();
					dataItem.setEdnBatchNum(rpcObj.getEdnBatchNum());
					dataItem.setDeliveryReceiptUrl(rpcObj.getDeliveryReceiptUrl());
					dataList.add(dataItem);
				}
			}
		}else if(rpcResult != null){
			log.warn("调用金鹏接口失败！return:{}",JsonHelper.toJson(rpcResult));
			result.toFail(rpcResult.getResultCode()+ rpcResult.getResultMessage());
		}else{
			result.toFail("调用金鹏接口失败！");
		}
		return result;
	}
}
