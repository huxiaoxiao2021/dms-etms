package com.jd.bluedragon.distribution.seal.domain;

import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 封车请求对象
 */
public class DmsSealVehicleRequest implements Serializable {

    private static final long serialVersionUID = -6113987558315120951L;

    /**
     * 分拣封车对象列表
     */
    private List<SealCarDto> sealCarList;

    /**
     * 运输封车对象列表
     */
    List<com.jd.etms.vos.dto.SealCarDto> tmsSealCarList;

    /**
     * 空批次集合
     */
    private Map<String, String> emptyBatchCode;

    /**
     * 操作信息对象
     */
    private OperatorData operatorData;

    /**
     * 业务来源
     */
    private String bizType;

    public List<SealCarDto> getSealCarList() {
        return sealCarList;
    }

    public void setSealCarList(List<SealCarDto> sealCarList) {
        this.sealCarList = sealCarList;
    }

    public List<com.jd.etms.vos.dto.SealCarDto> getTmsSealCarList() {
        return tmsSealCarList;
    }

    public void setTmsSealCarList(List<com.jd.etms.vos.dto.SealCarDto> tmsSealCarList) {
        this.tmsSealCarList = tmsSealCarList;
    }

    public Map<String, String> getEmptyBatchCode() {
        return emptyBatchCode;
    }

    public void setEmptyBatchCode(Map<String, String> emptyBatchCode) {
        this.emptyBatchCode = emptyBatchCode;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }
}
