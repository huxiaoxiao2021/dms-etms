package com.jd.bluedragon.core.hint.manager;

import com.jd.dms.comp.api.hint.vo.HintReq;
import com.jd.dms.comp.api.hint.vo.HintResp;
import com.jd.dms.comp.api.hint.vo.HintVoiceReq;
import com.jd.dms.comp.api.hint.vo.HintVoiceResp;
import com.jd.dms.comp.base.ApiResult;

import java.util.Map;

/**
 * 提示语接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-14 18:16:51 周三
 */
public interface IHintApiManager {

    /**
     * 获取提示语信息
     * @param req 请求参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    ApiResult<HintResp> getHint(HintReq req);

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    ApiResult<HintResp> getHint(String hintCode);

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    ApiResult<HintResp> getHint(String hintCode, Map<String, String> paramsMap);

    /**
     * 获取通用提示语配置
     *
     * @param req
     * @return
     */
    ApiResult<HintVoiceResp> getCommonHintVoiceConfig(HintVoiceReq req);
}
