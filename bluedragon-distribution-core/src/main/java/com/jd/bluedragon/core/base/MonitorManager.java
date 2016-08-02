package com.jd.bluedragon.core.base;

import com.jd.etms.monitor.dto.vos.ScanDto;

/**
 * Created by guoyongzhi on 2016/7/29.
 */
public interface MonitorManager  {
    ScanDto query(String sendCode,Integer Start,Integer Size);
}
