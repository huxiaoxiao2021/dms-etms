package com.jd.bluedragon.distribution.abnormal.domain;


import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * Created by xumei3 on 2018/7/26.
 */
public class DmsOperateHintTrack extends DbEntity {

    private static final long serialVersionUID = 1L;

    public static final String OPERATE_NODE_INSPECTION="验货";
    public static final String OPERATE_NODE_SEND="发货";

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 显示提示的分拣中心编码
     */
    private Integer hintDmsCode;

    /**
     * 显示提示的分拣中心名称
     */
    private String hintDmsName;

    /**
     * 显示提示的业务环节
     */
    private String hintOperateNode;

    /**
     * 处理提示的操作人编码
     */
    private Integer operateUserCode;

    /**
     * 处理提示的操作人erp
     */
    private String operateUserErp;

    /**
     * 处理提示的操作人姓名
     */
    private String operateUserName;

    /**
     * 显示提示的时间
     */
    private Date hintTime;

    public Integer getHintDmsCode() {
        return hintDmsCode;
    }

    public void setHintDmsCode(Integer hintDmsCode) {
        this.hintDmsCode = hintDmsCode;
    }

    public String getHintDmsName() {
        return hintDmsName;
    }

    public void setHintDmsName(String hintDmsName) {
        this.hintDmsName = hintDmsName;
    }

    public String getHintOperateNode() {
        return hintOperateNode;
    }

    public void setHintOperateNode(String hintOperateNode) {
        this.hintOperateNode = hintOperateNode;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Date getHintTime() {
        return hintTime;
    }

    public void setHintTime(Date hintTime) {
        this.hintTime = hintTime;
    }
}
