package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * 发货封车任务响应
 **/
public class AviationRailwaySendSealTaskData<T> implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    /**
     * 任务状态
     */
    private Integer taskStatus;
    private Integer taskStatusName  ;


    /**
     * 车辆数据
     */
    private List<T> data;



    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
