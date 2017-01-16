package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.framework.TaskExecuteContext;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;

import java.util.List;

/**
 * Created by wangtingwei on 2017/1/16.
 */
public class InspectionTaskExecuteContext extends TaskExecuteContext {
    /**
     * 验货数据列表
     */
    private List<Inspection> inspectionList;


    public List<Inspection> getInspectionList() {
        return inspectionList;
    }

    public void setInspectionList(List<Inspection> inspectionList) {
        this.inspectionList = inspectionList;
    }
}
