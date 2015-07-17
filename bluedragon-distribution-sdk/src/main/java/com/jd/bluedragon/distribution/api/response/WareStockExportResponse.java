package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * @author dudong
 * @date 2015/5/10
 */
public class WareStockExportResponse<T> extends JdResponse {
    private static final long serialVersionUID = -3676920751276667561L;
    private List<T> sids;

    public List<T> getSids() {
        return sids;
    }

    public void setSids(List<T> sids) {
        this.sids = sids;
    }

    public WareStockExportResponse(){

    }

    public WareStockExportResponse(Integer code, String message){
        super(code,message);
    }

}
