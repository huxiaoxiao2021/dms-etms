package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * Created by wuzuxiang on 2016/12/26.
 */
public class GantryBatchSendPrintRequest<T> extends JdRequest{

    private static final long serialVersionUID = -3833546092033252920L;

    private Integer machineId;

    private T list;

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }
}
