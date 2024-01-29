package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
import java.util.List;
public class GenerateBoxResp implements Serializable {
    private static final long serialVersionUID = -3488367551656755811L;

    private List<String>  boxCodes;

    public List<String> getBoxCodes() {
        return boxCodes;
    }

    public void setBoxCodes(List<String> boxCodes) {
        this.boxCodes = boxCodes;
    }
}
