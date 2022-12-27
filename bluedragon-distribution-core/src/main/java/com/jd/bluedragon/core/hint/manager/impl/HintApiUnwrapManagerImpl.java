package com.jd.bluedragon.core.hint.manager.impl;

import com.jd.bluedragon.core.hint.manager.IHintApiManager;
import com.jd.bluedragon.core.hint.manager.IHintApiUnwrapManager;
import com.jd.dms.comp.api.hint.vo.HintReq;
import com.jd.dms.comp.api.hint.vo.HintResp;
import com.jd.dms.comp.api.hint.vo.HintVoiceReq;
import com.jd.dms.comp.api.hint.vo.HintVoiceResp;
import com.jd.dms.comp.base.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 提示语接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-14 18:16:51 周三
 */
@Component("hintApiUnwrapManager")
public class HintApiUnwrapManagerImpl implements IHintApiUnwrapManager {

    @Autowired
    private IHintApiManager hintApiManager;

    /**
     * 获取提示语信息
     * @param req 请求参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public HintResp getHint(HintReq req) {
        ApiResult<HintResp> hintResult = hintApiManager.getHint(req);
        return hintResult.checkSuccess() ? hintResult.getData() : null;
    }

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public HintResp getHint(String hintCode) {
        ApiResult<HintResp> hintResult = hintApiManager.getHint(hintCode);
        return hintResult.checkSuccess() ? hintResult.getData() : null;
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
    public HintResp getHint(String hintCode, Map<String, String> paramsMap) {
        ApiResult<HintResp> hintResult = hintApiManager.getHint(hintCode, paramsMap);
        return hintResult.checkSuccess() ? hintResult.getData() : null;
    }

    @Override
    public HintVoiceResp getCommonHintVoiceConfig(HintVoiceReq req) {
        ApiResult<HintVoiceResp> result = hintApiManager.getCommonHintVoiceConfig(req);
        return result.checkSuccess() ? result.getData() : null;
    }
}
