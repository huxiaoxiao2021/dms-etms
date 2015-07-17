package com.jd.bluedragon.distribution.batch.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

public class BatchSendResponse extends JdResponse {

	private static final long serialVersionUID = 6421643159029953636L;

	List<BatchSend> data;
	
	public BatchSendResponse() {
		super();
	}

	public BatchSendResponse(Integer code, String message) {
		super(code, message);
	}

    public  List<BatchSend> getData(){
        return data;
    }

    public void setData(List<BatchSend> batchSends){
        this.data=batchSends;
    }

}
