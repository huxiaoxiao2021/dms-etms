package com.jd.bluedragon.distribution.send.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DmsToTmsService 收货接口输入参数，该对象相当于批次信息
 */
@XmlRootElement(name = "ShouHuoInfo")
public class ShouHuoInfo {
    /**
     * 批次号
     */
    private String batchId;
    /**
     * 防重码
     */
    private String uuId;
    /**
     * 接收时间
     */
    private Date createTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 调用代码，用来统计调用方身份，必填
     */
    private String callCode;
    
    private String eos;
    /**
     * 包裹信息
     */
    private List<BoxInfo> boxInfoList = new ArrayList<BoxInfo>();
    
    /**
     * 返回代码，用来事务调用时返回的结果
     */
    private boolean backCode;
    
    private String carNo;
    
    public String getBatchId() {
        return this.batchId;
    }
    
    public List<BoxInfo> getBoxInfoList() {
        return this.boxInfoList;
    }
    
    public String getCallCode() {
        return this.callCode;
    }
    
    public String getCarNo() {
        return this.carNo;
    }
    
    public Date getCreateTime() {
    	return this.createTime == null ? null : (Date) this.createTime.clone();
    }
    
    public String getRemark() {
        return this.remark;
    }
    
    public String getUuId() {
        return this.uuId;
    }
    
    public boolean isBackCode() {
        return this.backCode;
    }
    
    public void setBackCode(boolean backCode) {
        this.backCode = backCode;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    public void setBoxInfoList(List<BoxInfo> boxInfoList) {
        this.boxInfoList = boxInfoList;
    }
    
    public void setCallCode(String callCode) {
        this.callCode = callCode;
    }
    
    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }
    
    public void setCreateTime(Date createTime) {
    	this.createTime = createTime == null ? null : (Date) createTime.clone();
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public void setUuId(String uuId) {
        this.uuId = uuId;
    }
    
    public String getEos() {
        return this.eos;
    }
    
    public void setEos(String eos) {
        this.eos = eos;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        
        ShouHuoInfo shouHuo = (ShouHuoInfo) obj;
        if (this.batchId == null) {
            return this.batchId == shouHuo.batchId;
        } else {
            return this.batchId.equals(shouHuo.batchId);
        }
    }
    
    @Override
    public int hashCode() {
        return this.batchId.hashCode();
    }
}
