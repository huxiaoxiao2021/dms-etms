package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * Created by yanghongqiang on 2016/3/20.
 */
public class GantryDeviceResponse extends JdResponse {

    List<GantryDevice> data;

    public List<GantryDevice> getData() {
        return data;
    }

    public void setData(List<GantryDevice> data) {
        this.data = data;
    }
}
