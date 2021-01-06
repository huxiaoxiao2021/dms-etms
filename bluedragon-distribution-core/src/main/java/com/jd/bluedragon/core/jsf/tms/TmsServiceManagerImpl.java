package com.jd.bluedragon.core.jsf.tms;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vts.dto.VtsTransportResourceDto;
import com.jd.etms.vts.ws.VtsQueryWS;
import com.jd.etms.vts.dto.PageDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.beans.BeanUtils;

/**
 * 
 * @ClassName: EclpImportServiceManagerImpl
 * @Description: 调用eclp站内信jsf接口实现
 * @author: wuyoude
 * @date: 2021年01月14日 下午2:37:26
 *
 */
@Service("tmsServiceManager")
public class TmsServiceManagerImpl implements TmsServiceManager{
    private static final Logger log = LoggerFactory.getLogger(TmsServiceManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.tms.vtsQueryWS.";
    
    @Autowired
    @Qualifier("vtsQueryWS")
    private VtsQueryWS vtsQueryWS;
    
	@Override
	public JdResult<TransportResource> getTransportResourceByTransCode(String transCode) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "getTransportResourceByTransCode");
    	JdResult<TransportResource> result = new JdResult<TransportResource>();
    	try {
    		com.jd.etms.vts.dto.CommonDto<VtsTransportResourceDto> rest = vtsQueryWS.getTransportResourceByTransCode(transCode);
	        if(null != rest 
	        		&& Constants.RESULT_SUCCESS == rest.getCode()
	        		&& rest.getData() != null){
	            result.setData(convertBean(rest.getData()));
	            result.toSuccess();
	        }else {
				log.warn("调用tms加载运力信息失败！transCode={0},返回值：{1}",transCode,JsonHelper.toJson(rest));
				result.toFail("调用tms加载运力信息失败！");
	        }
		} catch (Exception e) {
			log.error("调用tms加载运力信息异常！transCode={0}",e,transCode);
			result.toError("调用tms加载运力信息异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
        return result;
	}
    /**
     * 调用运输系统获取运力资源
     * @param createSiteCode        始发站点
     * @param receiveSiteCode       结束站点
     * @return
     */
    @Override
    public JdResult<List<TransportResource>> loadTransportResources(Integer createSiteCode, Integer receiveSiteCode) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "getTransportResourceByPage");
    	JdResult<List<TransportResource>> result = new JdResult<List<TransportResource>>();
    	try {
	        VtsTransportResourceDto parameter=new VtsTransportResourceDto();
	        PageDto<VtsTransportResourceDto> page = new PageDto<VtsTransportResourceDto>();
	        page.setPageSize(5);
	        parameter.setStartNodeId(createSiteCode);
	        parameter.setEndNodeId(receiveSiteCode);
	        PageDto<VtsTransportResourceDto> rest = vtsQueryWS.getTransportResourceByPage(parameter,page);
	        if(null != rest){
	        	List<TransportResource> listData=new ArrayList<TransportResource>();
	        	List<VtsTransportResourceDto> list = rest.getResult();
	            for (VtsTransportResourceDto item:list){
	                listData.add(convertBean(item));
	            }
	            result.setData(listData);
	            result.toSuccess();
	        }else {
				log.warn("调用tms加载运力信息为空！{0}-{1}",createSiteCode,receiveSiteCode);
				result.toFail("调用tms加载运力信息为空！");
	        }
		} catch (Exception e) {
			log.error("调用tms加载运力信息异常！",e);
			result.toError("调用tms加载运力信息异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
        return result;
    }
    /**
     * bean转换
     * @param tmsBean
     * @return
     */
    private TransportResource convertBean(VtsTransportResourceDto tmsBean) {
    	if(tmsBean != null) {
        	TransportResource resource=new TransportResource();
        	BeanUtils.copyProperties(tmsBean,resource);
        	return resource;
    	}
    	return null;
    }
}
