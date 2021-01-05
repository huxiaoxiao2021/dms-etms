package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ExceptionReason;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.PdaResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ReportRecord;

import java.util.List;
import java.util.Map;

public interface IAbnPdaAPIManager {

    Map<String, ExceptionReason> selectAbnReasonByErp();

    JdCResponse report(List<ReportRecord> wpAbnormalRecordPda);

    JdCResponse<ExceptionReason> getAbnormalFirst(Long abnormalSecondId);
}
