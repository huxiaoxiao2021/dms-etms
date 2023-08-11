package com.jd.bluedragon.distribution.jy.task;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:06
 * @Description
 */
public class JyBizTaskSendAviationPlanQueryCondition extends JyBizTaskSendAviationPlanEntity implements Serializable {

    private static final long serialVersionUID = 4089383783438643445L;

    private List<Integer> startSiteIdList;


    public List<Integer> getStartSiteIdList() {
        return startSiteIdList;
    }

    public void setStartSiteIdList(List<Integer> startSiteIdList) {
        this.startSiteIdList = startSiteIdList;
    }
}
