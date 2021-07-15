package com.jd.bluedragon.core.hint.service;

import com.jd.bluedragon.core.hint.manager.IHintApiUnwrapManager;
import com.jd.dms.comp.api.hint.vo.HintResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-15 20:20:36 周四
 */
@Component
public class HintService {

    private static IHintApiUnwrapManager hintApiUnwrapManager;

    @Autowired
    public HintService setHintApiUnwrapManager(IHintApiUnwrapManager hintApiUnwrapManager) {
        HintService.hintApiUnwrapManager = hintApiUnwrapManager;
        return this;
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    public static String getPrintClientHintReverseDefault(String hintCode, Map<String, String> paramsMap){
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
    public static String getPrintClientHintReverseDefault(String hintCode){
        return HintService.getPrintClientHintReverseDefault(hintCode, null);
    }

    /**
     * 获取打印系统提示语信息
     * @param reversedStr 预留缺省字符
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    public static String getPrintClientHint(String reversedStr, String hintCode, Map<String, String> paramsMap){
        String str = HintService.getPrintClientHintReverseDefault(hintCode, paramsMap) ;
        if(str == null){
            str = reversedStr;
        }
        return str;
    }

    /**
     * 获取打印系统提示语信息
     * @param reversedStr 预留缺省字符
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    public static String getPrintClientHint(String reversedStr, String hintCode){
        return HintService.getPrintClientHint(reversedStr, hintCode, null) ;
    }
}
