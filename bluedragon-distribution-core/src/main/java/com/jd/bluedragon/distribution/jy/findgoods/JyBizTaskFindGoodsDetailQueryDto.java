package com.jd.bluedragon.distribution.jy.findgoods;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/7/17 16:49
 * @Description
 */
public class JyBizTaskFindGoodsDetailQueryDto extends JyBizTaskFindGoodsDetail{

    private List<Integer> statusList;

    private Integer offset;
    private Integer pageSize;


    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
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
}
