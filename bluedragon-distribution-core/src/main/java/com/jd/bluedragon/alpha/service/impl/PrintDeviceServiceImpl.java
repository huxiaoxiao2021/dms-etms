package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.service.PrintDeviceService;
import com.jd.bluedragon.alpha.ucc.UccPrintDeviceService;
import com.jd.bluedragon.utils.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/23.
 */
@Service("printDeviceService")
public class PrintDeviceServiceImpl implements PrintDeviceService {

    private static final Logger log = LoggerFactory.getLogger(PrintDeviceServiceImpl.class);

    @Autowired
    UccPrintDeviceService uccPrintDeviceService;

    @Override
    public List<PrintDevice> allPrintDeviceInfo(){
        List<PrintDevice> list = uccPrintDeviceService.getPrintDevice();
        return list;
    }

    @Override
    public List<PrintDevice> searchPrintDevice(String versionId, String printDeviceId){
        List<PrintDevice> list = uccPrintDeviceService.searchPrintDevice(versionId, printDeviceId);
        return list;
    }

    @Override
    public Integer addPrintDevice(PrintDevice printDevice) throws Exception{

        Date date = new Date();
        Integer result = -1;
        if (null != printDevice.getPrintDeviceId() && null != printDevice.getVersionId()) {
            printDevice.setCreateTime(DateHelper.formatDateTime(date));
            printDevice.setUpdateTime(DateHelper.formatDateTime(date));
            boolean bool = uccPrintDeviceService.isPrintDeviceExist(printDevice.getPrintDeviceId());
            if (bool != true) {
                uccPrintDeviceService.addPrintDevice(printDevice);
                result = 1;
            }
        } else {
            result = -1;//表示增加任务失败
        }
        return result;
    }

    @Override
    public Integer deletePrintDeviceById(List<String> KeyList) throws Exception{
        uccPrintDeviceService.deleteAllPrintDevice(KeyList);
        return 1;
    }

    @Override
    public Integer modifyPrintDevice(PrintDevice printDevice) throws Exception{
        Date date = new Date();
        if (null != printDevice.getPrintDeviceId() && null != printDevice.getVersionId()) {
            printDevice.setUpdateTime(DateHelper.formatDateTime(date));
            uccPrintDeviceService.modifyPrintDevice(printDevice);
            return 1;
        } else {
            return -1;//表示修改任务失败
        }
    }

    @Override
    public String searchVersionIdByPrintDeviceId(String printDeviceId){
        String versionId = "";
        try {
            versionId = uccPrintDeviceService.searchVersionIdByPrintDeviceId(printDeviceId);
        } catch(Exception e){
            log.error("获取打印机设备的版本号失败，设备编号：{}" , printDeviceId ,e);
        }
        return versionId;
    }

    @Override
    public boolean printDeviceState(String printDeviceId){
        boolean bool = Boolean.FALSE;
        try{
            bool = uccPrintDeviceService.printDeviceState(printDeviceId);
        } catch (Exception e){
            log.error("获取打印机设备信息失败，设备ID：{}" , printDeviceId,e);
        }
        return bool;
    }

    @Override
    public Integer changePrintDeviceState(String printDeviceId, boolean state) throws Exception{
        Integer result;
        uccPrintDeviceService.changePrintDeviceState(printDeviceId,state);//更改状态信息
        result = 1;//状态修改成功
        return result;
    }

}
