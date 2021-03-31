package com.jd.bluedragon.distribution.discardedPackageStorageTemp.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2021-03-31 11:32:59 周三
 */
public class DiscardedPackageStorageTemp implements Serializable{

    private static final long serialVersionUID = -3635180133815548634L;

    //columns START
    /**
     * 主键，雪花计算值  db_column: id
     */
    private Long id;
    /**
     * 运单号  db_column: waybill_code
     */
    private String waybillCode;
    /**
     * 包裹号  db_column: package_code
     */
    private String packageCode;
    /**
     * 状态 0 弃件暂存 1 弃件出库 2 已认领  db_column: status
     */
    private Integer status;
    /**
     * 快递产品类型  db_column: waybill_product
     */
    private String waybillProduct;
    /**
     * 托寄物名称  db_column: consignment_name
     */
    private String consignmentName;
    /**
     * 重量 单位KG  db_column: weight
     */
    private BigDecimal weight;
    /**
     * 是否cod 0 不是cod 1 是cod  db_column: cod
     */
    private Integer cod;
    /**
     * cod代收款金额 单位元  db_column: cod_amount
     */
    private BigDecimal codAmount;
    /**
     * 商家编码  db_column: business_code
     */
    private String businessCode;
    /**
     * 商家名称  db_column: business_name
     */
    private String businessName;
    /**
     * 操作人ID  db_column: operator_code
     */
    private Long operatorCode;
    /**
     * 操作人erp  db_column: operator_erp
     */
    private String operatorErp;
    /**
     * 操作人姓名  db_column: operator_name
     */
    private String operatorName;
    /**
     * 操作站点ID  db_column: site_code
     */
    private Integer siteCode;
    /**
     * 操作站点名称  db_column: site_name
     */
    private String siteName;
    /**
     * 操作站点所在城市  db_column: site_city
     */
    private String siteCity;
    /**
     * 操作站点所在大区  db_column: org_code
     */
    private Integer orgCode;
    /**
     * 操作站点所在大区名称  db_column: org_name
     */
    private String orgName;
    /**
     * 上一级操作站点ID  db_column: prev_site_code
     */
    private Integer prevSiteCode;
    /**
     * 上一级操作站点ID名称  db_column: prev_site_code_name
     */
    private String prevSiteCodeName;
    /**
     * 上一级操作站点所在大区  db_column: prev_org_code
     */
    private Integer prevOrgCode;
    /**
     * 上一级操作站点所在大区名称  db_column: prev_org_name
     */
    private String prevOrgName;
    /**
     * 创建时间  db_column: create_time
     */
    private Date createTime;
    /**
     * 更新时间  db_column: update_time
     */
    private Date updateTime;
    /**
     * 数据状态 1 有效 0 无效  db_column: yn
     */
    private Integer yn;
    /**
     * 时间戳  db_column: ts
     */
    private Date ts;
    //columns END

    @Override
    public String toString() {
        return "DiscardedPackageStorageTemp{" +
                "id=" + id +
                ", waybillCode='" + waybillCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", status=" + status +
                ", waybillProduct='" + waybillProduct + '\'' +
                ", consignmentName='" + consignmentName + '\'' +
                ", weight=" + weight +
                ", cod=" + cod +
                ", codAmount=" + codAmount +
                ", businessCode='" + businessCode + '\'' +
                ", businessName='" + businessName + '\'' +
                ", operatorCode=" + operatorCode +
                ", operatorErp='" + operatorErp + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", siteCity='" + siteCity + '\'' +
                ", orgCode=" + orgCode +
                ", orgName='" + orgName + '\'' +
                ", prevSiteCode=" + prevSiteCode +
                ", prevSiteCodeName='" + prevSiteCodeName + '\'' +
                ", prevOrgCode=" + prevOrgCode +
                ", prevOrgName='" + prevOrgName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", yn=" + yn +
                ", ts=" + ts +
                '}';
    }
}
