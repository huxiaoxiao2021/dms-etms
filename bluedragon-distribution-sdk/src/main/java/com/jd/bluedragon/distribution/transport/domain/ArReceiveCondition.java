package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;
import java.util.List;

/**
 * 计提查询空铁提供参数
 */
public class ArReceiveCondition {

    /**
     * 开始操作时间
     */
    private Date operateTimeStart;

    /**
     * 结束操作时间
     */
    private Date operateTimeEnd;

    /**
     * 操作站点ID
     */
    private Integer createSiteCode;

    /**
     * 包裹/箱号列表
     */
    private List<String> barCodeList;

    /**
     * 创建人编号列表
     */
    private List<Integer> createUserCodeList;

    private Integer offset;

    private Integer size;

    public Date getOperateTimeStart() {
        return operateTimeStart;
    }

    public void setOperateTimeStart(Date operateTimeStart) {
        this.operateTimeStart = operateTimeStart;
    }

    public Date getOperateTimeEnd() {
        return operateTimeEnd;
    }

    public void setOperateTimeEnd(Date operateTimeEnd) {
        this.operateTimeEnd = operateTimeEnd;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public List<String> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<String> barCodeList) {
        this.barCodeList = barCodeList;
    }

    public List<Integer> getCreateUserCodeList() {
        return createUserCodeList;
    }

    public void setCreateUserCodeList(List<Integer> createUserCodeList) {
        this.createUserCodeList = createUserCodeList;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
