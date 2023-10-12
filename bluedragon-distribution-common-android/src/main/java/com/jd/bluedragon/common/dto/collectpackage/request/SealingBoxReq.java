package com.jd.bluedragon.common.dto.collectpackage.request;

import java.io.Serializable;
import java.util.List;

public class SealingBoxReq implements Serializable {
    private static final long serialVersionUID = -3640118777427248453L;
    
    private List<String> bizIdList;

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }
}
