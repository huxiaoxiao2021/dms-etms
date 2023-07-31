package com.jd.bluedragon.distribution.jy.dto;

import com.jd.bluedragon.distribution.jy.exception.JyExceptionDamageEntity;

public class JyExceptionDamageDto extends JyExceptionDamageEntity {
    private String staffName;

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
