package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/17
 * @Description:  虚拟入备件库请求，仅用于入备件库小工具使用
 */
public class SpWmsCreateInRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号（逆向单 JDT）
     */
    private String waybillCode;

    /**
     * 商品信息（商品编码与备件条码对应关系）
     */
    private List<SpWmsCreateInProduct> spareCodes;

    /**
     * 收货的备件库编码（对应青龙基础资料中的纯数字编码 例如 沈阳3C逆向库 是 25026）
     *
     * 收货场地
     */
    private Integer spWmsCode;

    /**
     * 虚拟退货节点的退货组（对应青龙基础资料中的纯数字编码 例如 北京大兴退货组 是 733578）
     *
     * 发货场地 有就给 没有就不用给了
     */
    private Integer rdWmsCode;

    /**
     * 操作人编码 （对应青龙基础资料中的纯数字人员编码）
     *
     * 用于写备件库收货全程跟踪 有就给 没有就不用给了
     */
    private Integer opeUserCode;

    /**
     * 操作时间
     */
    private Date opeTime;

    private String sendCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public List<SpWmsCreateInProduct> getSpareCodes() {
        return spareCodes;
    }

    public void setSpareCodes(List<SpWmsCreateInProduct> spareCodes) {
        this.spareCodes = spareCodes;
    }

    public Integer getSpWmsCode() {
        return spWmsCode;
    }

    public void setSpWmsCode(Integer spWmsCode) {
        this.spWmsCode = spWmsCode;
    }

    public Integer getRdWmsCode() {
        return rdWmsCode;
    }

    public void setRdWmsCode(Integer rdWmsCode) {
        this.rdWmsCode = rdWmsCode;
    }

    public Integer getOpeUserCode() {
        return opeUserCode;
    }

    public void setOpeUserCode(Integer opeUserCode) {
        this.opeUserCode = opeUserCode;
    }

    public Date getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(Date opeTime) {
        this.opeTime = opeTime;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }
}
