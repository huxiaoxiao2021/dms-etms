package com.jd.bluedragon.core.jsf.tms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.SealVehicleJobDto;
import com.jd.etms.vos.ws.VosVehicleJobQueryWS;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 
 * @ClassName: TmsServiceManagerImpl
 * @Description: 调用tms-jsf接口实现
 * @author: wuyoude
 * @date: 2021年01月14日 下午2:37:26
 *
 */
@Service("vosVehicleJobQueryWSManager")
public class VosVehicleJobQueryWSManagerImpl implements VosVehicleJobQueryWSManager{
    private static final Logger log = LoggerFactory.getLogger(VosVehicleJobQueryWSManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.tms.vosVehicleJobQueryWS.";
    
    @Autowired
    @Qualifier("vosVehicleJobQueryWS")
    private VosVehicleJobQueryWS vosVehicleJobQueryWS;
	/**
	 * 根据任务编码查询任务信息
	 * @param vehicleJobCode
	 * @return
	 */
	@Override
	public JdResult<SealVehicleJobDto> getSealVehicleJobByVehicleJobCode(String vehicleJobCode) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "getSealVehicleJobByVehicleJobCode");
    	JdResult<SealVehicleJobDto> result = new JdResult<SealVehicleJobDto>();
    	try {
    		CommonDto<SealVehicleJobDto> rest = vosVehicleJobQueryWS.getSealVehicleJobByVehicleJobCode(vehicleJobCode);
	        if(null != rest 
	        		&& Constants.RESULT_SUCCESS == rest.getCode()){
	            result.setData(rest.getData());
	            result.toSuccess(rest.getMessage());
	            if(rest.getData() == null) {
	            	log.warn("调用tms查询任务信息返回对象为空！vehicleJobCode={},返回值：{}",vehicleJobCode,JsonHelper.toJson(rest));
	            }
	        }else {
				log.warn("调用tms查询任务信息失败！vehicleJobCode={},返回值：{}",vehicleJobCode,JsonHelper.toJson(rest));
				result.toFail("调用tms查询任务信息失败！");
	        }
		} catch (Exception e) {
			log.error("调用tms查询任务信息异常！vehicleJobCode={}",e,vehicleJobCode);
			result.toError("调用tms查询任务信息异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
        return result;
	}
}
