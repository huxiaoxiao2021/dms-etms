package com.jd.bluedragon.alpha.ucc;

import com.jd.bluedragon.alpha.domain.PrintDevice;

import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/23.
 */
public interface UccPrintDevice {


    //获取所有的基本信息
    public List<PrintDevice> getPrintDevice();

    //根据条件查询信息
    public List<PrintDevice> searchPrintDevice(String versionId,String printDeviceId);

    /**
     * 添加ISV信息
     * @param PrintDevice 对象
     */
    public void addPrintDevice(PrintDevice PrintDevice);

    /**
     * 根据ISVID查询该ISVID是否已经存在
     * @param printDeviceId
     * @return 不存在返回false 存在发挥true
     */
    public boolean isPrintDeviceExist(String printDeviceId);

    /**
     * 批量删除ISV信息，根据UCCkey的值删除，UCCKey == ISVID
     */
    public void deleteAllPrintDevice(List<String> KeyList);

    /**
     * 修改ISV信息
     * @param PrintDevice 对象
     */
    public void modifyPrintDevice(PrintDevice PrintDevice);

    /**
     * 根据ISVID查询对应的版本编号
     * @param printDeviceId
     * @return
     */
    public String searchVersionIdByPrintDeviceId(String printDeviceId);

    /**
     * 根据ISVID查询该ISV的状态
     * @param printDeviceId ISVID
     * @return 状态为1返回true 状态为0返回false
     */
    public boolean printDeviceState(String printDeviceId);

    /**
     * 更改ISV的状态
     * @param printDeviceId ISVID
     * @param state 要更改的状态
     */
    public void changePrintDeviceState(String printDeviceId,String state)throws Exception;

}
