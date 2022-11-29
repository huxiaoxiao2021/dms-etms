package com.jd.bluedragon.core.hint.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.hint.manager.IHintApiManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.comp.api.hint.HintApi;
import com.jd.dms.comp.api.hint.vo.HintReq;
import com.jd.dms.comp.api.hint.vo.HintResp;
import com.jd.dms.comp.api.hint.vo.HintVoiceReq;
import com.jd.dms.comp.api.hint.vo.HintVoiceResp;
import com.jd.dms.comp.base.ApiResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 提示语接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-14 18:16:51 周三
 */
@Component("hintApiManager")
public class HintApiManagerImpl implements IHintApiManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HintApi hintApi;

    @Value("${hint.dms.system.systemCode}")
    private String dmsSystemCode;

    /**
     * 获取提示语信息
     * @param req 请求参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getHint", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getHint(HintReq req){
        ApiResult<HintResp> hintResult = new ApiResult<>();
        try {
            req.setSystemCode(dmsSystemCode);
            hintResult = hintApi.getHint(req);
            if(!hintResult.checkSuccess()){
                log.error("HintApiManagerImpl.getHint fail {}", JsonHelper.toJson(hintResult));
            }
        } catch (Exception e) {
            log.error("HintApiManagerImpl.getHint exception {}", e.getMessage(), e);
            hintResult.toFail("调用提示语系统异常");
        }
        return hintResult;
    }

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getHint", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getHint(String hintCode) {
        ApiResult<HintResp> hintResult = new ApiResult<>();
        try {
            HintReq req = new HintReq();
            req.setSystemCode(dmsSystemCode);
            req.setHintCode(hintCode);
            hintResult = this.getHint(req);
            if(!hintResult.checkSuccess()){
                log.error("HintApiManagerImpl.getHint fail {}", JsonHelper.toJson(hintResult));
            }
        } catch (Exception e) {
            log.error("HintApiManagerImpl.getHint exception {}", e.getMessage(), e);
            hintResult.toFail("调用提示语系统异常");
        }
        return hintResult;
    }

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getHint", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getHint(String hintCode, Map<String, String> paramsMap) {
        ApiResult<HintResp> hintResult = new ApiResult<>();
        try {
            HintReq req = new HintReq();
            req.setSystemCode(dmsSystemCode);
            req.setHintCode(hintCode);
            req.setParamsMap(paramsMap);
            hintResult = this.getHint(req);
            if(!hintResult.checkSuccess()){
                log.error("HintApiManagerImpl.getHint fail {}", JsonHelper.toJson(hintResult));
            }
        } catch (Exception e) {
            log.error("HintApiManagerImpl.getHint exception {}", e.getMessage(), e);
            hintResult.toFail("调用提示语系统异常");
        }
        return hintResult;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getCommonHintVoiceConfig", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintVoiceResp> getCommonHintVoiceConfig(HintVoiceReq req) {
        ApiResult<HintVoiceResp> hintResult = new ApiResult<>();
        try {
            req.setSystemCode(dmsSystemCode);
            hintResult = hintApi.getCommonHintVoiceConfig(req);
            if(!hintResult.checkSuccess()){
                log.error("HintApiManagerImpl.getCommonHintVoiceConfig fail {}", JsonHelper.toJson(hintResult));
            }
        } catch (Exception e) {
            log.error("HintApiManagerImpl.getCommonHintVoiceConfig exception {}", e.getMessage(), e);
            hintResult.toFail("调用提示语系统异常");
        }
        return hintResult;
    }
}
