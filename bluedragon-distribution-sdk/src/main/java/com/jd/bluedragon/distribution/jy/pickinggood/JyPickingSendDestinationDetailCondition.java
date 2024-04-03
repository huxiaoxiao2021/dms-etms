package com.jd.bluedragon.distribution.jy.pickinggood;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2024/4/2 20:59
 * @Description
 */
public class JyPickingSendDestinationDetailCondition extends JyPickingSendDestinationDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 批次号集合
     */
    private List<String> sendCodeList;

    //偏移量
    private Integer offset;
    //页容量
    private Integer pageSize;
    //完成时间查询开始时间
    private Date completeTimeRangeStart;
    //完成时间查询结束时间
    private Date completeTimeRangeEnd;

    public List<String> getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(List<String> sendCodeList) {
        this.sendCodeList = sendCodeList;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Date getCompleteTimeRangeStart() {
        return completeTimeRangeStart;
    }

    public void setCompleteTimeRangeStart(Date completeTimeRangeStart) {
        this.completeTimeRangeStart = completeTimeRangeStart;
    }

    public Date getCompleteTimeRangeEnd() {
        return completeTimeRangeEnd;
    }

    public void setCompleteTimeRangeEnd(Date completeTimeRangeEnd) {
        this.completeTimeRangeEnd = completeTimeRangeEnd;
    }
}
