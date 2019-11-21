package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dispatch.api.DmsToVendorDispatchService;
import com.jd.ql.dispatch.dto.BaseResponse;
import com.jd.ql.dispatch.dto.DmsVendorRequest;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 内部实物网络（C、B、TC）互转服务
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月04日 13时:38分
 */
@Service("dmsInterturnManager")
public class DmsInterturnManagerImpl implements DmsInterturnManager{

    private final Logger log = LoggerFactory.getLogger(DmsInterturnManagerImpl.class);

    @Autowired
    private DmsToVendorDispatchService dmsToVendorDispatchService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsInterturnManagerImpl.dispatchToExpress", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> dispatchToExpress(Integer siteCode, Integer vendorId, String waybillSign) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(siteCode == null || vendorId == null || StringUtils.isEmpty(waybillSign)){
            result.parameterError("参数为空");
            return result;
        }
        DmsVendorRequest request = new DmsVendorRequest();
        request.setDmsId(siteCode);
        request.setVendorId(vendorId);
        request.setWaybillSign(waybillSign);
        if(log.isDebugEnabled()){
            log.debug("C网转B网校验请求参数：{}" , JsonHelper.toJson(request));
        }
        BaseResponse<Boolean> dmsToVendor =  dmsToVendorDispatchService.dispatchToExpress(request);
        if(dmsToVendor.getCode() != BaseResponse.OK_CODE || dmsToVendor.getData() ==null || dmsToVendor.getData().booleanValue() == false) {
            log.warn("C网转B网校验不通过，参数：{}；结果：{}",JsonHelper.toJson(request), JsonHelper.toJson(dmsToVendor));
        }
        result.setCode(dmsToVendor.getCode());
        result.setMessage(dmsToVendor.getMessage());
        result.setData(dmsToVendor.getData());
        return result;
    }
}
