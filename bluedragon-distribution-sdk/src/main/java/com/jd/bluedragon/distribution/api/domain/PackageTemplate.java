package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.List;

public class PackageTemplate implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 正式版本的模板名称
     */
    private String releaseTemplateName;

    /**
     * 适用版本的模板及适用站点
     */
    private List<TemporaryPackageTemplate> temporaryTemplate ;

    public String getReleaseTemplateName() {
        return releaseTemplateName;
    }

    public void setReleaseTemplateName(String releaseTemplateName) {
        this.releaseTemplateName = releaseTemplateName;
    }

    public List<TemporaryPackageTemplate> getTemporaryTemplate() {
        return temporaryTemplate;
    }

    public void setTemporaryTemplate(List<TemporaryPackageTemplate> temporaryTemplate) {
        this.temporaryTemplate = temporaryTemplate;
    }
}
