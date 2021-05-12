package com.jd.bluedragon.core.jsf.tms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CarrierQueryWSManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.BasicSelectWS;
import com.jd.tms.tfc.dto.TransWorkItemWsDto;
import com.jd.tms.tfc.ws.TfcSelectWS;
import com.jd.tms.workbench.api.PdaSorterApi;
import com.jd.tms.workbench.dto.TransWorkItemDto;
import com.jd.tms.workbench.dto.TransWorkItemSimpleDto;
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
@Service("tmsServiceManager")
public class TmsServiceManagerImpl implements TmsServiceManager{
    private static final Logger log = LoggerFactory.getLogger(TmsServiceManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.tms.TmsServiceManagerImpl.";
    private static final String UMP_KEY_PREFIX_PDASORTERAPI = "dmsWeb.jsf.client.tms.pdaSorterApi.";
    private static final String UMP_KEY_PREFIX_TFCSELECTWS = "dmsWeb.jsf.client.tms.tfcSelectWS.";
    
    
    @Autowired
    @Qualifier("carrierQueryWSManger")
    private CarrierQueryWSManager carrierQueryWSManager;
    
    @Autowired
    private BasicSelectWS basicSelectWs;
    
    @Autowired
    private PdaSorterApi pdaSorterApi;
    
	@Autowired
	private TfcSelectWS tfcSelectWS;
	
	@Autowired
	UccPropertyConfiguration uccPropertyConfiguration;
    
	@Override
	public JdResult<TransportResource> getTransportResourceByTransCode(String transCode) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "getTransportResourceByTransCode");
    	JdResult<TransportResource> result = new JdResult<TransportResource>();
    	try {
    		CommonDto<TransportResourceDto> rest = carrierQueryWSManager.getTransportResourceByTransCode(transCode);
	        if(null != rest 
	        		&& Constants.RESULT_SUCCESS == rest.getCode()
	        		&& rest.getData() != null){
	            result.setData(convertBean(rest.getData()));
	            result.toSuccess();
	        }else {
				log.warn("调用tms加载运力信息失败！transCode={},返回值：{}",transCode,JsonHelper.toJson(rest));
				result.toFail("调用tms加载运力信息失败！");
	        }
		} catch (Exception e) {
			log.error("调用tms加载运力信息异常！transCode={}",e,transCode);
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
     * @param filterTransTypes      过滤运力类型
     * @return
     */
    @Override
    public JdResult<List<TransportResource>> loadTransportResources(String startNodeCode, String endNodeCode, List<Integer> filterTransTypes) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "getTransportResourceByPage");
    	JdResult<List<TransportResource>> result = new JdResult<List<TransportResource>>();
    	try {
    		if(startNodeCode == null || endNodeCode == null) {
    			result.toFail("传入参数startNodeCode、endNodeCode不能为空！");
    			return result;
    		}
    		TransportResourceDto parameter=new TransportResourceDto();
    		PageDto<TransportResourceDto> page = new PageDto<TransportResourceDto>();
    		page.setCurrentPage(1);
    		page.setPageSize(20);
	        parameter.setStartNodeCode(startNodeCode);
	        parameter.setEndNodeCode(endNodeCode);
	        CommonDto<PageDto<TransportResourceDto>> rest = basicSelectWs.queryPageTransportResource(page,parameter);
	        if(log.isDebugEnabled()) {
	        	log.debug("查询运输接口getTransportResourceByPage，入参：{},{} 返回结果：{}", startNodeCode,endNodeCode,JsonHelper.toJson(rest));
	        }
	        if(null != rest 
	        		&& rest.getData() != null 
	        		&& rest.getData().getResult() != null){
	        	List<TransportResource> listData=new ArrayList<TransportResource>();
	        	List<TransportResourceDto> list = rest.getData().getResult();
	            for (TransportResourceDto item:list){
	            	//过滤运力类型
	            	if(CollectionUtils.isNotEmpty(filterTransTypes)) {
	            		if(filterTransTypes.contains(item.getTransType())) {
	            			listData.add(convertBean(item));
	            		}
	            	}else {
	            		listData.add(convertBean(item));
	            	}
	            }
	            result.setData(listData);
	            result.toSuccess();
	        }else {
				log.warn("调用tms加载运力信息为空！{}-{}",startNodeCode,endNodeCode);
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
    private TransportResource convertBean(TransportResourceDto tmsBean) {
    	if(tmsBean != null) {
        	TransportResource resource=new TransportResource();
        	BeanUtils.copyProperties(tmsBean,resource);
        	return resource;
    	}
    	return null;
    }
    /**
     * 调用tms封车校验
     * @param transWorkItemSimpleDto
     * @return
     */
    public JdResult<TransWorkItemDto> getTransWorkItemAndCheckParam(TransWorkItemSimpleDto transWorkItemSimpleDto){
    	//开关控制调用新接口
    	if(!BusinessHelper.isTrue(uccPropertyConfiguration.getUsePdaSorterApi())) {
    		return this.getVehicleNumberOrItemCodeByParam(transWorkItemSimpleDto);
    	}
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX_PDASORTERAPI + "getTransWorkItemAndCheckParam");
    	JdResult<TransWorkItemDto> result = new JdResult<TransWorkItemDto>();
    	try {
    		com.jd.tms.workbench.dto.CommonDto<TransWorkItemDto> rest = pdaSorterApi.getTransWorkItemAndCheckParam(transWorkItemSimpleDto);
	        if(null != rest 
	        		&& rest.isSuccess()
	        		&& rest.getData() != null){
	            result.setData(rest.getData());
	            result.toSuccess(rest.getCode(),rest.getMessage());
	        }else if(null != rest){
				log.warn("调用tms封车校验失败！request={},返回值：{}",JsonHelper.toJson(transWorkItemSimpleDto),JsonHelper.toJson(rest));
				result.toFail(rest.getCode(),rest.getMessage());
	        }else {
				log.warn("调用tms封车校验返回值为空！request={},返回值：{}",JsonHelper.toJson(transWorkItemSimpleDto),JsonHelper.toJson(rest));
				result.toFail("调用tms封车校验返回值为空！");
	        }
		} catch (Exception e) {
			log.error("调用tms封车校验异常！request={}",e,JsonHelper.toJson(transWorkItemSimpleDto));
			result.toError("调用tms封车校验异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
        return result;
    }
    /**
     * 旧接口
     * @param transWorkItemSimpleDto
     * @return
     */
    private JdResult<TransWorkItemDto> getVehicleNumberOrItemCodeByParam(TransWorkItemSimpleDto transWorkItemSimpleDto){
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX_TFCSELECTWS + "getVehicleNumberOrItemCodeByParam");
    	JdResult<TransWorkItemDto> result = new JdResult<TransWorkItemDto>();
    	try {
    		com.jd.tms.tfc.dto.CommonDto<TransWorkItemWsDto> rest = tfcSelectWS.getVehicleNumberOrItemCodeByParam(toTransWorkItemSimpleDto(transWorkItemSimpleDto));
	        if(null != rest 
	        		&& Constants.RESULT_SUCCESS == rest.getCode()
	        		&& rest.getData() != null){
	            result.setData(toTransWorkItemDto(rest.getData()));
	            result.toSuccess(rest.getCode(),rest.getMessage());
	        }else if(null != rest){
				log.warn("调用tms封车校验失败！request={},返回值：{}",JsonHelper.toJson(transWorkItemSimpleDto),JsonHelper.toJson(rest));
				result.toFail(rest.getCode(),rest.getMessage());
	        }else {
				log.warn("调用tms封车校验返回值为空！request={},返回值：{}",JsonHelper.toJson(transWorkItemSimpleDto),JsonHelper.toJson(rest));
				result.toFail("调用tms封车校验返回值为空！");
	        }
		} catch (Exception e) {
			log.error("调用tms封车校验异常！request={}",e,JsonHelper.toJson(transWorkItemSimpleDto));
			result.toError("调用tms封车校验异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
        return result;
    }
    /**
     * 对象转换
     * @param transWorkItemWsDto
     * @return
     */
	private TransWorkItemDto toTransWorkItemDto(TransWorkItemWsDto transWorkItemWsDto) {
		if(transWorkItemWsDto != null) {
			TransWorkItemDto transWorkItemDto = new TransWorkItemDto();
			transWorkItemWsDto.setTransWorkItemCode(transWorkItemWsDto.getTransWorkItemCode());
			transWorkItemWsDto.setOperateNodeCode(transWorkItemWsDto.getOperateNodeCode());
			transWorkItemWsDto.setOperateUserCode(transWorkItemWsDto.getOperateUserCode());
			transWorkItemWsDto.setVehicleNumber(transWorkItemWsDto.getVehicleNumber());
			return transWorkItemDto;
		}
		return null;
	}
	/**
	 * 对象转换
	 * @param transWorkItemSimpleDto
	 * @return
	 */
	private TransWorkItemWsDto toTransWorkItemSimpleDto(TransWorkItemSimpleDto transWorkItemSimpleDto) {
		if(transWorkItemSimpleDto != null) {
			TransWorkItemWsDto transWorkItemWsDto = new TransWorkItemWsDto();
			transWorkItemWsDto.setTransWorkItemCode(transWorkItemSimpleDto.getTransWorkItemCode());
			transWorkItemWsDto.setOperateNodeCode(transWorkItemSimpleDto.getOperateNodeCode());
			transWorkItemWsDto.setOperateUserCode(transWorkItemSimpleDto.getOperateUserCode());
			transWorkItemWsDto.setVehicleNumber(transWorkItemSimpleDto.getVehicleNumber());
			return transWorkItemWsDto;
		}
		return null;
	}
}
