package com.jd.bluedragon.common.dto.operation.workbench.strand;

import java.io.Serializable;

/**
 * 拣运app-滞留上报已扫明细分页列表请求体
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportScanPageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务编码
     */
    private String bizId;
    
    private Integer offset;

    private Integer pageNum;

    private Integer pageSize;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
