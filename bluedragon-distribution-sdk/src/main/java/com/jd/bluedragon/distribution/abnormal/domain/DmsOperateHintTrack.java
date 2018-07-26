package com.jd.bluedragon.distribution.abnormal.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * Created by xumei3 on 2018/7/26.
 */
public class DmsOperateHintTrack extends DbEntity {

    private static final long serialVersionUID = 1L;

    public static final String OPERATE_NODE_INSPECTION="验货";
    public static final String OPERATE_NODE_SEND="发货";

    private String waybillCode;

    private Integer operateDmsCode;

    private String operateDmsName;

    private String operateNodeName;

    private Integer operateUserCode;

    private String operateUserErp;

    private String operateUserName;

    public Integer getOperateDmsCode() {
        return operateDmsCode;
    }

    public void setOperateDmsCode(Integer operateDmsCode) {
        this.operateDmsCode = operateDmsCode;
    }

    public String getOperateDmsName() {
        return operateDmsName;
    }

    public void setOperateDmsName(String operateDmsName) {
        this.operateDmsName = operateDmsName;
    }

    public String getOperateNodeName() {
        return operateNodeName;
    }

    public void setOperateNodeName(String operateNodeName) {
        this.operateNodeName = operateNodeName;
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
}
