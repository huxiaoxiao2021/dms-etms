package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;


/**
 * Created by yanghongqiang on 2016/3/15.
 */
public class GantryDeviceConfigResponse extends JdResponse {

    List<GantryDeviceConfig> data;

    public List<GantryDeviceConfig> getData() {
        return data;
    }

    public void setData(List<GantryDeviceConfig> data) {
        this.data = data;
    }
}


