package com.jd.bluedragon.common.dto.comboard.request;


import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.comboard.response.TableTrolleyDto;

import java.io.Serializable;
import java.util.List;

public class AddCTTReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 5658397633187805841L;
    /**
     * 混扫任务编号
     */
    private String templateCode;
    /**
     * 混扫任务名称
     */
    private String templateName;

    private List<TableTrolleyDto> tableTrolleyDtoList;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

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
