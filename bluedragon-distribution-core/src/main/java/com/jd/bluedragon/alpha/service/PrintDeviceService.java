package com.jd.bluedragon.alpha.service;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.distribution.alpha.PrintDeviceDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/22.
 */
@Component
public interface PrintDeviceService {

    public List<PrintDevice> allPrintDeviceInfo();

    public List<PrintDevice> searchPrintDevice(String versionId, String ISVID);

    public Integer addPrintDevice(PrintDevice printDevice);

    public Integer deletePrintDeviceById(List<String> KeyList);

    public Integer modifyPrintDevice(PrintDeviceDto printDeviceDto);

    /**
     * 通过ISVID查询版本号
     * @param printDeviceId ISVID
     * @return 版本号
     */
    public String searchVersionIdByPrintDeviceId(String printDeviceId);

    public boolean printDeviceState(String printDeviceId);

    public Integer changePrintDeviceState(String printDeviceId,String State);


}
