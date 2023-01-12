package com.jd.bluedragon.core.jsf.eclp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.third.jsf.ThirdJsfInterface;
import com.jd.etms.third.service.domain.ReceiptStateParameter;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 
 * @ClassName: ThirdJsfInterfaceManagerImpl
 * @Description: 调用3pl三方状态回传
 * @author: wuyoude
 * @date: 2022年12月1日 下午2:37:26
 *
 */
@Service("thirdJsfInterfaceManager")
public class ThirdJsfInterfaceManagerImpl implements ThirdJsfInterfaceManager{
    private static final Logger log = LoggerFactory.getLogger(ThirdJsfInterfaceManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.3pl.thirdJsfInterface.";
    
    @Autowired
    @Qualifier("thirdJsfInterface")
    private ThirdJsfInterface thirdJsfInterface;
    
	@Override
	public JdResult<List<ReceiptStateParameter>> partnerReceiptState(List<ReceiptStateParameter> list){
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "partnerReceiptState");
		JdResult<List<ReceiptStateParameter>> result = new JdResult<List<ReceiptStateParameter>>();
		try {
			if(log.isInfoEnabled()){
				log.info("三方状态回传3pl-request："+JsonHelper.toJson(list));
			}
			List<ReceiptStateParameter>  rpcResult = thirdJsfInterface.partnerReceiptState(list);
			if(log.isInfoEnabled()){
				log.info("三方状态回传3pl-response："+JsonHelper.toJson(rpcResult));
			}
			if(rpcResult == null
					|| CollectionUtils.isEmpty(rpcResult)){
				result.toSuccess();
			}else{
				log.warn("三方状态回传3pl接口失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("三方状态回传3pl接口失败！");
				result.setData(rpcResult);
			}
		} catch (Exception e) {
			log.error("三方状态回传3pl接口异常！",e);
			result.toError("三方状态回传3pl接口异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
}
