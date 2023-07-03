package com.jd.bluedragon.common.dto.operation.workbench.strand;

import java.io.Serializable;
import java.util.List;

/**
 * 拣运app-滞留上报任务分页返回体
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportTaskPageResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分页返回体
     */
    private List<JyStrandReportTaskVO> list;

    /**
     * 总数
     */
    private Integer total;
    
    public List<JyStrandReportTaskVO> getList() {
        return list;
    }

    public void setList(List<JyStrandReportTaskVO> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
