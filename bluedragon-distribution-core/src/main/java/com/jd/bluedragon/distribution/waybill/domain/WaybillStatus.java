package com.jd.bluedragon.distribution.waybill.domain;

import java.util.Date;

public class WaybillStatus {

    public static final int RESULT_CODE_PARAM_IS_NULL = 10001;
    public static final int RESULT_CODE_REPEAT_TASK = 10003;

    public static final Integer WAYBILL_STATUS_CODE_FORWARD_INSPECTION = 0; //正向验货
    public static final Integer WAYBILL_STATUS_CODE_FORWARD_SORTING = 1;  //正向分拣
    public static final Integer WAYBILL_STATUS_CODE_POP_InFactory = 1150; //驻场验货

    public static final Integer WAYBILL_STATUS_CODE_REVERSE_INSPECTION = 30; //逆向验货
    public static final Integer WAYBILL_STATUS_CODE_REVERSE_SORTING = 40; //逆向分拣
    public static final Integer WAYBILL_STATUS_CODE_REVERSE_DELIVERY = 50;
    public static final Integer WAYBILL_STATUS_CODE_FORWORD_DELIVERY = 2; //正向发货

    /*退货完成 全收标志*/
    public static final Integer WAYBILL_RETURN_COMPLETE_FLAG_ALL = 1;
    /*退货完成 部分收标志*/
    public static final Integer WAYBILL_RETURN_COMPLETE_FLAG_HALF = 2;
    /*退货完成 收货标识扩展字段属性名*/
    public static final String WAYBILL_RETURN_FLAG_NAME = "returnFlag";


    public static final Integer WAYBILL_STATUS_CODE_REVERSE_SORTING_RETURN = 60;

    public static final Integer WAYBILL_STATUS_CODE_FORWARD_INSPECTION_THIRDPARTY = 80;
    
    public static final Integer WAYBILL_STATUS_CODE_DEPARTURE_THIRDPARTY = 300;

    public static final Integer WAYBILL_TRACK_FC = 150;
    public static final Integer WAYBILL_TRACK_RCD = 160;
    public static final Integer WAYBILL_TRACK_PACKAGE_HALF = 600; //包裹半收完成
    public static final Integer WAYBILL_TRACK_SH = 400; //正向收货
    public static final Integer WAYBILL_TRACK_REVERSE_SH = 500; //逆向收货
    
    /**仓储收货驳回**/ 
    public static final Integer WAYBILL_TRACK_BH = 900;
    /**仓储驳回分拣中心收货**/ 
    public static final Integer WAYBILL_TRACK_BHS = 1100;
    /**仓储收货确认**/ 
    public static final Integer WAYBILL_TRACK_SHREVERSE = 800;
    
    /**仓储 备件库 售后收货确认修改为，回传退货完成状态 16-1-7**/ 
    public static final Integer WAYBILL_STATUS_SHREVERSE = 4300;
    /**仓储 备件库 售后收货确认修改为，回传退货完成状态 16-1-7**/
    public static final Integer WAYBILL_STATUS_JJREVERSE = 4400;
    
    /**备件库售后取件交接拆包--驳回**/ 
    public static final Integer WAYBILL_TRACK_AMS_BH = 901;
    /**备件库售后取件交接拆包--确认**/ 
    public static final Integer WAYBILL_TRACK_AMS_SHREVERSE = 1901;
    
    
    /**新发车全称跟踪类型-运输车辆出发**/ 
    public static final Integer WAYBILL_TRACK_XFC = 1800;
    /**发车收货全称跟踪类型-运输车辆到达**/ 
    public static final Integer WAYBILL_TRACK_FCS = 1900;

    /**配送异常节点全程跟踪类型*/
    public static final Integer WAYBILL_TRACK_QC = 2100;
    /**
     * 空铁转陆运 全程跟踪节点
     */
    public static final Integer WAYBILL_TRACK_ARQC = 10000;
    /**
     * 全程跟踪消息类型mstType-仓储收货确认
     **/
    public static final Integer WAYBILL_TRACK_MSGTYPE_CCSHQR = 1800;
    public static final String WAYBILL_TRACK_MSGTYPE_CCSHQR_MSG = "仓储收货确认";

    /**
     * 全程跟踪消息类型mstType-仓储收货驳回
     **/
    public static final Integer WAYBILL_TRACK_MSGTYPE_CCSHBH = 1900;
    public static final String WAYBILL_TRACK_MSGTYPE_CCSHBH_MSG = "仓储收货驳回";

    /**
     * 全程跟踪消息类型mstType-运单修改补打
     **/
    public static final Integer WAYBILL_TRACK_MSGTYPE_UPDATE = 2400;
    public static final String WAYBILL_TRACK_MSGTYPE_UPDATE_MSG = "运单修改补打";
    public static final String WAYBILL_TRACK_MSGTYPE_UPDATE_CONTENT = "温馨提示：您的订单因信息修改，正在重新中转";

    /**
     * 全程跟踪消息类型mstType-运单修改补打
     **/
    public static final Integer WAYBILL_TRACK_MSGTYPE_PACK_REPRINT = 1000;

    /**
     * 逆向换单打印
     */
    public static final Integer WAYBILL_TRACK_REVERSE_PRINT=1700;

