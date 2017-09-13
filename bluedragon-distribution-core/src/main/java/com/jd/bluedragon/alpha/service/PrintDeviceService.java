package com.jd.bluedragon.alpha.service;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/22.
 */
@Component
public interface PrintDeviceService {

    /**
     * 查询所有ISV的信息
     * @return
     */
    public List<PrintDevice> allPrintDeviceInfo();

    /**
     * 通过条件查询对应的ISV信息
     * @param versionId 版本编号
     * @param printDevice
     * @return
     */
    public List<PrintDevice> searchPrintDevice(String versionId, String printDevice);

    /**
     * 添加ISV的信息
     * @param printDevice
     * @return
     */
    public Integer addPrintDevice(PrintDevice printDevice) throws Exception;

    /**
     * 通过ISV的id批量删除ISV的信息
     * @param KeyList ISV的删除列表
     * @return
     */
    public Integer deletePrintDeviceById(List<String> KeyList) throws Exception;

    /**
     * 修改ISV的信息
     * @param printDevice
     * @return
     */
    public Integer modifyPrintDevice(PrintDevice printDevice) throws Exception;

    /**
     * 通过ISVID查询版本号
     * @param printDeviceId ISVID
     * @return 版本号
     */
    public String searchVersionIdByPrintDeviceId(String printDeviceId);

    /**
     * 获取ISV的状态
     * @param printDeviceId
     * @return true可用 false不可用
     */
    public boolean printDeviceState(String printDeviceId);

    /**
     * 改变ISV的状态
     * @param printDeviceId
     * @param State
     * @return
     */
    public Integer changePrintDeviceState(String printDeviceId,boolean State) throws Exception;


}
