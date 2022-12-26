package com.jd.bluedragon.distribution.device.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备位置记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-23 17:09:09 周三
 */
public class DeviceLocationLog implements Serializable{
    private static final long serialVersionUID = 5454155825314635342L;

    //columns START
    /**
     * 主键  db_column: id
     */
    private Long id;
    /**
     * 系统编码  db_column: system_code
     */
    private String systemCode;
    /**
     * 设备编码  db_column: device_code
     */
    private String deviceCode;
    /**
     * 设备名称  db_column: device_name
     */
    private String deviceName;
    /**
     * 设备唯一序列号  db_column: device_sn
     */
    private String deviceSn;
    /**
     * 设备类型  db_column: device_type
     */
    private Integer deviceType;
    /**
     * 程序类型  db_column: program_type
     */
    private Integer programType;
    /**
     * 版本号  db_column: version_code
     */
    private String versionCode;
    /**
     * 区域ID  db_column: org_id
     */
    private Integer orgId;
    /**
     * 区域名称  db_column: org_name
     */
    private String orgName;
    /**
     * 场地id  db_column: site_code
     */
    private Integer siteCode;
    /**
     * 场地名称  db_column: site_name
     */
    private String siteName;
    /**
     * ipv4  db_column: ipv4
     */
    private String ipv4;
    /**
     * ipv6  db_column: ipv6
     */
    private String ipv6;
    /**
     * 设备mac地址  db_column: mac_address_self
     */
    private String macAddressSelf;
    /**
     * 连接网络mac地址  db_column: mac_address_network
     */
    private String macAddressNetwork;
    /**
     * 经度  db_column: longitude
     */
    private BigDecimal longitude;
    /**
     * 纬度  db_column: latitude
     */
    private BigDecimal latitude;
    /**
     * 操作人erp  db_column: operate_user_erp
     */
    private String operateUserErp;
    /**
     * 操作人名称  db_column: operate_user_name
     */
    private String operateUserName;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 修改人  db_column: update_user
     */
    private String updateUser;
    /**
     * 修改人名称  db_column: update_user_name
     */
    private String updateUserName;
    /**
     * 修改时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 有效标志  db_column: yn
     */
    private Boolean yn;
    /**
     * 数据库时间  db_column: ts
     */
    private Date ts;
    //columns END

    public DeviceLocationLog(){}
    public DeviceLocationLog(Long id){
        this.id = id;
    }


    @Override
    public String toString() {
        return "DeviceLocationLog{" +
                "id=" + id +
                ", systemCode='" + systemCode + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", deviceType=" + deviceType +
                ", programType=" + programType +
                ", versionCode='" + versionCode + '\'' +
                ", orgId=" + orgId +
                ", orgName='" + orgName + '\'' +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", ipv4='" + ipv4 + '\'' +
                ", ipv6='" + ipv6 + '\'' +
                ", macAddressSelf='" + macAddressSelf + '\'' +
                ", macAddressNetwork='" + macAddressNetwork + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", operateUserErp='" + operateUserErp + '\'' +
                ", operateUserName='" + operateUserName + '\'' +
                ", createTime=" + createTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateUserName='" + updateUserName + '\'' +
                ", updateTime=" + updateTime +
                ", yn=" + yn +
                ", ts=" + ts +
                '}';
    }
}
