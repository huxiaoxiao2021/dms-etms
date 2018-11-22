package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.BatchSendSummary;

import java.util.List;

/**
 * Created by wuzuxiang on 2018/11/22.
 */
public class BatchSendSummaryResponse extends JdResponse{

    private List<BatchSendSummary> batchSendSummaries;

    public BatchSendSummaryResponse(Integer code, String message) {
        super(code, message);
    }

    public List<BatchSendSummary> getBatchSendSummaries() {
        return batchSendSummaries;
    }

    public void setBatchSendSummaries(List<BatchSendSummary> batchSendSummaries) {
        this.batchSendSummaries = batchSendSummaries;
    }
}
