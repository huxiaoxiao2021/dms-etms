package com.jd.bluedragon.distribution.abnormalwaybill.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * PDA异常操作查询类
 * @author fanggang7
 * @time 2023-08-28 20:04:37 周一
 */
public class AbnormalWayBillQuery extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = 5693545231191551823L;

    /** 全局唯一ID */
    private Long id;

    /** 运单号 */
    private String waybillCode;

    /** 包裹号 */
    private String packageCode;

    /** 申请人帐号 */
    private Integer createUserCode;

    /** 申请人ERP帐号 */
    private String createUserErp;

    /** 申请人 */
    private String createUser;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 分拣中心名称 */
    private String createSiteName;

    /** 1:包裹号|2:运单号|3:箱号|4:批次号|5:车次号 */
    private Integer qcType;

    /** qcType的具体值 */
    private String qcValue;

    /**
     * 异常一级原因ID
     */
    private Long abnormalReasonFirstId;

    /**
     * 异常一级原因名称
     */
    private String abnormalReasonFirstName;

    /**
     * 异常二级原因ID
     */
    private Long abnormalReasonSecondId;

    /**
     * 异常二级原因名称
     */
    private String abnormalReasonSecondName;

    /**
     * 异常三级原因ID
     */
    private Long abnormalReasonThirdId;

    /**
     * 异常三级原因名称
     */
    private String abnormalReasonThirdName;

    /** 质控异常类型ID */
    private Integer qcCode;

    /** 质控异常名称 */
    private String qcName;

    /** 是否生成分拣退货数据 */
    private boolean isSortingReturn;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 操作时间 */
    private Date operateTime;

    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;

    /**  数据库时间戳 */
    private Date ts;
    private String waveBusinessId;//版次号，路由系统的字段

    public AbnormalWayBillQuery() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getQcType() {
        return qcType;
    }

    public void setQcType(Integer qcType) {
        this.qcType = qcType;
    }

    public String getQcValue() {
        return qcValue;
    }

    public void setQcValue(String qcValue) {
        this.qcValue = qcValue;
    }

    public Long getAbnormalReasonFirstId() {
        return abnormalReasonFirstId;
    }

    public void setAbnormalReasonFirstId(Long abnormalReasonFirstId) {
        this.abnormalReasonFirstId = abnormalReasonFirstId;
    }

    public String getAbnormalReasonFirstName() {
        return abnormalReasonFirstName;
    }

    public void setAbnormalReasonFirstName(String abnormalReasonFirstName) {
        this.abnormalReasonFirstName = abnormalReasonFirstName;
    }

    public Long getAbnormalReasonSecondId() {
        return abnormalReasonSecondId;
    }

    public void setAbnormalReasonSecondId(Long abnormalReasonSecondId) {
        this.abnormalReasonSecondId = abnormalReasonSecondId;
    }

    public String getAbnormalReasonSecondName() {
        return abnormalReasonSecondName;
    }

    public void setAbnormalReasonSecondName(String abnormalReasonSecondName) {
        this.abnormalReasonSecondName = abnormalReasonSecondName;
    }

    public Long getAbnormalReasonThirdId() {
        return abnormalReasonThirdId;
    }

    public void setAbnormalReasonThirdId(Long abnormalReasonThirdId) {
        this.abnormalReasonThirdId = abnormalReasonThirdId;
    }

    public String getAbnormalReasonThirdName() {
        return abnormalReasonThirdName;
    }

    public void setAbnormalReasonThirdName(String abnormalReasonThirdName) {
        this.abnormalReasonThirdName = abnormalReasonThirdName;
    }

    public Integer getQcCode() {
        return qcCode;
    }

    public void setQcCode(Integer qcCode) {
        this.qcCode = qcCode;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public boolean isSortingReturn() {
        return isSortingReturn;
    }

    public void setSortingReturn(boolean sortingReturn) {
        isSortingReturn = sortingReturn;
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

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }
}
