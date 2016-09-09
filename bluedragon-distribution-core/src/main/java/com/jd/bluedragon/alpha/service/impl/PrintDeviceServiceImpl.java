package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.service.PrintDeviceService;
import com.jd.bluedragon.alpha.ucc.UccPrintDevice;
import com.jd.bluedragon.distribution.alpha.PrintDeviceDto;
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
    UccPrintDevice uccPrintDevice;

    @Override
    public List<PrintDevice> allPrintDeviceInfo() {
        List<PrintDevice> list = uccPrintDevice.getPrintDevice();
        return list;
    }

    @Override
    public List<PrintDevice> searchPrintDevice(String versionId, String printDeviceId) {
        List<PrintDevice> list = uccPrintDevice.searchPrintDevice(versionId, printDeviceId);
        return list;
    }

    @Override
    public Integer addPrintDevice(PrintDevice printDevice) {

        Date date = new Date();
        Integer result = -1;
        if (null != printDevice.getPrintDeviceId() && null != printDevice.getVersionId() && null != printDevice.getState()) {
            printDevice.setCreateTime(DateHelper.formatDateTime(date));
            printDevice.setUpdateTime(DateHelper.formatDateTime(date));
            boolean bool = uccPrintDevice.isPrintDeviceExist(printDevice.getPrintDeviceId());
            if (bool != true) {
                uccPrintDevice.addPrintDevice(printDevice);
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
            uccPrintDevice.deleteAllPrintDevice(KeyList);
            return 1;
        } catch (Exception e) {
            logger.error("删除ISVID失败：",e);
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Integer modifyPrintDevice(PrintDeviceDto printDeviceDto) {
        PrintDevice printDevice = new PrintDevice();
        Date date = new Date();
        if (null != printDeviceDto.getPrintDeviceId() && null != printDeviceDto.getVersionId() && null != printDeviceDto.getState()) {
            printDevice.setPrintDeviceId(printDeviceDto.getPrintDeviceId());
            printDevice.setVersionId(printDeviceDto.getVersionId());
            printDevice.setState(printDeviceDto.getState());
            printDevice.setDes(null == printDeviceDto.getDes() ? null : printDeviceDto.getDes());
            printDevice.setCreateTime(printDeviceDto.getCreateTime());
            printDevice.setUpdateTime(DateHelper.formatDateTime(date));
            try {
                uccPrintDevice.modifyPrintDevice(printDevice);
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
            versionId = uccPrintDevice.searchVersionIdByPrintDeviceId(printDeviceId);
        }catch (Exception e){
            logger.error("根据ID查询ISV失败：",e);
            e.printStackTrace();
        }
        return versionId;
    }

    @Override
    public boolean printDeviceState(String printDeviceId) {
        boolean bool = uccPrintDevice.printDeviceState(printDeviceId);
        return bool;
    }

    @Override
    public Integer changePrintDeviceState(String printDeviceId, String state) {
        Integer result;
        try{
            uccPrintDevice.changePrintDeviceState(printDeviceId,state);//更改状态信息
            result = 1;//状态修改成功
        }catch(Exception e){
            result = -1;//状态修改失败
            logger.error("更改ISV状态失败：",e);
            e.printStackTrace();
        }
        return result;
    }

}
