package com.jd.bluedragon.distribution.jy.transport.dto;

import com.jd.bluedragon.distribution.jy.dto.JySelectOption;

import java.io.Serializable;
import java.util.List;

/**
 * 运输车辆靠台基础数据
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-09 10:41:54 周二
 */
public class VehicleArriveDockDataDto implements Serializable {

    private static final long serialVersionUID = -5941136054594631023L;

    /**
     * 场地编码
     */
    private String siteCode;

    /**
     * 场地ID
     */
    private Long siteId;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 作业区编码
     */
    private String workAreaCode;

    /**
     * 作业区名称
     */
    private String workAreaName;

    /**
     * 网格编号
     */
    private String workGridNo;

    /**
     * 网格名称
     */
    private String workGridName;

    /**
     * 随机字符
     */
    private String validateRandomStr;

    /**
     * 验证字符，二维码内容
     */
    private String validateStr;

    /**
     * 服务器时间，毫秒单位
     */
    private Long timeMillSeconds;

    /**
     * 服务器时间格式化样式字符
     */
    private String timeStr;

    /**
     * 服务器时间，格式化形式
     */
    private String timeFormatStr;

    public VehicleArriveDockDataDto() {
    }

}
