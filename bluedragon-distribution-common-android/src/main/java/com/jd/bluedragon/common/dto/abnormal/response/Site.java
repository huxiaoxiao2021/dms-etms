package com.jd.bluedragon.common.dto.abnormal.response;

public class Site {

    /**
     * 站点名称
     */
    String site_name;

    /**
     * 站点id
     */
    Integer site_code;

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public Integer getSite_code() {
        return site_code;
    }

    public void setSite_code(Integer site_code) {
        this.site_code = site_code;
    }
}
