package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xumei3 on 2018/5/10.
 */
public class SysConfigContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean masterSwitch;
    private List<Integer> siteCodes;

    public Boolean getMasterSwitch() {
        return masterSwitch;
    }

    public void setMasterSwitch(Boolean masterSwitch) {
        this.masterSwitch = masterSwitch;
    }

    public List<Integer> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<Integer> siteCodes) {
        this.siteCodes = siteCodes;
    }

    public String toString(){
        return "masterSwitch:" + masterSwitch + ",siteCodes:" + siteCodes;
    }
}
