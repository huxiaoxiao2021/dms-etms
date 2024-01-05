package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.List;

public class JyPickingTaskBatchUpdateDto implements Serializable {
    private static final long serialVersionUID = 6564607030422596333L;

    private List<String> bizIdList;

    private Integer status;

    private Integer completeNode;

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCompleteNode() {
        return completeNode;
    }

    public void setCompleteNode(Integer completeNode) {
        this.completeNode = completeNode;
    }
}
