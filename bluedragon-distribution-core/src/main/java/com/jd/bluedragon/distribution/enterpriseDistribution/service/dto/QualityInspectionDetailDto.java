package com.jd.bluedragon.distribution.enterpriseDistribution.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 质检明细dto
 * @Author chenjunyan
 * @Date 2022/6/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityInspectionDetailDto implements Serializable {
    /**
     * 运单号
     */
    private String waybillNo;
    /**
     *  商品编码
     */
    private String goodsNo;
    /**
     * 69码
     */
    private String barcode;
    /**
     * 物料编码
     */
    private String materialCode;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 货号
     */
    private String goodsCode;
    /**
     * 商品数量
     */
    private Integer goodsQty;
    /**
     * 已质检数
     */
    private Integer checkedQty;
    /**
     * 图片路径
     */
    private String imagePath;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private String updateUser;
}
