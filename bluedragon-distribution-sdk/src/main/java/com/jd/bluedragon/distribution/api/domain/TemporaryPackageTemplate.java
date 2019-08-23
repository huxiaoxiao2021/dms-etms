package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.List;

public class TemporaryPackageTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 模板名称
     */
    private String temporaryTemplateName;
    /**
     * 模板版本号
     */
    private Integer temporaryTemplateVersion;

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

	/**
	 * @return the temporaryTemplateVersion
	 */
	public Integer getTemporaryTemplateVersion() {
		return temporaryTemplateVersion;
	}

	/**
	 * @param temporaryTemplateVersion the temporaryTemplateVersion to set
	 */
	public void setTemporaryTemplateVersion(Integer temporaryTemplateVersion) {
		this.temporaryTemplateVersion = temporaryTemplateVersion;
	}
}
