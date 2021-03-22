package com.jd.bluedragon.distribution.reflowPackage.doman;
import java.util.Date;

/**
 * @author biyubo
 * @Description 回流包裹表
 * @ClassName ReflowPackage
 * @date 2021/2/24
 */
public class ReflowPackage {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 目的地滑道号
     */
    private String chuteCode;

    /**
     * 目的地笼车号
     */
    private String cageCarCode;

    /**
     * 操作人id
     */
    public Integer operatorCode;

    /**
     * 操作人erp
     */
    private String operatorErp;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作站点编号
     */
    private Integer siteCode;

    /**
     * 操作站点名称
     */
    private String siteName;


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

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getChuteCode() {
        return chuteCode;
    }

    public void setChuteCode(String chuteCode) {
        this.chuteCode = chuteCode;
    }

    public String getCageCarCode() {
        return cageCarCode;
    }

    public void setCageCarCode(String cageCarCode) {
        this.cageCarCode = cageCarCode;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
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
