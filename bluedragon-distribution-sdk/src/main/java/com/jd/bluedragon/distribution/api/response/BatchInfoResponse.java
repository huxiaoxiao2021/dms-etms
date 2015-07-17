package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

public class BatchInfoResponse extends JdResponse {

	private static final long serialVersionUID = 6421643159029953636L;

	List<BatchInfo> data;

    public  List<BatchInfo> getData(){
        return data;
    }

    public void setData(List<BatchInfo> batchInfos){
        this.data=batchInfos;
    }

}
