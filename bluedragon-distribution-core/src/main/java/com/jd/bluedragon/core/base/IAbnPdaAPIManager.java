package com.jd.bluedragon.core.base;

import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ExceptionReason;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.PdaResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.WpAbnormalRecordPda;
import java.util.Map;

public interface IAbnPdaAPIManager {

    Map<String, ExceptionReason> selectAbnReasonByErp(String userErp);

    PdaResult report(WpAbnormalRecordPda wpAbnormalRecordPda);
}
