package com.jd.bluedragon.alpha.ucc;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.utils.JsonHelper;
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
public class UccPrintDeviceImpl implements UccPrintDevice{

    private static final Log logger = LogFactory.getLog(UccPrintDeviceImpl.class);

    ConfClient confClient = ConfClientFactory.getConfClient();



    private String uccPath;//ucc路径

    private String uccReadToken;//ucc读权限

    private String uccWriteToken;//ucc写权限


    //获取所有的基本信息
    public List<PrintDevice> getPrintDevice(){

        Map<String,String> KeyMap;//用于获取UCC的key集合
        List<PrintDevice> printDevicesList = new ArrayList<PrintDevice>();
        PrintDevice PrintDevice ;
        try{
            KeyMap = confClient.getPathValues(uccPath,uccReadToken);
            for (String value : KeyMap.values()){
                PrintDevice = JsonHelper.fromJson(value,PrintDevice.class);//将key的值转化为json
                printDevicesList.add(PrintDevice);
            }
        }catch(Exception e){
            //Todo 日志打印以及异常处理
            logger.error("连接UCC失败：",e);
        }
        return printDevicesList;
    }

    //根据条件查询信息
    public List<PrintDevice> searchPrintDevice(String versionId,String printDeviceId){

        List<PrintDevice> result = new ArrayList<PrintDevice>();
        String str = "";
        PrintDevice printDevice;
        try{
            if(null != printDeviceId && "".equals(printDeviceId) == false){
                str = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);
                printDevice = JsonHelper.fromJsonUseGson(str,PrintDevice.class);
                if (null != versionId && versionId == printDevice.getVersionId()){
                    result.add(printDevice);
                }
                if(null == versionId || "".equals(versionId)){
                    result.add(printDevice);
                }
            }else{
                if (null != versionId && "".equals(versionId) == false){
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
        String value = JsonHelper.toJson(PrintDevice);//将对象转化为json字符串
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
            logger.error("查询该ISVID失败：",e);
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
        String value = JsonHelper.toJson(PrintDevice);//将对象转化为json字符串
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
            version = JsonHelper.fromJsonUseGson(str,Version.class);
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
            String value = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);//读取该ISV的配置信息
            PrintDevice printDevice = JsonHelper.fromJsonUseGson(value,PrintDevice.class);
            if(printDevice.getState() == "1"){
                bool = true;
            }else{
                bool = false;
            }
        }catch(Exception e){
            bool = false;
            e.printStackTrace();
            logger.error("UCC连接失败：",e);
        }
        return bool;
    }

    /**
     * 更改ISV的状态
     * @param printDeviceId ISVID
     * @param state 要更改的状态
     */
    public void changePrintDeviceState(String printDeviceId,String state)throws Exception{
        String value1 = confClient.getConfValue(uccPath,uccReadToken,printDeviceId);//获取ISV配置最初状态
        PrintDevice printDevice = JsonHelper.fromJsonUseGson(value1,PrintDevice.class);
        if(printDevice.getState() != state){
            printDevice.setState(state);//执行修改
            confClient.updateConfValue(uccPath,uccWriteToken,printDeviceId,JsonHelper.toJson(printDevice));
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
