package com.jd.bluedragon.distribution.external.jos.service;

import com.jd.bluedragon.distribution.api.request.LoadBillReportRequest;
import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;

import java.util.Map;

/**
 * Created by yangbo7 on 2015/9/1.
 */
public interface JosService {

    public LoadBillReportResponse updateLoadBillStatus(LoadBillReportRequest request);



}
