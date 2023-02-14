package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyDto;

import java.io.Serializable;
import java.util.List;

public class CreateGroupCTTReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 4621182571302366428L;
    private List<TableTrolleyDto> tableTrolleyDtoList;
    /**
     * 混扫任务名称：【混扫01】
     */
    private String templateName;

    public List<TableTrolleyDto> getTableTrolleyDtoList() {
        return tableTrolleyDtoList;
    }

    public void setTableTrolleyDtoList(List<TableTrolleyDto> tableTrolleyDtoList) {
        this.tableTrolleyDtoList = tableTrolleyDtoList;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
