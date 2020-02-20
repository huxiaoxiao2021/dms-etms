package com.jd.bluedragon.distribution.operationLog.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录
 *
 * @author wangzichen
 */
public class OperationLog implements java.io.Serializable {

    private static final long serialVersionUID = -2128599558209868233L;

    /** 收货相关　 */
    public static final Integer LOG_TYPE_RECEIVE = 101; // 分拣中心收货

    /** 验货相关　 */
    public static final Integer LOG_TYPE_TRANSFER = 109; // 分拣中心交接
    public static final Integer LOG_TYPE_INSPECTION = 110; // 分拣中心验货
    public static final Integer LOG_TYPE_PARTNER_WAY_BILL = 111;// 运单号关联包裹信息

    /** POP打印相关　 */
    public static final Integer LOG_TYPE_POP_PRINT = 115;// POP打印相关

    /** 分拣相关　 */
    public static final Integer LOG_TYPE_SORTING = 120; // 分拣
    public static final Integer LOG_TYPE_SORTING_CANCEL = 121; // 取消分拣

    /** 发货发车相关　 */
    public static final Integer LOG_TYPE_DEPARTURE = 130; // 发车
    public static final Integer LOG_TYPE_SEND_DELIVERY = 131; // 发货
    public static final Integer LOG_TYPE_SEND_CANCELDELIVERY = 132; // 发货
    public static final Integer LOG_TYPE_SORTING_RET = 133; // 分拣退货
    public static final Integer LOG_TYPE_SORTING_INTERCEPT_LOG = 134; // 分拣拦截记录

    /** 逆向相关-售后 */
    public static final Integer TYPE_REVERSE_AMS_REJECT = 300; // 逆向售后驳回
    public static final Integer TYPE_REVERSE_AMS_REJECT_INSPECT = 301; // 逆向售后驳回验货
    public static final Integer TYPE_REVERSE_AMS_RECEIVE = 302; // 逆向售后收货
    public static final Integer TYPE_REVERSE_RECEIVE = 303; // 逆向收货

    /** 逆向相关-仓储 */
    public static final Integer TYPE_REVERSE_WMS_REJECT = 310; // 逆向仓储驳回
    public static final Integer TYPE_REVERSE_WMS_REJECT_INSPECT = 311; // 逆向仓储驳回验货
    public static final Integer TYPE_REVERSE_WMS_RECEIVE = 312; // 逆向仓储收货

    /** 逆向相关-备件库 */
    public static final Integer TYPE_REVERSE_SPWMS_REJECT = 320; // 逆向备件库驳回
    public static final Integer TYPE_REVERSE_SPWMS_REJECT_INSPECT = 321; // 逆向备件库驳回验货
    public static final Integer TYPE_REVERSE_SPWMS_RECEIVE = 322; // 逆向备件库收货


    /** 逆向相关-先货先款退款类型　 */
    public static final Integer LOG_TYPE_FASTREFUND = 400; // 先货先款退款类型

    /** 发货发车相关　 */
    public static final Integer LOG_TYPE_CAN_GLOBAL = 135; // 取消预装载

    /** 打印包裹标签日志添加　 */
    public static final Integer PRINT_PACKAGE = 136; // 打印包裹标签日志

    /** 分拣中心组板 **/
    public static final Integer BOARD_COMBINATITON = 166; // 分拣中心组板日志

    /** 分拣中心取消组板 **/
    public static final Integer BOARD_COMBINATITON_CANCEL = 167; // 分拣中心取消组板日志

    /** 集货相关　 */
    public static final Integer LOG_TYPE_COLLECT = 188;// 集货相关


    /**
     * 数据异常
     */
    public static final Integer DATA_EXCEPTION=54321;

