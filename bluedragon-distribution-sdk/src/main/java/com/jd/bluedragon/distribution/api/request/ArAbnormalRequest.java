package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:14分
 */
public class ArAbnormalRequest extends JdRequest {

    /**
     * 用户ERP
     */
    private String userErp;

    /**
     * 转发类型  10 航空转陆运 、20 航空转铁路 、30 铁路转航空
     */
    private Integer transpondType;

    /**
     * 异常原因 10违禁品、20航班异常、30天气原因、40其他
     */
    private Integer transpondReason;

    /**
     * 违禁品原因
     */
    private Integer contrabandReason;

    /**
     * 包裹号、运单号、箱号、批次号
     */
    private String packageCode;

    /**
     * 运输方式变更是否取消发货标识，此标志存在于pda新老版本升级期间，老版本为null,新版本为1;
     */
    private Integer cancelType;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Integer getTranspondType() {
        return transpondType;
    }

    public void setTranspondType(Integer transpondType) {
        this.transpondType = transpondType;
    }

    public Integer getTranspondReason() {
        return transpondReason;
    }

    public void setTranspondReason(Integer transpondReason) {
        this.transpondReason = transpondReason;
    }

    public Integer getContrabandReason() {
        return contrabandReason;
    }

    public void setContrabandReason(Integer contrabandReason) {
        this.contrabandReason = contrabandReason;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCancelType() {
        return cancelType;
    }

    public void setCancelType(Integer cancelType) {
        this.cancelType = cancelType;
    }
}
