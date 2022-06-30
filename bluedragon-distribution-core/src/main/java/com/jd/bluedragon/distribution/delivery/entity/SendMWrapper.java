package com.jd.bluedragon.distribution.delivery.entity;

import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.send.domain.SendM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wyh
 * @className SendMWrapper
 * @description 发货包装类
 * @date 2021/8/9 11:04
 **/
public class SendMWrapper implements Serializable {

    private static final long serialVersionUID = 529360445847131744L;

    /**
     * 发货主表公共属性
     */
    private SendM sendM;

    /**
     * 前端扫描的单号类型
     */
    private SendKeyTypeEnum keyType;

    /**
     * 扫描的单号列表. SendM只提取公共字段
     */
    private List<String> barCodeList;

    /**
     * 批次任务唯一标识
     */
    private String batchUniqKey;
    private String newSendCode;

    /**
     * 运单号
     */
    private String waybillCode;

    private int pageNo;

    private int pageSize;

    private int totalPage;

    public SendMWrapper() {

    }

    public String getNewSendCode() {
        return newSendCode;
    }

    public void setNewSendCode(String newSendCode) {
        this.newSendCode = newSendCode;
    }

    public SendMWrapper(SendKeyTypeEnum keyType) {
        this.keyType = keyType;
    }

    public SendKeyTypeEnum getKeyType() {
        return keyType;
    }

    public void setKeyType(SendKeyTypeEnum keyType) {
        this.keyType = keyType;
    }

    public SendM getSendM() {
        return sendM;
    }

    public void setSendM(SendM sendM) {
        this.sendM = sendM;
    }

    public List<String> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<String> barCodeList) {
        this.barCodeList = barCodeList;
    }

    public void add(String barCode) {
        if (null == barCodeList || barCodeList.size() == 0) {
            barCodeList = new ArrayList<>();
        }
        barCodeList.add(barCode);
    }

    public String getBatchUniqKey() {
        return batchUniqKey;
    }

    public void setBatchUniqKey(String batchUniqKey) {
        this.batchUniqKey = batchUniqKey;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