    public Map<Integer, String> getLogTypeMap() {
        Map<Integer, String> logTypes = new HashMap<Integer, String>();
        logTypes.put(OperationLog.LOG_TYPE_TRANSFER, "交接");
        logTypes.put(OperationLog.LOG_TYPE_RECEIVE, "收货");
        logTypes.put(OperationLog.LOG_TYPE_INSPECTION, "验货");
        logTypes.put(OperationLog.LOG_TYPE_POP_PRINT, "打印");
        logTypes.put(OperationLog.LOG_TYPE_PARTNER_WAY_BILL, "运单号关联包裹信息");
        logTypes.put(OperationLog.LOG_TYPE_SORTING, "分拣");
        logTypes.put(OperationLog.LOG_TYPE_SORTING_CANCEL, "取消分拣");
        logTypes.put(OperationLog.LOG_TYPE_DEPARTURE, "发车");
        logTypes.put(OperationLog.LOG_TYPE_SEND_DELIVERY, "发货");
        logTypes.put(OperationLog.LOG_TYPE_SEND_CANCELDELIVERY, "取消发货");
        logTypes.put(OperationLog.TYPE_REVERSE_AMS_REJECT, "逆向售后驳回");
        logTypes.put(OperationLog.TYPE_REVERSE_AMS_REJECT_INSPECT, "逆向售后驳回验货");
        logTypes.put(OperationLog.TYPE_REVERSE_AMS_RECEIVE, "逆向售后收货");
        logTypes.put(OperationLog.TYPE_REVERSE_WMS_REJECT, "逆向仓储驳回");
        logTypes.put(OperationLog.TYPE_REVERSE_WMS_REJECT_INSPECT, "逆向仓储驳回验货");
        logTypes.put(OperationLog.TYPE_REVERSE_WMS_RECEIVE, "逆向仓储收货");
        logTypes.put(OperationLog.TYPE_REVERSE_SPWMS_REJECT, "逆向备件库驳回");
        logTypes.put(OperationLog.TYPE_REVERSE_SPWMS_REJECT_INSPECT, "逆向备件库驳回验货");
        logTypes.put(OperationLog.TYPE_REVERSE_SPWMS_RECEIVE, "逆向备件库收货");
        logTypes.put(OperationLog.TYPE_REVERSE_RECEIVE, "逆向收货");
        logTypes.put(OperationLog.LOG_TYPE_FASTREFUND, "先货先款退款类型");
        logTypes.put(OperationLog.LOG_TYPE_SORTING_INTERCEPT_LOG, "分拣拦截日志");
        logTypes.put(OperationLog.LOG_TYPE_CAN_GLOBAL, "取消预装载");
        logTypes.put(OperationLog.PRINT_PACKAGE, "包裹标签打印");
        logTypes.put(OperationLog.BOARD_COMBINATITON,"组板");
        logTypes.put(OperationLog.DATA_EXCEPTION,"数据异常");
        return logTypes;
    }

    /** 主键 */
    private Long logId;

    /** 箱号 */
    private String boxCode;

    /** 运单号 */
    private String waybillCode;

    /** 取件单号 */
    private String pickupCode;

    /** 包裹号 */
    private String packageCode;

    /** 机构 */
    private Integer orgCode;

    /** 操作日志类型 */
    private Integer logType;

    /** 批次号 */
    private String sendCode;

    /** 创建人code */
    private Integer createUserCode;

    /** 创建人姓名 */
    private String createUser;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    /** 备注 */
    private String remark;

    /** 操作时间 */
    private Date operateTime;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 记录日志的url */
    private String url;

    /** 记录日志的方法名 */
    private String methodName;

    /** 记录是否存在，1存在，0删除 */
    private Integer yn;

    public OperationLog() {
        super();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getLogId() {
        return this.logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPickupCode() {
        return this.pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public String getPackageCode() {
        return this.packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getLogType() {
        return this.logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getSendCode() {
        return this.sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Date getOperateTime() {
        return operateTime != null ? (Date) operateTime.clone() : null;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime != null ? (Date) operateTime.clone() : null;
    }

    public Date getCreateTime() {
        return createTime != null ? (Date) createTime.clone() : null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime != null ? (Date) createTime.clone() : null;
    }

    public Date getUpdateTime() {
        return updateTime != null ? (Date) updateTime.clone() : null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime != null ? (Date) updateTime.clone() : null;
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
