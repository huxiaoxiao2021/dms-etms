package com.jd.bluedragon.distribution.capability.send.handler;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/22
 * @Description:
 */
public interface ISendHandler {

    /**
     * 执行逻辑
     * @param context
     * @return
     */

    boolean doHandler(SendOfCAContext context);

    /**
     * 是否必须执行，返回true时不允许跳过,必须执行。
     * @return
     */
    boolean mustHandler();

}
