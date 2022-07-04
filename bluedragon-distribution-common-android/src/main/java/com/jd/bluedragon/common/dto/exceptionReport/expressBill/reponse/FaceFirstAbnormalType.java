package com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 面单举报一级原因
 *
 * @author hujiping
 * @date 2022/5/31 4:19 PM
 */
public class FaceFirstAbnormalType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer abnormalCode;
    private String abnormalName;
    private List<FaceSecondAbnormalType> list = new ArrayList<FaceSecondAbnormalType>();

    public Integer getAbnormalCode() {
        return abnormalCode;
    }

    public void setAbnormalCode(Integer abnormalCode) {
        this.abnormalCode = abnormalCode;
    }

    public String getAbnormalName() {
        return abnormalName;
    }

    public void setAbnormalName(String abnormalName) {
        this.abnormalName = abnormalName;
    }

    public List<FaceSecondAbnormalType> getList() {
        return list;
    }

    public void setList(List<FaceSecondAbnormalType> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return getAbnormalName() == null ? "" : getAbnormalName();
    }
}
