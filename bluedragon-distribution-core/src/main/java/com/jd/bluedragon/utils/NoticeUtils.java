package com.jd.bluedragon.utils;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.MspClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通知工具类
 */
public class NoticeUtils {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(NoticeUtils.class);

    /**
     * 发送通知到咚咚
     *
     * @param title   标题
     * @param content 内容
     * @param url     链接
     * @param erpCode 用户erp
     */
    public static void noticeToTimeline(String title, String content, String url, String erpCode) {
        try {
            List<String> pins = Lists.newArrayListWithCapacity(1);
            pins.add(erpCode);
            noticeToTimeline(title, content, url, pins,true);
            logger.info("【导出通知】推送到咚咚结束...title: {}, url: {}, erpCode: {}", title, url, erpCode);
        } catch (Throwable e) {
            logger.error("【导出通知】推送到咚咚发生异常，捕获异常，不影响正常流程", e);
        }
    }

    public static void noticeToTimeline(String title, String content, String url, List<String> erpList) {
        noticeToTimeline(title, content, url, erpList, true);
    }

    public static void noticeToTimelineWithNoUrl(String title, String content, List<String> erpList) {
        noticeToTimeline(title, content, null, erpList, true);
    }

    /**
     * 发送通知到咚咚
     *
     * @param title    标题
     * @param content  内容
     * @param url      链接
     * @param erpCodes 用户erp
     */
    public static void noticeToTimeline(String title, String content, String url, List<String> erpCodes, boolean encodeUrl) {
        try {
            title = "【".concat(title).concat("】");
            Set<String> pins = new HashSet<>(erpCodes);
            MspClientProxy mspClientProxy = (MspClientProxy)SpringHelper.getBean("mspClientProxy");
            mspClientProxy.sendTimeline(title, content, url, pins, encodeUrl);
            logger.info("推送到咚咚结束...title: {}, url: {}, erpCode: {}", title, url, erpCodes);
        } catch (Throwable e) {
            logger.error("推送到咚咚发生异常，捕获异常，不影响正常流程", e);
        }
    }
}
