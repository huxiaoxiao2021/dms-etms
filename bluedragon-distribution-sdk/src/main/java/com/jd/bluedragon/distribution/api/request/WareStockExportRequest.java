package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @author dudong
 * @date 2015/5/10
 */
public class WareStockExportRequest implements Serializable{
    private static final long serialVersionUID = -7293859102172654232L;
    private long[] wids;
    private int rid;

    public WareStockExportRequest(){

    }

    public WareStockExportRequest(long[] wids, int rid){
        this.wids = wids;
        this.rid  = rid;
    }

    public long[] getWids() {
        return wids;
    }

    public void setWids(long[] wids) {
        this.wids = wids;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }
}
