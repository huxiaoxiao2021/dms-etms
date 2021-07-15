package com.jd.bluedragon.core.hint.service;

import com.jd.dms.comp.api.hint.vo.HintReq;
import com.jd.dms.comp.api.hint.vo.HintResp;

import java.util.Map;

/**
 * 提示语服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-15 14:43:06 周四
 */
public interface IHintApiService {

    /**
     * 获取提示语信息
     * @param req 请求参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    HintResp getHint(HintReq req);

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    HintResp getHintByCode(String hintCode);

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    HintResp getHintByCode(String hintCode, Map<String, String> paramsMap);

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    HintResp getPrintClientHint(String hintCode);

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    HintResp getPrintClientHint(String hintCode, Map<String, String> paramsMap);

    /**
     * 获取打印系统提示语信息
     * @param reservedMsg 预留的缺省提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    String getPrintClientHint(String reservedMsg, String hintCode);

    /**
     * 获取打印系统提示语信息
     * @param reservedMsg 预留的缺省提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    String getPrintClientHint(String reservedMsg, String hintCode, Map<String, String> paramsMap);

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    String getPrintClientHintReverseDefault(String hintCode, Map<String, String> paramsMap);

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    String getPrintClientHintReverseDefault(String hintCode);
}
