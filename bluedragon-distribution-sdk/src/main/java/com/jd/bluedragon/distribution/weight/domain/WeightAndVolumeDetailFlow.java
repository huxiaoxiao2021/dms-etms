package com.jd.bluedragon.distribution.weight.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 称重量方明细实体
 *
 * @author: hujiping
 * @date: 2020/1/3 14:58
 */
public class WeightAndVolumeDetailFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 来源：
     * WeightSourceFromEnum
     * */
    private Integer sourceFrom;

    /**
     * 称重量方维度：
     * WeightOpeTypeEnum
     * 1、箱 2、运单 3、包裹
     * */
    private Integer weightType;

    /**
     * 操作类型：
     * OpeTypeEnum
     * */
    private Integer opeType;

    /**
     * 操作站点ID
     * */
    private Integer opeSiteCode;

    /**
     * 操作站点名称
     * */
    private String opeSiteName;

    /**
     * 操作人ID
     * */
    private Integer opeUserId;

    /**
     * 操作人ERP
     * */
    private String opeUserErp;

    /**
     * 操作人姓名
     * */
    private String opeUserName;

    /**
     * 操作单号：箱号、运单号、包裹号
     * */
    private String strCode;

    /**
     * 称重量方数据
     * */
    private List<WeightAndVolumeFlow> list;

    public Integer getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(Integer sourceFrom) {
        this.sourceFrom = sourceFrom;
    }

    public Integer getWeightType() {
        return weightType;
    }

    public void setWeightType(Integer weightType) {
        this.weightType = weightType;
    }

    public Integer getOpeType() {
        return opeType;
    }

    public void setOpeType(Integer opeType) {
        this.opeType = opeType;
    }

    public Integer getOpeSiteCode() {
        return opeSiteCode;
    }

    public void setOpeSiteCode(Integer opeSiteCode) {
        this.opeSiteCode = opeSiteCode;
    }

    public String getOpeSiteName() {
        return opeSiteName;
    }

    public void setOpeSiteName(String opeSiteName) {
        this.opeSiteName = opeSiteName;
    }

    public Integer getOpeUserId() {
        return opeUserId;
    }

    public void setOpeUserId(Integer opeUserId) {
        this.opeUserId = opeUserId;
    }

    public String getOpeUserErp() {
        return opeUserErp;
    }

    public void setOpeUserErp(String opeUserErp) {
        this.opeUserErp = opeUserErp;
    }

    public String getOpeUserName() {
        return opeUserName;
    }

    public void setOpeUserName(String opeUserName) {
        this.opeUserName = opeUserName;
    }

    public String getStrCode() {
        return strCode;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public List<WeightAndVolumeFlow> getList() {
        return list;
    }

    public void setList(List<WeightAndVolumeFlow> list) {
        this.list = list;
    }
}
