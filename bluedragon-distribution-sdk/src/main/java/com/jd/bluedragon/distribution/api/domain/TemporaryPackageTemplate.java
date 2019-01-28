package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.List;

public class TemporaryPackageTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private String temporaryTemplateName;

    private List<Integer> siteCodeList;

    public String getTemporaryTemplateName() {
        return temporaryTemplateName;
    }

    public void setTemporaryTemplateName(String temporaryTemplateName) {
        this.temporaryTemplateName = temporaryTemplateName;
    }

    public List<Integer> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<Integer> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }
}
