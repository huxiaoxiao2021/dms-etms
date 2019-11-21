package com.jd.bluedragon.alpha.ucc;

import com.google.gson.Gson;
import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.domain.Version;
import com.jd.std.ucc.client.ConfClient;
import com.jd.std.ucc.client.client.ConfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by wuzuxiang on 2016/8/23.
 */
public class UccPrintDeviceServiceImpl implements UccPrintDeviceService{

    private static final Logger log = LoggerFactory.getLogger(UccPrintDeviceServiceImpl.class);

    ConfClient confClient = ConfClientFactory.getConfClient();

    Gson gson = new Gson();

    /** UCC的路径**/
    private String uccPath;

    /** UCC的读权限**/
    private String uccReadToken;

    /**UCC的写权限**/
    private String uccWriteToken;

    /**获取所有的基本信息**/
    public List<PrintDevice> getPrintDevice() {

        /** 用于获取UCC的key集合 **/
        Map<String,String> KeyMap = new HashMap<String, String>();
        List<PrintDevice> printDevicesList = new ArrayList<PrintDevice>();
        PrintDevice PrintDevice ;
        try {
            KeyMap = confClient.getPathValues(uccPath,uccReadToken);
        } catch (Exception e){
            log.error("ucc-connection error",e);
        }
        if (KeyMap != null){
            for (String value : KeyMap.values()){
                /** 将key的值转化为json **/
                PrintDevice = gson.fromJson(value,PrintDevice.class);
                printDevicesList.add(PrintDevice);
            }
        }
        return printDevicesList;
    }

    /**根据条件查询信息**/
    public List<PrintDevice> searchPrintDevice(String versionId,String printDeviceId){

        List<PrintDevice> result = new ArrayList<PrintDevice>();
        if(null != printDeviceId && "".equals(printDeviceId) == false){
            String str = "";
            try {
                str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
            } catch (Exception e){
                log.error("ucc-connection error!!" ,e);
                return Collections.emptyList();
            }
            PrintDevice printDevice = gson.fromJson(str,PrintDevice.class);
            if (null != versionId && versionId.equals(printDevice.getVersionId())){
                result.add(printDevice);
            }
            if(null == versionId || "".equals(versionId)){
                result.add(printDevice);
            }
        }else{
            if (null != versionId && !"".equals(versionId)){
                List<PrintDevice> trans = getPrintDevice();
                for (PrintDevice a: trans) {
                    if(versionId.equals(a.getVersionId())){
                        result.add(a);
                    }
                }
            }else{
                result = getPrintDevice();
            }
        }

        return result;
    }

    /**
     * 添加ISV信息
     * @param PrintDevice 对象
     */
    public void addPrintDevice(PrintDevice PrintDevice) throws Exception{
        String value = gson.toJson(PrintDevice);//将对象转化为json字符串
        confClient.addConfValue(uccPath,uccWriteToken,PrintDevice.getPrintDeviceId(),value);//将ID作为key值
    }

    /**
     * 根据ISVID查询该ISVID是否已经存在
     * @param printDeviceId
     * @return 不存在返回false 存在发挥true
     */
    public boolean isPrintDeviceExist(String printDeviceId) throws Exception{
        boolean bool;
        String str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
        if(str.isEmpty()){
            bool = false;
        }else{
            bool = true;
        }
        return bool;
    }

    /**
     * 批量删除ISV信息，根据UCCkey的值删除，UCCKey == ISVID
     */
    public void deleteAllPrintDevice(List<String> KeyList) throws Exception{
        for (String key : KeyList){
            confClient.deleteConfKey(uccPath,uccWriteToken,key);
        }
    }

    /**
     * 修改ISV信息
     * @param PrintDevice 对象
     */
    public void modifyPrintDevice(PrintDevice PrintDevice) throws Exception{
        String value = gson.toJson(PrintDevice);//将对象转化为json字符串
        confClient.updateConfValue(uccPath,uccWriteToken,PrintDevice.getPrintDeviceId(),value);
    }

    /**
     * 根据ISVID查询对应的版本编号
     * @param printDeviceId
     * @return
     */
    public String searchVersionIdByPrintDeviceId(String printDeviceId) throws Exception{

        Version version = null;

        String str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
        version = gson.fromJson(str,Version.class);
        return version.getVersionId();
    }

    /**
     * 根据ISVID查询该ISV的状态
     * @param printDeviceId ISVID
     * @return 状态为1返回true 状态为0返回false
     */
    public boolean printDeviceState(String printDeviceId) throws Exception{
        boolean bool = false;
        /** 读取该ISV的配置信息 **/
        String value = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
        PrintDevice printDevice = gson.fromJson(value,PrintDevice.class);
        if(printDevice.isState()){
            bool = true;
        }else{
            bool = false;
        }
        return bool;
    }

    /**
     * 更改ISV的状态
     * @param printDeviceId ISVID
     * @param state 要更改的状态
     */
    public void changePrintDeviceState(String printDeviceId,boolean state)throws Exception{
        String value1 = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);//获取ISV配置最初状态
        PrintDevice printDevice = gson.fromJson(value1,PrintDevice.class);
        if(printDevice.isState() != state){
            printDevice.setState(state);//执行修改
            confClient.updateConfValue(uccPath,uccWriteToken,printDeviceId,gson.toJson(printDevice));
        }
    }

    public String getUccPath() {
        return uccPath;
    }

    public void setUccPath(String uccPath) {
        this.uccPath = uccPath;
    }

    public String getUccReadToken() {
        return uccReadToken;
    }

    public void setUccReadToken(String uccReadToken) {
        this.uccReadToken = uccReadToken;
    }

    public String getUccWriteToken() {
        return uccWriteToken;
    }

    public void setUccWriteToken(String uccWriteToken) {
        this.uccWriteToken = uccWriteToken;
    }
}
