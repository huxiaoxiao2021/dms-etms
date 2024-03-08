package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class SealingBoxReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -3640118777427248453L;
    
    private List<SealingBoxDto> sealingBoxDtoList;

    public List<SealingBoxDto> getSealingBoxDtoList() {
        return sealingBoxDtoList;
    }

    public void setSealingBoxDtoList(List<SealingBoxDto> sealingBoxDtoList) {
        this.sealingBoxDtoList = sealingBoxDtoList;
    }
}
