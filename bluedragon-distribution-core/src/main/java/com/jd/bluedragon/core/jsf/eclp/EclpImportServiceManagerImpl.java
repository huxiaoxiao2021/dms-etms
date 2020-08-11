package com.jd.bluedragon.core.jsf.eclp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.eclp.bbp.notice.domain.dto.BatchImportDTO;
import com.jd.eclp.bbp.notice.open.ImportService;
import com.jd.eclp.core.ApiResponse;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 
 * @ClassName: EclpImportServiceManagerImpl
 * @Description: 调用eclp站内信jsf接口实现
 * @author: wuyoude
 * @date: 2020年07月29日 下午2:37:26
 *
 */
@Service("eclpImportServiceManager")
public class EclpImportServiceManagerImpl implements EclpImportServiceManager{
    private static final Logger log = LoggerFactory.getLogger(EclpImportServiceManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.eclp.importService.";
    
    @Autowired
    @Qualifier("importService")
    private ImportService importService;
    
	@Override
	public JdResult<Boolean> batchImport(BatchImportDTO dto) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "batchImport");
		JdResult<Boolean> result = new JdResult<Boolean>();
		result.setData(Boolean.FALSE);
		try {
			if(log.isInfoEnabled()){
				log.info("发送站内信："+JsonHelper.toJson(dto));
			}
			ApiResponse  rpcResult = importService.batchImport(dto);
			if(rpcResult != null
					&& rpcResult.isSuccess()){
				result.setData(Boolean.TRUE);
				result.toSuccess();
			}else{
				log.warn("调用eclp通用站内信创建接口失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用eclp通用站内信创建接口失败！");
			}
		} catch (Exception e) {
			log.error("调用eclp通用站内信创建接口异常！",e);
			result.toError("调用eclp通用站内信创建接口异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
}
