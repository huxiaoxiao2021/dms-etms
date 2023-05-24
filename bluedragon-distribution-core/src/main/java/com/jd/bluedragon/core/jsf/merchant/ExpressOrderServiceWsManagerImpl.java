package com.jd.bluedragon.core.jsf.merchant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.merchant.sdk.common.dto.ResultDTO;
import com.jd.merchant.sdk.order.dto.UpdateOrderRequest;
import com.jd.merchant.sdk.order.dto.UpdateOrderResultDto;
import com.jd.merchant.sdk.order.ws.ExpressOrderServiceWs;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 
 * @ClassName: ExpressOrderServiceWsManager
 * @Description: 快运订单修改接口-服务管理-jsf接口实现
 * @author: wuyoude
 * @date: 2023年05月05日 下午2:37:26
 *
 */
@Service("expressOrderServiceWsManager")
public class ExpressOrderServiceWsManagerImpl implements ExpressOrderServiceWsManager{
    private static final Logger log = LoggerFactory.getLogger(ExpressOrderServiceWsManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.merchant.expressOrderServiceWs.";
    
    @Autowired
    @Qualifier("expressOrderServiceWs")
    private ExpressOrderServiceWs expressOrderServiceWs;
    
	@Override
	public JdResult<Boolean> updateOrderSelective(UpdateOrderRequest dto) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "updateOrderSelective");
		JdResult<Boolean> result = new JdResult<Boolean>();
		result.setData(Boolean.FALSE);
		try {
			if(log.isInfoEnabled()){
				log.info("修改订单-超长超重服务信息req："+JsonHelper.toJson(dto));
			}
			ResultDTO<UpdateOrderResultDto>  rpcResult = expressOrderServiceWs.updateOrderSelective(dto);
			if(log.isInfoEnabled()){
				log.info("修改订单-超长超重服务信息resp："+JsonHelper.toJson(rpcResult));
			}
			if(rpcResult != null
					&& ResultDTO.SUCCESS_CODE.equals(rpcResult.getStatusCode())){
				result.setData(Boolean.TRUE);
				result.toSuccess();
			}else{
				log.warn("修改订单-超长超重服务信息失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("修改订单-超长超重服务信息失败！");
			}
		} catch (Exception e) {
			log.error("修改订单-超长超重服务信息异常！",e);
			result.toError("修改订单-超长超重服务信息异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
}
