package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ka.edn.sdk.common.SdkRpcResult;
import com.jd.ka.edn.sdk.dto.DeliveryReceiptResDto;
import com.jd.ka.edn.sdk.enums.ReturnCodeEnum;
import com.jd.ka.edn.sdk.service.JpEdnDeliveryReceiptService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 金鹏相关服务管理类
 * @author wuyoude
 *
 */
@Service("ednServiceManager")
public class EdnServiceManagerImpl implements EdnServiceManager{
	private static final Logger log = LoggerFactory.getLogger(EdnServiceManagerImpl.class);
	private static final String UMP_KEY_batchGetDeliveryReceipt = "dmsWeb.rpc.client.edn.jpEdnDeliveryReceiptService.batchGetDeliveryReceipt";
	@Autowired
	JpEdnDeliveryReceiptService jpEdnDeliveryReceiptService;
	/**
	 * 
	 * @param ednBatchNumList
	 * @return
	 */
	public JdResult<List<DmsEdnBatchVo>> batchGetDeliveryReceipt(List<String> ednBatchNumList){
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_batchGetDeliveryReceipt);
		JdResult<List<DmsEdnBatchVo>> result = new JdResult<List<DmsEdnBatchVo>>();
		try {
			SdkRpcResult<List<DeliveryReceiptResDto>> rpcResult = jpEdnDeliveryReceiptService.batchGetDeliveryReceipt(ednBatchNumList);
			if(rpcResult != null
					&& rpcResult.isSuccess() 
					&& ReturnCodeEnum.SUCCESS.getCode().equals(rpcResult.getResultCode())){
				if(rpcResult.getResult() != null && !rpcResult.getResult().isEmpty()){
					List<DmsEdnBatchVo> dataList = new ArrayList<DmsEdnBatchVo>();
					result.setData(dataList);
					result.toSuccess();
					for(DeliveryReceiptResDto rpcObj:rpcResult.getResult()){
						DmsEdnBatchVo dataItem = new DmsEdnBatchVo();
						dataItem.setEdnBatchNum(rpcObj.getEdnBatchNum());
						dataItem.setDeliveryReceiptUrl(rpcObj.getDeliveryReceiptUrl());
						dataList.add(dataItem);
					}
				}
			}else{
				log.warn("调用金鹏接口批量获取配送单失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用金鹏接口批量获取配送单失败！");
			}
		} catch (Exception e) {
			log.error("调用金鹏接口批量获取配送单异常！",e);
			result.toError("调用金鹏接口批量获取配送单异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
}
