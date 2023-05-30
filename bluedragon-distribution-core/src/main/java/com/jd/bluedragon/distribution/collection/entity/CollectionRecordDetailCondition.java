package com.jd.bluedragon.distribution.collection.entity;

/**
 * @Author zhengchengfa
 * @Date 2023/5/30 20:16
 * @Description
 */
public class CollectionRecordDetailCondition extends CollectionRecordDetailPo{

    private Integer pageSize;

    private Integer offset;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
