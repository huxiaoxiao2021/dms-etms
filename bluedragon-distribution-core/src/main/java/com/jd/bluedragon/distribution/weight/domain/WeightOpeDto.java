package com.jd.bluedragon.distribution.weight.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 终端称重量方对象
 * @link https://cf.jd.com/pages/viewpage.action?pageId=195474766
 * Created by yangjun8 on 2019/7/10.
 */
public class WeightOpeDto implements Serializable {

    /**
     * serialVersionUID : 序列化ID
     */
    private static final long serialVersionUID = 1L;

    private String operateCode;//运单号/包裹号
    private Integer codeType;//单号类型 运单号：1/包裹号：2
    private Integer operateUserId;//操作人ID
    private String operateUser;//操作人姓名
    private Long operateTime;//打包时间
    private Integer operateSiteId;//站点id
    private String operateSite;//站点名称
    private Integer opeType;//操作类型 3.加盟站点操作 4.揽收交接操作 5.配送交接操作
    private Double volumeHeight;//包裹高
    private Double volumeLength;//包裹长
    private Double volumeWidth;//包裹宽
    private Double volume;//包裹体积
    private Double weight;//包裹重量

    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
    }

    public Integer getCodeType() {
        return codeType;
    }

    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSite() {
        return operateSite;
    }

    public void setOperateSite(String operateSite) {
        this.operateSite = operateSite;
    }

    public Integer getOpeType() {
        return opeType;
    }

    public void setOpeType(Integer opeType) {
        this.opeType = opeType;
    }

    public Double getVolumeHeight() {
        return volumeHeight;
    }

    public void setVolumeHeight(Double volumeHeight) {
        this.volumeHeight = volumeHeight;
    }

    public Double getVolumeLength() {
        return volumeLength;
    }

    public void setVolumeLength(Double volumeLength) {
        this.volumeLength = volumeLength;
    }

    public Double getVolumeWidth() {
        return volumeWidth;
    }

    public void setVolumeWidth(Double volumeWidth) {
        this.volumeWidth = volumeWidth;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
