package com.jd.bluedragon.core.jsf.work;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.videoTraceCamera.VideoTraceCameraConfig;
import com.jdl.basic.common.utils.Result;

import java.util.List;

public interface VideoTraceCameraJsfManager {
    
    List<String> queryVideoTraceCameraConfig(String deviceNo, String nationalChannelCode,
                                                                     String createTime, Integer status, Integer siteCode);
}
