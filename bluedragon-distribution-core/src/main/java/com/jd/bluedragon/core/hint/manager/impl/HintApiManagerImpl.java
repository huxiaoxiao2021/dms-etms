package com.jd.bluedragon.core.hint.manager.impl;

import com.jd.bluedragon.core.hint.manager.IHintApiManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.comp.api.hint.HintApi;
import com.jd.dms.comp.api.hint.vo.HintReq;
import com.jd.dms.comp.api.hint.vo.HintResp;
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

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HintApi hintApi;

    @Value("${hint.dms.system.sourceCode}")
    private String dmsSourceCode;

    @Value("${hint.printClient.system.sourceCode}")
    private String printClientSourceCode;

    /**
     * 获取提示语信息
     * @param req 请求参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getHint", mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getHint(HintReq req){
        req.setSource(dmsSourceCode);
        ApiResult<HintResp> hintResult = hintApi.getHint(req);
        if(!hintResult.checkSuccess()){
            log.error("HintApiManagerImpl.getHint fail {}", JsonHelper.toJson(hintResult));
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
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getHintByCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getHint(String hintCode) {
        HintReq req = new HintReq();
        req.setSource(dmsSourceCode);
        req.setHintCode(hintCode);
        ApiResult<HintResp> hintResult = hintApi.getHint(req);
        if(!hintResult.checkSuccess()){
            log.error("HintApiManagerImpl.getHintByCode fail {}", JsonHelper.toJson(hintResult));
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
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getHintByCodeAndAndParamsMap", mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getHint(String hintCode, Map<String, String> paramsMap) {
        HintReq req = new HintReq();
        req.setSource(dmsSourceCode);
        req.setHintCode(hintCode);
        req.setParamsMap(paramsMap);
        ApiResult<HintResp> hintResult = hintApi.getHint(req);
        if(!hintResult.checkSuccess()){
            log.error("HintApiManagerImpl.getHintByCodeAndAndParamsMap fail {}", JsonHelper.toJson(hintResult));
        }
        return hintResult;
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getPrintClientHintByCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getPrintClientHint(String hintCode) {
        HintReq req = new HintReq();
        req.setSource(printClientSourceCode);
        req.setHintCode(hintCode);
        ApiResult<HintResp> hintResult = hintApi.getHint(req);
        if(!hintResult.checkSuccess()){
            log.error("HintApiManagerImpl.getPrintClientHintByCode fail {}", JsonHelper.toJson(hintResult));
        }
        return hintResult;
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.HintApiManagerImpl.getPrintClientHintByCodeAndParamsMap", mState = {JProEnum.TP, JProEnum.FunctionError})
    public ApiResult<HintResp> getPrintClientHint(String hintCode, Map<String, String> paramsMap) {
        HintReq req = new HintReq();
        req.setSource(printClientSourceCode);
        req.setHintCode(hintCode);
        req.setParamsMap(paramsMap);
        ApiResult<HintResp> hintResult = hintApi.getHint(req);
        if(!hintResult.checkSuccess()){
            log.error("HintApiManagerImpl.getPrintClientHintByCodeAndParamsMap fail {}", JsonHelper.toJson(hintResult));
        }
        return hintResult;
    }
}
