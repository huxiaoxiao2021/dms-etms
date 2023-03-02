package com.jd.bluedragon.distribution.jy.dto.send;


import com.jd.bluedragon.distribution.jy.dto.JyLabelOption;

import java.io.Serializable;
import java.util.List;


public class SendPackage implements Serializable {


    private static final long serialVersionUID = -7907707159294103521L;
    /**
     * 单号
     */
    private String packageCode;

    /**
     * 单号标签集合
     */
    private List<JyLabelOption> tags;


    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public List<JyLabelOption> getTags() {
        return tags;
    }

    public void setTags(List<JyLabelOption> tags) {
        this.tags = tags;
    }
}
