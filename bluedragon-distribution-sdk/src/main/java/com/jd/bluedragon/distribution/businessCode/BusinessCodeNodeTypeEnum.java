package com.jd.bluedragon.distribution.businessCode;

/**
 * <p>
 *     业务单号的节点枚举
 *     枚举各种单号类型
 *     形如: 批次号  板号  箱号 等
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public enum BusinessCodeNodeTypeEnum {

    /**
     * 批次号
     */
    send_code,

    /**
     * 板标号
     */
    board_code,

    /**
     * 箱号
     */
    box_code;

}
