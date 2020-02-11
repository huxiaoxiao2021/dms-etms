package com.jd.bluedragon.core.base;

import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.AbnormalReasonDto;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.PdaResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.WpAbnormalRecordPda;

import java.util.List;
import java.util.Map;

public interface IAbnPdaAPIManager {

    Map<String, AbnormalReasonDto> selectAbnReasonByErp(String userErp);

    PdaResult report(WpAbnormalRecordPda wpAbnormalRecordPda);
}
