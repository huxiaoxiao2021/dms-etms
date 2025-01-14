package com.jd.bluedragon.distribution.abnormalwaybill.domain;

import com.jd.bluedragon.distribution.api.domain.OperatorData;

import java.util.Date;

/**
 * PDA异常操作实体类
 * Created by shipeilin on 2017/11/17.
 */
public class AbnormalWayBill {

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
    /**
     * 操作流水表主键
     */
    private Long operateFlowId;
    /**
     * 操作信息对象
     */
    private OperatorData operatorData;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSortingReturn() {
        return isSortingReturn;
    }

    public void setSortingReturn(boolean sortingReturn) {
        isSortingReturn = sortingReturn;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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

    public String getAbnormalReasonFirstName() {
        return abnormalReasonFirstName;
    }

    public Long getAbnormalReasonSecondId() {
        return abnormalReasonSecondId;
    }

    public String getAbnormalReasonSecondName() {
        return abnormalReasonSecondName;
    }

    public Long getAbnormalReasonThirdId() {
        return abnormalReasonThirdId;
    }

    public String getAbnormalReasonThirdName() {
        return abnormalReasonThirdName;
    }

    public void setAbnormalReasonFirstId(Long abnormalReasonFirstId) {
        this.abnormalReasonFirstId = abnormalReasonFirstId;
    }

    public void setAbnormalReasonFirstName(String abnormalReasonFirstName) {
        this.abnormalReasonFirstName = abnormalReasonFirstName;
    }

    public void setAbnormalReasonSecondId(Long abnormalReasonSecondId) {
        this.abnormalReasonSecondId = abnormalReasonSecondId;
    }

    public void setAbnormalReasonSecondName(String abnormalReasonSecondName) {
        this.abnormalReasonSecondName = abnormalReasonSecondName;
    }

    public void setAbnormalReasonThirdId(Long abnormalReasonThirdId) {
        this.abnormalReasonThirdId = abnormalReasonThirdId;
    }

    public void setAbnormalReasonThirdName(String abnormalReasonThirdName) {
        this.abnormalReasonThirdName = abnormalReasonThirdName;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }

    public Long getOperateFlowId() {
        return operateFlowId;
    }

    public void setOperateFlowId(Long operateFlowId) {
        this.operateFlowId = operateFlowId;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }
}
