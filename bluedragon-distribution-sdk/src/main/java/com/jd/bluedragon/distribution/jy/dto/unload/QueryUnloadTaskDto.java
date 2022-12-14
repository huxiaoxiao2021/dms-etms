package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
public class QueryUnloadTaskDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 5362119394569718888L;
    private String vehicleNumber;
    private String packageCode;

    @Override
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    @Override
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
