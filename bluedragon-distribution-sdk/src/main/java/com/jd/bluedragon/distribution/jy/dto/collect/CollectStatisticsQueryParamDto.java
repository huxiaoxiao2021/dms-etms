package com.jd.bluedragon.distribution.jy.dto.collect;

import com.jd.bluedragon.distribution.jy.dto.unload.UnloadBaseDto;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //集齐查询参数请求bean
 * @date
 **/
public class CollectStatisticsQueryParamDto extends UnloadBaseDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String bizId;
    private String waybillCode;
    /**
     * 集齐类型
     * CollectTypeEnum
     */
    private Integer collectType;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }
}
