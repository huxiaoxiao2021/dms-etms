package com.jd.bluedragon.core.base;

import com.jd.mrd.delivery.rpc.sdk.feedback.dto.UserFeedbackContent;

import java.util.Map;

/**
 * @author lixin39
 * @Description 意见反馈系统接入
 * @ClassName MrdFeedbackManager
 * @date 2019/5/23
 */
public interface MrdFeedbackManager {

    /**
     * 按照指定格式提交意见反馈信息
     *
     * @param userFeedbackContent
     * @return
     */
    boolean saveFeedback(UserFeedbackContent userFeedbackContent);

    /**
     * 根据包名获取反馈类型
     *
     * @param packageName
     * @return
     */
    Map<Integer, String> getFeedType(String packageName);

    /**
     * 根据包名和物流权限码获取反馈类型
     *
     * @param packageName
     * @param roleIndex
     * @return
     */
    Map<Integer, String> getFeedTypeWithRole(String packageName, String roleIndex);
}
