package com.jd.bluedragon.distribution.cyclebox.domain;
import java.util.Date;

/**
 * @author biyubo
 * @Description 箱号与集包袋绑定关系表
 * @ClassName BoxMaterialRelation
 * @date 2019/9/25
 */
public class BoxMaterialRelation {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 绑定/解绑操作人erp
     */
    private String operatorErp;

    /**
     * 操作站点编号
     */
    private Integer siteCode;

    /**
     * 绑定标识 1 绑定 2 解绑
     */
    private Integer bindFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 数据状态 0 有效 1 无效
     */
    private Integer isDelete;

    /**
     * 时间戳
     */
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getBindFlag() {
        return bindFlag;
    }

    public void setBindFlag(Integer bindFlag) {
        this.bindFlag = bindFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
