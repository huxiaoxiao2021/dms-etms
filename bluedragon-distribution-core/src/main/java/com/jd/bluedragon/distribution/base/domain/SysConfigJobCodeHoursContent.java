package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by xumei3 on 2018/5/10.
 */
public class SysConfigJobCodeHoursContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer defaultHours;
    private List<Map<String,Object>> jobCodeHours;

    public Integer getDefaultHours() {
        return defaultHours;
    }

    public void setDefaultHours(Integer defaultHours) {
        this.defaultHours = defaultHours;
    }

    public List<Map<String, Object>> getJobCodeHours() {
        return jobCodeHours;
    }

    public void setJobCodeHours(List<Map<String, Object>> jobCodeHours) {
        this.jobCodeHours = jobCodeHours;
    }

    public String toString(){
        return "defaultHours:" + defaultHours + ",jobCodeHours:" + jobCodeHours;
    }
}
