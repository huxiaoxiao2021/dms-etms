package com.jd.bluedragon.core.hint.service.impl;

import com.jd.bluedragon.core.hint.manager.IHintApiUnwrapManager;
import com.jd.bluedragon.core.hint.service.IHintApiService;
import com.jd.dms.comp.api.hint.vo.HintReq;
import com.jd.dms.comp.api.hint.vo.HintResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 提示语服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-15 14:43:06 周四
 */
@Service("hintApiService")
public class HintApiServiceImpl implements IHintApiService {

    @Autowired
    private IHintApiUnwrapManager hintApiUnwrapManager;

    /**
     * 获取提示语信息
     * @param req 请求参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public HintResp getHint(HintReq req) {
        return hintApiUnwrapManager.getHint(req);
    }

    /**
     * 获取提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public HintResp getHintByCode(String hintCode) {
        return hintApiUnwrapManager.getHint(hintCode);
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
    public HintResp getHintByCode(String hintCode, Map<String, String> paramsMap) {
        return hintApiUnwrapManager.getHint(hintCode, paramsMap);
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public HintResp getPrintClientHint(String hintCode) {
        return hintApiUnwrapManager.getPrintClientHint(hintCode);
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
    public HintResp getPrintClientHint(String hintCode, Map<String, String> paramsMap) {
        return hintApiUnwrapManager.getPrintClientHint(hintCode, paramsMap);
    }

    /**
     * 获取打印系统提示语信息
     * @param reservedStr 预留的缺省提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public String getPrintClientHint(String reservedStr, String hintCode) {
        HintResp hintResp = hintApiUnwrapManager.getPrintClientHint(hintCode);
        return (hintResp != null && hintResp.getHintMsg() != null) ? hintResp.getHintMsg() : reservedStr;
    }

    /**
     * 获取打印系统提示语信息
     * @param reservedStr 预留的缺省提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public String getPrintClientHint(String reservedStr, String hintCode, Map<String, String> paramsMap) {
        HintResp hintResp = hintApiUnwrapManager.getPrintClientHint(hintCode, paramsMap);
        return (hintResp != null && hintResp.getHintMsg() != null) ? hintResp.getHintMsg() : reservedStr;
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
    public String getPrintClientHintReverseDefault(String hintCode, Map<String, String> paramsMap) {
        HintResp hintResp = hintApiUnwrapManager.getPrintClientHint(hintCode, paramsMap);
        return (hintResp != null && hintResp.getHintMsg() != null) ? hintResp.getHintMsg() : hintCode;
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    @Override
    public String getPrintClientHintReverseDefault(String hintCode) {
        return this.getPrintClientHintReverseDefault(hintCode, null);
    }
}
