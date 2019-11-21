package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.preseparate.jsf.BatchExpressTransferHandleAPI;
import com.jd.bluedragon.preseparate.jsf.CommonOrderServiceJSF;
import com.jd.bluedragon.preseparate.jsf.PresortMediumStationAPI;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.preseparate.vo.*;
import com.jd.preseparate.vo.external.AnalysisAddressResult;
import com.jd.preseparate.vo.external.ExternalOrderDto;
import com.jd.preseparate.vo.external.PreSeparateAddressInfo;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by wangtingwei on 2015/10/28.
 */
@Service("preseparateWaybillManager")
public class PreseparateWaybillManagerImpl implements PreseparateWaybillManager {

    private static final Logger log = LoggerFactory.getLogger(PreseparateWaybillManagerImpl.class);

    @Autowired
    @Qualifier("preseparateOrderService")
    private CommonOrderServiceJSF preseparateOrderService;
	/**
	 * 中小件-接口调用成功编码-200
	 */
	private static final Integer CODE_SUC = 200;
    @Autowired
    private PresortMediumStationAPI presortMediumStation;

    @Autowired
    private BatchExpressTransferHandleAPI batchExpressTransferHandleAPI;



	public PsOrderSeparateVo getPreSeparateOrderByOrderId(String waybillCode){
		CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.CommonOrderServiceJSF.getPreSeparateOrderByOrderId",Constants.UMP_APP_NAME_DMSWEB,false,true);
		try {
			return preseparateOrderService.getPreSeparateOrderByOrderId(waybillCode);
		}catch (Exception e){
			Profiler.functionError(callerInfo);
			throw e;
		}finally {
			Profiler.registerInfoEnd(callerInfo);
		}
	}

	public ExternalOrderDto getPreSeparateExternalByOrderId(String waybillCode){
		CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.CommonOrderServiceJSF.getPreSeparateExternalByOrderId",Constants.UMP_APP_NAME_DMSWEB,false,true);
		try {
			return preseparateOrderService.getPreSeparateExternalByOrderId(waybillCode);
		}catch (Exception e){
			Profiler.functionError(callerInfo);
			throw e;
		}finally {
			Profiler.registerInfoEnd(callerInfo);
		}
	}


    @Override
    public Integer getPreseparateSiteId(String waybillCode) throws Exception {
        Integer siteId=null;
        if(WaybillUtil.isJDWaybillCode(waybillCode)){
            PsOrderSeparateVo domain= getPreSeparateOrderByOrderId(waybillCode.trim());
            if(null!=domain){
                if(log.isDebugEnabled()){
                    log.debug(JsonHelper.toJson(domain));
                }
                siteId=domain.getPartnerId();
            }
        }else {
            ExternalOrderDto domain = getPreSeparateExternalByOrderId(waybillCode.trim());
            if(null!=domain){
                if(log.isDebugEnabled()){
                    log.debug(JsonHelper.toJson(domain));
                }
                siteId=domain.getPartnerId();
            }
        }
        return siteId;
    }
	/**
	 * 根据原站点和上传的称重信息获取新预分拣站点
	 * @param originalOrderInfo 原站点及运单+重量信息
	 * @return
	 */
	@Override
	public JdResult<BaseResponseIncidental<MediumStationOrderInfo>> getMediumStation(OriginalOrderInfo originalOrderInfo) {
		JdResult<BaseResponseIncidental<MediumStationOrderInfo>> result = new JdResult<BaseResponseIncidental<MediumStationOrderInfo>>();
		CallerInfo monitor = Profiler.registerInfo("dmsWeb.jsf.PresortMediumStationAPI.getMediumStation", false, true);
		try {
			//设置默认传递的系统标识码
			originalOrderInfo.setSystemCode(Constants.SYSTEM_CODE_OWON);
			if(log.isDebugEnabled()){
				log.debug("调用中小件二次预分拣JSF接口参数：{}",JsonHelper.toJsonUseGson(originalOrderInfo));
			}
			BaseResponseIncidental<MediumStationOrderInfo> apiResult = presortMediumStation.getMediumStation(originalOrderInfo);
            if(log.isDebugEnabled()){
				log.debug("调用中小件二次预分拣JSF接口返回结果：{}",JsonHelper.toJsonUseGson(apiResult));
			}
			if(apiResult != null && CODE_SUC.equals(apiResult.getCode())){
				result.toSuccess();
				result.setData(apiResult);
			}else{
				log.warn("中小件-调用外部接口获取预分拣站点数据为空:{}",JsonHelper.toJson(originalOrderInfo));
				result.toFail(JdResult.CODE_FAIL, "中小件-调用外部接口获取预分拣站点数据为空！");
			}
        }catch (Throwable throwable){
        	result.toFail(JdResult.CODE_ERROR, "调用外部接口获取预分拣站点异常:"+throwable.getMessage());
        	log.error("中小件-调用JSF-PresortMediumStationAPI.getMediumStation获取预分拣站点异常:{}",JsonHelper.toJson(originalOrderInfo), throwable);
			Profiler.functionError(monitor);
		}finally{
			Profiler.registerInfoEnd(monitor);
		}
		return result;
	}

	/**
	 * 根据详细地址获取四级地址
	 * @param address
	 * @return
	 */
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.PreseparateWaybillManagerImpl.analysisAddress", mState = {JProEnum.TP, JProEnum.FunctionError})
	public AnalysisAddressResult analysisAddress(String address){
		PreSeparateAddressInfo addressInfo = new PreSeparateAddressInfo();
		addressInfo.setFullAddress(address);
		addressInfo.setSysCode(Constants.SYSTEM_CODE_WEB);
		return preseparateOrderService.analysisAddress(addressInfo);
	}


	/**
	 * 批量转网
	 * @param request
	 * @return
	 */
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.JSF.Preseparate.batchExpressTransferHandleAPI.handle", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BaseResponseIncidental<BatchTransferResult> batchTransfer(BatchTransferRequest request){
		return batchExpressTransferHandleAPI.handle(request);
	}
}
