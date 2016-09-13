package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.service.PrintDeviceService;
import com.jd.bluedragon.alpha.ucc.UccPrintDeviceService;
import com.jd.bluedragon.utils.DateHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/23.
 */
@Service("printDeviceService")
public class PrintDeviceServiceImpl implements PrintDeviceService {

    private static final Log logger = LogFactory.getLog(PrintDeviceServiceImpl.class);

    @Autowired
    UccPrintDeviceService uccPrintDeviceService;

    @Override
    public List<PrintDevice> allPrintDeviceInfo() {
        List<PrintDevice> list = uccPrintDeviceService.getPrintDevice();
        return list;
    }

    @Override
    public List<PrintDevice> searchPrintDevice(String versionId, String printDeviceId) {
        List<PrintDevice> list = uccPrintDeviceService.searchPrintDevice(versionId, printDeviceId);
        return list;
    }

    @Override
    public Integer addPrintDevice(PrintDevice printDevice) {

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
    public Integer deletePrintDeviceById(List<String> KeyList) {
        try {
            uccPrintDeviceService.deleteAllPrintDevice(KeyList);
            return 1;
        } catch (Exception e) {
            logger.error("删除ISVID失败：",e);
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Integer modifyPrintDevice(PrintDevice printDevice) {
        Date date = new Date();
        if (null != printDevice.getPrintDeviceId() && null != printDevice.getVersionId()) {
            printDevice.setUpdateTime(DateHelper.formatDateTime(date));
            try {
                uccPrintDeviceService.modifyPrintDevice(printDevice);
                return 1;
            } catch (Exception e) {
                logger.error("修改ISV失败：",e);
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;//表示修改任务失败
        }
    }

    @Override
    public String searchVersionIdByPrintDeviceId(String printDeviceId) {
        String versionId = "";
        try{
            versionId = uccPrintDeviceService.searchVersionIdByPrintDeviceId(printDeviceId);
        }catch (Exception e){
            logger.error("根据ID查询ISV失败：",e);
            e.printStackTrace();
        }
        return versionId;
    }

    @Override
    public boolean printDeviceState(String printDeviceId) {
        boolean bool = uccPrintDeviceService.printDeviceState(printDeviceId);
        return bool;
    }

    @Override
    public Integer changePrintDeviceState(String printDeviceId, boolean state) {
        Integer result;
        try{
            uccPrintDeviceService.changePrintDeviceState(printDeviceId,state);//更改状态信息
            result = 1;//状态修改成功
        }catch(Exception e){
            result = -1;//状态修改失败
            logger.error("更改ISV状态失败：",e);
            e.printStackTrace();
        }
        return result;
    }

}
