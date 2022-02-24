package com.jd.bluedragon.distribution.qualityControl.dto;

/**
 * 附件信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-02-18 10:25:19 周五
 */
public class AttachInfoDto {

    /**
     * 名称：
     * 文件：文件名
     * 鲸盘：”鲸盘“
     * 文本框："电报号"、"车牌号"等
     */
    private String name;

    /**
     * 值：
     * 文件：文件在jss的路径
     * 鲸盘：鲸盘地址
     * 文本框：文本值
     */
    private String value;

    /**
     * 类型
     * localfile:文件
     * jbox：鲸盘
     * character：文本框（电报号，车牌号等）
     */
    private String attachType;
}
