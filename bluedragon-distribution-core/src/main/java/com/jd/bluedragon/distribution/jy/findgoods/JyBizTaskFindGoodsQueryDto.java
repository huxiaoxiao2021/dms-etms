package com.jd.bluedragon.distribution.jy.findgoods;

import java.util.Date;

public class JyBizTaskFindGoodsQueryDto extends JyBizTaskFindGoods {

    private Integer pageNo;
    private Integer offset;
    private Integer pageSize;
    /**
     * 按时间查询时
     */
    private Date createTimeBegin;


    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
