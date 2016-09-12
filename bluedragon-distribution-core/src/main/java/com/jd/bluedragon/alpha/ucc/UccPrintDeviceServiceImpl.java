package com.jd.bluedragon.alpha.ucc;

import com.google.gson.Gson;
import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.domain.Version;
import com.jd.std.ucc.client.ConfClient;
import com.jd.std.ucc.client.client.ConfClientFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2016/8/23.
 */
public class UccPrintDeviceServiceImpl implements UccPrintDeviceService{

    private static final Log logger = LogFactory.getLog(UccPrintDeviceServiceImpl.class);

    ConfClient confClient = ConfClientFactory.getConfClient();

    Gson gson = new Gson();

    /** UCC的路径**/
    private String uccPath;

    /** UCC的读权限**/
    private String uccReadToken;

    /**UCC的写权限**/
    private String uccWriteToken;

    /**获取所有的基本信息**/
    public List<PrintDevice> getPrintDevice(){

        /** 用于获取UCC的key集合 **/
        Map<String,String> KeyMap;
        List<PrintDevice> printDevicesList = new ArrayList<PrintDevice>();
        PrintDevice PrintDevice ;
        try{
            KeyMap = confClient.getPathValues(uccPath,uccReadToken);
            for (String value : KeyMap.values()){
                /** 将key的值转化为json **/
                PrintDevice = gson.fromJson(value,PrintDevice.class);
                printDevicesList.add(PrintDevice);
            }
        }catch(Exception e){
            //Todo 日志打印以及异常处理
            logger.error("连接UCC失败：",e);
        }
        return printDevicesList;
    }

    /**根据条件查询信息**/
    public List<PrintDevice> searchPrintDevice(String versionId,String printDeviceId){

        List<PrintDevice> result = new ArrayList<PrintDevice>();
        try{
            if(null != printDeviceId && "".equals(printDeviceId) == false){
                String str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
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
        }catch(Exception e){
            //Todo 日志打印以及异常处理
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 添加ISV信息
     * @param PrintDevice 对象
     */
    public void addPrintDevice(PrintDevice PrintDevice){
        String value = gson.toJson(PrintDevice);//将对象转化为json字符串
        try{
            confClient.addConfValue(uccPath,uccWriteToken,PrintDevice.getPrintDeviceId(),value);//将ID作为key值
        }catch (Exception e){
            //Todo 日志打印以及异常处理（此处异常可能原因是key值已经存在或者是path、token错误）
            e.printStackTrace();
        }
    }

    /**
     * 根据ISVID查询该ISVID是否已经存在
     * @param printDeviceId
     * @return 不存在返回false 存在发挥true
     */
    public boolean isPrintDeviceExist(String printDeviceId){
        boolean bool;
        try{
            String str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
            if(str.isEmpty()){
                bool = false;
            }else{
                bool = true;
            }
        }catch (Exception e){
            logger.error("查询该ISVID失败,该ISVID不存在：",e);
            bool = false;
        }
        return bool;
    }

    /**
     * 批量删除ISV信息，根据UCCkey的值删除，UCCKey == ISVID
     */
    public void deleteAllPrintDevice(List<String> KeyList){
        for (String key : KeyList){
            try{
                confClient.deleteConfKey(uccPath,uccWriteToken,key);
            }catch(Exception e){
                //Todo 日志打印以及异常处理
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改ISV信息
     * @param PrintDevice 对象
     */
    public void modifyPrintDevice(PrintDevice PrintDevice){
        String value = gson.toJson(PrintDevice);//将对象转化为json字符串
        try{
            confClient.updateConfValue(uccPath,uccWriteToken,PrintDevice.getPrintDeviceId(),value);
        }catch (Exception e){
            //Todo 日志打印以及异常处理（此处异常可能原因是key值已经存在或者是path、token错误）
            e.printStackTrace();
        }
    }

    /**
     * 根据ISVID查询对应的版本编号
     * @param printDeviceId
     * @return
     */
    public String searchVersionIdByPrintDeviceId(String printDeviceId){

        Version version = null;

        try{
            String str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
            version = gson.fromJson(str,Version.class);
        }catch(Exception e ){
            logger.error("版本查询失败",e);
        }
        return version.getVersionId();
    }

    /**
     * 根据ISVID查询该ISV的状态
     * @param printDeviceId ISVID
     * @return 状态为1返回true 状态为0返回false
     */
    public boolean printDeviceState(String printDeviceId){
        boolean bool = false;
        try{
            /** 读取该ISV的配置信息 **/
            String value = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
            PrintDevice printDevice = gson.fromJson(value,PrintDevice.class);
            if(printDevice.isState()){
                bool = true;
            }else{
                bool = false;
            }
        }catch(Exception e){
            bool = false;
            logger.error("UCC连接失败：",e);
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