    public static final Integer WAYBILL_TRACK_SITE_LABEL_PRINT=2900;
    
    /**
     * 封车
     */
    public static final Integer WAYBILL_TRACK_SEAL_VEHICLE = 2200;
    
    /**
     * 解封车
     */
    public static final Integer WAYBILL_TRACK_UNSEAL_VEHICLE = 2300;
    
    /**
     * 撤销封车
     */
    public static final Integer WAYBILL_TRACK_CANCEL_VEHICLE = 4200;
    
    /**
     * 全程跟踪消息类型mstType-仓储收货确认
     **/
    public static final Integer WAYBILL_TRACK_CANCLE_LOADBILL = 2222;
    public static final String WAYBILL_TRACK_CANCLE_LOADBILLMSG = "取消预装载全程跟踪";
    /**
     * 全程跟踪-空铁提货
     */
    public static final Integer WAYBILL_TRACK_AR_RECEIVE = 4900;

    /**
     * 全程跟踪-空铁发货登记
     */
    public static final Integer WAYBILL_TRACK_AR_SEND_REGISTER = 6700;

    /**
     * 全程跟踪-配送员上门收货
     */
    public static final Integer WAYBILL_TRACK_UP_DELIVERY = 700;

    /**
     * 全程跟踪-配送员完成揽收
     */
    public static final Integer WAYBILL_TRACK_COMPLETE_DELIVERY = 5400;

    /**
     * 全称跟踪-组板
     */
    public static final Integer WAYBILL_TRACK_BOARD_COMBINATION = 7000;

    /**
     * 全称跟踪-取消组板
     */
    public static final Integer WAYBILL_TRACK_BOARD_COMBINATION_CANCEL = 7600;

    /**
     * 全称跟踪-转网
     */
    public static final Integer WAYBILL_TRACK_WAYBILL_TRANSFER = 13600;
    public static final String WAYBILL_TRACK_MESSAGE_WAYBILL_TRANSFER_B2C = "已成功转成C网运单";


    /**
     * POP打印
     */
    public static final Integer WAYBILL_TRACK_POP_PRINT= 1200;
    public static final String WAYBILL_TRACK_POP_PRINT_STATE= "-250";
    /**
     * 妥投 操作码
     */
    public static final Integer WAYBILL_OPE_TYPE_DELIVERED= 8;
    /**
     * 拒收 操作码
     */
    public static final Integer WAYBILL_OPE_TYPE_REJECT= 19;
    /**
     * 部分签收 操作码
     */
    public static final Integer WAYBILL_OPE_TYPE_HALF_SIGNIN= 7500;

    /**
     * 暂存上架 操作码
     */
    public static final Integer WAYBILL_OPE_TYPE_PUTAWAY= 8400;

    /**
     * 审核完成状态
     */
    public static final Integer WAYBILL_STATUS_CONSULT = 135;

    /**
     *  签单返回合单
     * */
    public static final Integer WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN = 8700;
    public static final Integer WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_OLD = 8701;
    public static final Integer WAYBILL_STATUS_MERGE_WAYBILLCODE_RETURN_NEW = 8702;

    /**
     * 全称跟踪-取消建箱
     */
    public static final Integer WAYBILL_TRACK_SORTING_CANCEL = 13400;

    /**
     * 全称跟踪-取消发货
     */
    public static final Integer WAYBILL_TRACK_SEND_CANCEL = 3800;

    /**
     * 全称跟踪-揽收交接
     */
    public static final Integer WAYBILL_TRACK_RECEIVE_HANDOVERS = 14300;

    /**
     * 全称跟踪-派送交接
     */
    public static final Integer WAYBILL_TRACK_SEND_HANDOVERS = 14400;

    private Long id;

    private String sendCode;
    private String boxCode;
    private String waybillCode;
    private String packageCode;

    private Integer orgId;
    private String orgName;

    private Integer createSiteCode;
    private Integer createSiteType;
    private String createSiteName;

    private Integer receiveSiteCode;
    private Integer receiveSiteType;
    private String receiveSiteName;

    private Integer operatorId;
    private String operator;
    private Integer operateType;
    private Date operateTime;

    private Integer reasonId;

    private Integer returnFlag;
    private String remark;

    /**
     * 返单号
     */
    private String returnWaybillCode;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSendCode() {
        return this.sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
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

    public String getPackageCode() {
        return this.packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOrgId() {
        return this.orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getCreateSiteType() {
        return this.createSiteType;
    }

    public void setCreateSiteType(Integer createSiteType) {
        this.createSiteType = createSiteType;
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

    public Integer getReceiveSiteType() {
        return this.receiveSiteType;
    }

    public void setReceiveSiteType(Integer receiveSiteType) {
        this.receiveSiteType = receiveSiteType;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getOperateType() {
        return this.operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Date getOperateTime() {
        return this.operateTime == null ? null : (Date) this.operateTime.clone();
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime == null ? null : (Date) operateTime.clone();
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public Integer getReturnFlag() {
        return returnFlag;
    }

    public void setReturnFlag(Integer returnFlag) {
        this.returnFlag = returnFlag;
    }

    public String getReturnWaybillCode() {
        return returnWaybillCode;
    }

    public void setReturnWaybillCode(String returnWaybillCode) {
        this.returnWaybillCode = returnWaybillCode;
    }
}
