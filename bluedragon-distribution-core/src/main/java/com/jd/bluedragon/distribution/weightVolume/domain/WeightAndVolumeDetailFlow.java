package com.jd.bluedragon.distribution.weightVolume.domain;

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
    private String sourceFrom;

    /**
     * 操作类型：
     * OpeTypeEnum
     * */
    private String opeType;

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
     * 称重量方数据
     * */
    private List<WeightAndVolumeFlow> list;

    public String getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(String sourceFrom) {
        this.sourceFrom = sourceFrom;
    }

    public String getOpeType() {
        return opeType;
    }

    public void setOpeType(String opeType) {
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

    public List<WeightAndVolumeFlow> getList() {
        return list;
    }

    public void setList(List<WeightAndVolumeFlow> list) {
        this.list = list;
    }
}
