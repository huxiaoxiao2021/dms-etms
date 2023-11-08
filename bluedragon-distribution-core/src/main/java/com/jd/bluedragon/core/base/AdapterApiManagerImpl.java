package com.jd.bluedragon.core.base;

import cn.jdl.jecap.adapter.AdapterApi;
import cn.jdl.jecap.request.adapter.AdapterRequest;
import cn.jdl.jecap.response.adapter.AdapterResponse;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.adapter.AdapterOutJsonObj;
import com.jd.bluedragon.core.jsf.adapter.AdapterRequestStandardJsonObj;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/11/8
 * @Description: 商家服务
 */
@Service("adapterApiManager")
public class AdapterApiManagerImpl implements AdapterApiManager {

    private static final Logger logger = LoggerFactory.getLogger(AdapterApiManagerImpl.class);

    @Autowired
    private AdapterApi adapterApiJsfService;

    /**
     * 所属平台：同运单增值服务中保持一致，10:字节
     */
    String COMPANY_ZJ = "10";

    /**
     * 字节相关运单的收件人信息 解密服务
     *
     * 给其他系统提供服务包装接口尽量不要用string，如果用了那最好把实体放依赖包里，与人方便自己方便。
     *
     *
     * https://joyspace.jd.com/pages/YFpNlkVE7RzzK36YGS7z
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.AssertQueryManagerImpl.commonAdapterExcute",
            mState = {JProEnum.TP,JProEnum.FunctionError})
    public AdapterOutJsonObj commonAdapterExcute(AdapterRequestStandardJsonObj request) {
        try{
            AdapterRequest jsfRequest = new AdapterRequest();
            request.setDecryType(Constants.CONSTANT_NUMBER_ONE);
            request.setCompany(COMPANY_ZJ);
            jsfRequest.setJsfArrangeName("platformDecryRouter");
            jsfRequest.setStandardJson(JsonHelper.toJson(request));
            AdapterResponse jsfResp = adapterApiJsfService.commonAdapterExcute(jsfRequest);
            if(jsfResp == null || StringUtils.isBlank(jsfResp.getOutJson())){
                logger.error("commonAdapterExcute fail! resp is null, req:{}",JsonHelper.toJson(jsfRequest));
                return null;
            }
            AdapterOutJsonObj response = JsonHelper.fromJson(jsfResp.getOutJson(),AdapterOutJsonObj.class);
            if(response == null || !response.getResult()){
                logger.error("commonAdapterExcute fail! resp {}, req:{}",jsfResp.getOutJson(),JsonHelper.toJson(jsfRequest));
            }else {
                logger.error("commonAdapterExcute success! resp {}, req:{}",jsfResp.getOutJson(),JsonHelper.toJson(jsfRequest));
            }
            return response;
        }catch (Exception e){
            logger.error("commonAdapterExcute error , waybill:{}",request.getWaybillCode(),e);
        }
        return null;
    }

}
