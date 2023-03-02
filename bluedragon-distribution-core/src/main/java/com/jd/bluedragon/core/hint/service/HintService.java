package com.jd.bluedragon.core.hint.service;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.jd.bluedragon.core.hint.manager.IHintApiUnwrapManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.comp.api.hint.vo.HintResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 提示语服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-15 20:20:36 周四
 */
@Component
public class HintService {

    private static Map<String, String> hintCodeConfMap;

    private static IHintApiUnwrapManager hintApiUnwrapManager;

    private static final Logger log = LoggerFactory.getLogger(HintService.class);

    @Autowired
    public HintService setHintApiUnwrapManager(IHintApiUnwrapManager hintApiUnwrapManager) {
        HintService.hintApiUnwrapManager = hintApiUnwrapManager;
        return this;
    }

    public static Map<String, String> getHintCodeConfMap() {
        return hintCodeConfMap;
    }

    public void setHintCodeConfMap(Map<String, String> hintCodeConfMap) {
        HintService.hintCodeConfMap = hintCodeConfMap;
        log.info("HintService hintCodeConfMap initialized {}", JsonHelper.toJson(hintCodeConfMap));
    }

    public static IHintApiUnwrapManager getHintApiUnwrapManager() {
        return hintApiUnwrapManager;
    }

    public static String getCode(String hintCodeConstant) {
        if(hintCodeConstant == null){
            return null;
        }
        final String hintCode = hintCodeConfMap.get(hintCodeConstant);
        return hintCode != null ? hintCode : hintCodeConstant;
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @param paramsMap 传值参数
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    public static String getHint(String hintCode, Map<String, String> paramsMap){
        HintResp hintResp = hintApiUnwrapManager.getHint(hintCode, paramsMap);
        return (hintResp != null && hintResp.getHintMsg() != null) ? String.format("%s-%s", hintCode, hintResp.getHintMsg()) : hintCode;
    }

    /**
     * 获取提示语
     *
     * @param hintCode 提示语编码
     * @param isShowHintCode 是否展示编码
     * @return
     */
    public static String getHint(String hintCode, Boolean isShowHintCode){
        HintResp hintResp = hintApiUnwrapManager.getHint(hintCode, null);
        return (hintResp != null && hintResp.getHintMsg() != null)
                ? ( isShowHintCode ? String.format("%s-%s", hintCode, hintResp.getHintMsg()) : hintResp.getHintMsg()) : hintCode;
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
    public static String getHint(String reversedStr, String hintCode, Map<String, String> paramsMap){
        String str = HintService.getHint(hintCode, paramsMap) ;
        if(str == null
        		|| Objects.equal(hintCode, str)){
            str = reversedStr;
        }
        return str;
    }

    /**
     * 获取打印系统提示语信息
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    public static String getHint(String hintCode){
        return HintService.getHint(hintCode, hintCode, null);
    }

    /**
     * 获取打印系统提示语信息
     * @param reversedStr 预留缺省字符
     * @param hintCode 提示语编码
     * @return 提示语结果
     * @author fanggang7
     * @time 2021-07-14 18:23:32 周三
     */
    public static String getHint(String reversedStr, String hintCode){
        return HintService.getHint(reversedStr, hintCode, null) ;
    }

    /**
     * 根据功能模块获取提示语
     * @param hintCode
     * @param module
     * @return
     */
    public static String getHintWithFuncModule(String hintCode, int module) {
        // TODO 补全提示语功能模块的语义
        return getHint(hintCode, Maps.<String, String>newHashMap());
    }

    /**
     * 根据功能模块获取提示语
     * @param hintCode
     * @param module
     * @return
     */
    public static String getHintWithFuncModule(String hintCode, int module, Map<String, String> paramsMap) {
        // TODO 补全提示语功能模块的语义
        return getHint(hintCode, paramsMap);
    }
}
