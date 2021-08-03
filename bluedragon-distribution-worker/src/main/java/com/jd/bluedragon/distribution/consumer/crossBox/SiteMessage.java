package com.jd.bluedragon.distribution.consumer.crossBox;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SiteMessage {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 站点编号
     */
    private Integer siteCode;
    /**
     * 站点名称
     */
    private String siteName;
    /**
     * 机构Id
     */
    private Integer orgId;

    /**
     * 机构名称
     */
    private String orgName;
    /**
     * 站点类型
     */
    private Integer siteType;
    /**
     * 站点子类型
     */
    private Integer subType;

    /**
     * 站点三级子类型
     */
    private Integer thirdType;

    /**
     * 类型：1-站点，2-分拣
     */
    private Integer targetType;

    /**
     * 站点7位编码
     */
    private String dmsCode;

    /**
     * 站点绑定分拣中心ID
     */
    private Integer dmsId;

    /**
     * 站点经度
     */
    private Double longitude;
    /**
     * 站点纬度
     */
    private Double latitude;
    /**
     * 省Id
     */
    private Integer provinceId;
    /**
     * 市Id
     */
    private Integer cityId;
    /**
     * 县Id
     */
    private Integer countyId;
    /**
     * 乡Id
     */
    private Integer townId;
    /**
     * 详细地址
     */
    private String address;

    /**
     * 省公司编号
     */
    private String provinceCompanyCode;

    /**
     * 片区编号
     */
    private String areaCode;

    /**
     * 分区编号
     */
    private String partitionCode;

    /**
     * 组织架构编码
     */
    private String organCode;

    /**
     * 联系人
     */
    private String contact;
    /**
     * 联系电话
     */
    private String telephone;

    /**
     * 是否支持无人车标识 1：支持无人车，其它不支持，数据库对应字段EXPAND_3
     */
    private String supportVehicle;

    /**
     * 是否开通迷你仓
     */
    private Integer isMiniWarehouse;

    /**
     * 站点业务类型
     * 1-京配配送、自提服务
     * 2-京配配送服务
     * 3-自提服务
     * 4-非京配配送服务
     * 5-京配配送、自提服务、生鲜自提
     * 6-自提服务、生鲜自提
     * 7-非京配配送服务、自提服务
     */
    private Integer siteBusinessType;

    /**
     * 特色服务
     */
    private String specialService;

    /**
     * 自营订单时效
     */
    private String orderTimeEffective;

    /**
     * 外单时效产品：1-当日达 2-次日达  3-隔日达 4-4日及以上达
     */
    private String outerOrderProduct;

    /**
     * 运营状态（线上运营-1，线下运营-2，关闭-0）
     */
    private Integer operateState;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 站点面积大小
     */
    private BigDecimal siteArea;

    /**
     * 营业时间 起始时间，格式：HH:mm
     */
    private String businessHoursStart;

    /**
     * 营业时间 结束时间，格式：HH:mm
     */
    private String businessHoursEnd;

    /**
     * 自提点可见区域：一级地址-1，二级地址-2，三级地址-3，四级地址-4
     */
    private Integer visibleArea;

    /**
     * 时效
     */
    private Integer fixTime;


    /**
     * 配送半径(单位公里)
     */
    private Double dispatchRange;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;


    /**
     * 备注
     */
    private String memo;



    /**
     * 是否有效：1-有效，0-无效
     */
    private Integer yn;

    /**
     * 扩展字段1
     */
    private String expand1;

    /**
     * 扩展字段2
     */
    private String expand2;

    /**
     * 手机号1、手机号2
     */
    private String mobilePhone1;
    private String mobilePhone2;

    /**
     * 站点标位
     */
    private String siteSign;


    /**
     * 归属站点
     */
    private Integer belongCode;
    private String belongName;

    /**
     * 提货网点ID（大小站
     */
    private Integer pickupBelongCode;
    private String pickupBelongName;

    /**
     * 3PL归属站点
     */
    private Integer thirdPartnerBelongCode;
    private String thirdPartnerBelongName;

    /**
     * 自提柜柜机厂商
     */
    private Integer companyCode;

    /**
     * 限关键字下单：0否 1是
     */
    private Integer isKeyword;

    /**
     * 特殊提示
     */
    private String specialRemark;

    /**
     * 关键字
     */
    private List<String> keywords;

    /**
     * 四级地址名称
     */
    private String provinceName;
    private String cityName;
    private String countyName;
    private String townName;

    /**
     * 组织机构名称
     */
    private String provinceCompanyName;
    private String areaName;
    private String partitionName;
}
