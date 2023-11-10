package com.jd.bluedragon.core.base;

import cn.jdl.jecap.adapter.AdapterApi;
import cn.jdl.jecap.request.adapter.AdapterRequest;
import cn.jdl.jecap.response.adapter.AdapterResponse;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.adapter.AdapterOutOfPlatformDecryRouter;
import com.jd.bluedragon.core.jsf.adapter.AdapterRequestOfPlatformDecryRouter;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
    public static String PLATFORM_ZJ = "10";

    /**
     * 抖音解密服务
     */
    public static String PLATFORM_DECRY_ROUTER = "platformDecryRouter";

    /**
     * 抖音解密服务
     */
    public static String QUERY_REASON_EMS = "转三方邮政使用";

    /**
     * 字节相关运单的收件人信息 解密服务
     *
     * 给其他系统提供服务包装接口尽量不要用string，如果用了那最好把实体放依赖包里，与人方便自己方便。
     *
     * 如果想在commonAdapterExcute调用他这个接口里面的其他方法，请自行包装实体
     *
     * https://joyspace.jd.com/pages/YFpNlkVE7RzzK36YGS7z
     *
     * @param request  运单号 和 原因 必填
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.AssertQueryManagerImpl.commonAdapterExecuteOfPlatformDecryRouter",
            mState = {JProEnum.TP,JProEnum.FunctionError})
    public AdapterOutOfPlatformDecryRouter commonAdapterExecuteOfPlatformDecryRouter(AdapterRequestOfPlatformDecryRouter request) {
        try{
            AdapterRequest jsfRequest = new AdapterRequest();
            request.setDecryType(Constants.CONSTANT_NUMBER_ONE);
            request.setPlatform(PLATFORM_ZJ);
            request.setUserType(Constants.CONSTANT_NUMBER_ONE);
            request.setUserId(Constants.UMP_APP_NAME_DMSWORKER);
            request.setSystemSource(Constants.UMP_APP_NAME_DMSWORKER);
            jsfRequest.setJsfArrangeName(PLATFORM_DECRY_ROUTER);
            jsfRequest.setStandardJson(JsonHelper.toJson(request));
            AdapterResponse jsfResp = adapterApiJsfService.commonAdapterExcute(jsfRequest);
            jsfResp.setOutJson("{\"returnCode\":\"0\",\"result\":true,\"data\":{\"waybillCode\":\"JDAZ00001587575\",\"receiver\":{\"name\":\"周借钱\",\"mobile\":\"18187627811\",\"virtualMobile\":\"18187627811-0878\"}}}");
            if(jsfResp == null || StringUtils.isBlank(jsfResp.getOutJson())){
                logger.error("commonAdapterExcute fail! resp is null, req:{}",JsonHelper.toJson(jsfRequest));
                return null;
            }
            AdapterOutOfPlatformDecryRouter response = JsonHelper.fromJson(jsfResp.getOutJson(), AdapterOutOfPlatformDecryRouter.class);
            if(response == null || !response.getResult()){
                logger.error("commonAdapterExcute fail! resp {}, req:{}",jsfResp.getOutJson(),JsonHelper.toJson(jsfRequest));
            }else {
                logger.info("commonAdapterExcute success! resp {}, req:{}",jsfResp.getOutJson(),JsonHelper.toJson(jsfRequest));
            }
            return response;
        }catch (Exception e){
            logger.error("commonAdapterExcute error , waybill:{}",request.getWaybillCode(),e);
        }
        return null;
    }

}
