package com.jd.bluedragon.distribution.jy.task;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/3 16:16
 * @Description
 */
public class JyBizTaskBindEntityQueryCondition extends JyBizTaskBindEntity{

    private List<String> bindDetailBizIdList;

    private Integer limit;


    public List<String> getBindDetailBizIdList() {
        return bindDetailBizIdList;
    }

    public void setBindDetailBizIdList(List<String> bindDetailBizIdList) {
        this.bindDetailBizIdList = bindDetailBizIdList;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
