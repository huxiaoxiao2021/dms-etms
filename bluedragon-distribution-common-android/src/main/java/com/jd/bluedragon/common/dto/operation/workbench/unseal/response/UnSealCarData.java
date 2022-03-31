package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ToSealCarList
 * @Description
 * @Author wyh
 * @Date 2022/3/3 13:57
 **/
public class UnSealCarData<T> implements Serializable {

    private static final long serialVersionUID = -7090184885072826427L;

    /**
     * 车辆数据
     */
    private List<T> data;

    /**
     * 车辆类型统计
     */
    private List<LineTypeStatis> lineStatistics;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<LineTypeStatis> getLineStatistics() {
        return lineStatistics;
    }

    public void setLineStatistics(List<LineTypeStatis> lineStatistics) {
        this.lineStatistics = lineStatistics;
    }
}
