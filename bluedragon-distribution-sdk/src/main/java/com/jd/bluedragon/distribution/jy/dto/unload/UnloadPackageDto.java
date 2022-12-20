package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class UnloadPackageDto implements Serializable {
    private static final long serialVersionUID = -5377602873738672373L;
    private String packageCode;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
