package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发货数据统计表
 *
 * @author chenyaguo
 * @date 2022-11-02 15:26:08
 */
public class JySendProductAggsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;
    /**
     * 任务主键
     */
    private String bizId;

    /**
     * 操作场地ID
     */
    private Long operateSiteId;
    /**
     * 目的场地ID
     */
    private Long receiveSiteId;
    /**
     * 当前流向应扫包裹总计
     */
    private Integer shouldScanCount;

    /**
     * 产品类型
     */
    private String productType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;
    /**
     * 数据库时间
     */
    private Date ts;

    /**
     * 版本号
     */
    private Long version;

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setBizId(String bizId) {
        return this.bizId = bizId;
    }

    public String getBizId() {
        return this.bizId;
    }


    public Long setOperateSiteId(Long operateSiteId) {
        return this.operateSiteId = operateSiteId;
    }

    public Long getOperateSiteId() {
        return this.operateSiteId;
    }

    public Long setReceiveSiteId(Long receiveSiteId) {
        return this.receiveSiteId = receiveSiteId;
    }

    public Long getReceiveSiteId() {
        return this.receiveSiteId;
    }

    public Integer setShouldScanCount(Integer shouldScanCount) {
        return this.shouldScanCount = shouldScanCount;
    }

    public Integer getShouldScanCount() {
        return this.shouldScanCount;
    }

    public Date setCreateTime(Date createTime) {
        return this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Integer setYn(Integer yn) {
        return this.yn = yn;
    }

    public Integer getYn() {
        return this.yn;
    }

    public Date setTs(Date ts) {
        return this.ts = ts;
    }

    public Date getTs() {
        return this.ts;
    }

    public String setSendVehicleBizId(String sendVehicleBizId) {
        return this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getSendVehicleBizId() {
        return this.sendVehicleBizId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
