package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * @author wuyoude
 * @ClassName: ArSendRegister
 * @Description: 发货登记表-实体类
 * @date 2017年12月28日 09:46:12
 */
public class ArSendRegister extends DbEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 状态,1-已发货,2-已提取
     */
    private Integer status;

    /**
     * 运输类型，1-航空，2-铁路
     */
    private Integer transportType;

    /**
     * 航空单号/铁路单号
     */
    private String orderCode;

    /**
     * 运力名称 航班号/铁路车次号
     */
    private String transportName;

    /**
     * 铁路站序
     */
    private String siteOrder;

    /**
     * 发货日期
     */
    private Date sendDate;

    /**
     * 航空公司编号/铁路担当局编号
     */
    private String transCompanyCode;

    /**
     * 航空公司/铁路担当局名称
     */
    private String transCompany;

    /**
     * 起飞城市
     */
    private String startCityName;

    /**
     * 起飞城市编号
     */
    private Integer startCityId;

    /**
     * 落地城市
     */
    private String endCityName;

    /**
     * 落地城市编号
     */
    private Integer endCityId;

    /**
     * 起飞机场
     */
    private String startStationName;

    /**
     * 起飞机场编号
     */
    private String startStationId;

    /**
     * 落地机场
     */
    private String endStationName;

    /**
     * 落地机场编号
     */
    private String endStationId;

    /**
     * 预计起飞时间
     */
    private Date planStartTime;

    /**
     * 预计落地时间
     */
    private Date planEndTime;

    /**
     * 时效（跨越天数）
     */
    private Integer aging;

    /**
     * 发货件数
     */
    private Integer sendNum;

    /**
     * 计费重量
     */
    private BigDecimal chargedWeight;

    /**
     * 发货备注
     */
    private String remark;

    /**
     * 摆渡车型
     */
    private Integer shuttleBusType;

    /**
     * 摆渡车牌号
     */
    private String shuttleBusNum;

    /**
     * 操作人ERP
     */
    private String operatorErp;

    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作部门
     */
    private String operationDept;

    /**
     * 操作部门编号
     */
    private Integer operationDeptCode;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 修改用户
     */
    private String updateUser;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 该条记录是否已经发过路由MQ
     * 1、未发过，2、已发过
     */
    private Integer sendRouterMqType;

    /**
     * 批次号，对应数据库无映射关系
     */
    private String sendCode;

    /**
     * 批次号列表
     */
    private List<String> sendCodes;

    /**
     * 批次号对应发货包裹数列表
     */
    private List<Integer> packCounts;

    /**
     * 货物类型
     */
    private String goodsType;

    public Integer getSendRouterMqType() {
        return sendRouterMqType;
    }

    public void setSendRouterMqType(Integer sendRouterMqType) {
        this.sendRouterMqType = sendRouterMqType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getSiteOrder() {
        return siteOrder;
    }

    public void setSiteOrder(String siteOrder) {
        this.siteOrder = siteOrder;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getTransCompanyCode() {
        return transCompanyCode;
    }

    public void setTransCompanyCode(String transCompanyCode) {
        this.transCompanyCode = transCompanyCode;
    }

    public String getTransCompany() {
        return transCompany;
    }

    public void setTransCompany(String transCompany) {
        this.transCompany = transCompany;
    }

    public String getStartCityName() {
        return startCityName;
    }

    public void setStartCityName(String startCityName) {
        this.startCityName = startCityName;
    }

    public Integer getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Integer startCityId) {
        this.startCityId = startCityId;
    }

    public String getEndCityName() {
        return endCityName;
    }

    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public void setStartStationId(String startStationId) {
        this.startStationId = startStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public void setEndStationId(String endStationId) {
        this.endStationId = endStationId;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Integer getAging() {
        return aging;
    }

    public void setAging(Integer aging) {
        this.aging = aging;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public BigDecimal getChargedWeight() {
        return chargedWeight;
    }

    public void setChargedWeight(BigDecimal chargedWeight) {
        this.chargedWeight = chargedWeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getShuttleBusType() {
        return shuttleBusType;
    }

    public void setShuttleBusType(Integer shuttleBusType) {
        this.shuttleBusType = shuttleBusType;
    }

    public String getShuttleBusNum() {
        return shuttleBusNum;
    }

    public void setShuttleBusNum(String shuttleBusNum) {
        this.shuttleBusNum = shuttleBusNum;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperationDept() {
        return operationDept;
    }

    public void setOperationDept(String operationDept) {
        this.operationDept = operationDept;
    }

    public Integer getOperationDeptCode() {
        return operationDeptCode;
    }

    public void setOperationDeptCode(Integer operationDeptCode) {
        this.operationDeptCode = operationDeptCode;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
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

    public List<Integer> getPackCounts() {
        return packCounts;
    }

    public void setPackCounts(List<Integer> packCounts) {
        this.packCounts = packCounts;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }
}
