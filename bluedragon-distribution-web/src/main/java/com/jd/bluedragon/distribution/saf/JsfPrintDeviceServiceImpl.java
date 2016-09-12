package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.alpha.domain.PrintDevice;
import com.jd.bluedragon.alpha.service.PrintDeviceService;
import com.jd.bluedragon.alpha.service.VersionInfoInJssService;
import com.jd.bluedragon.alpha.service.VersionInfoInUccService;
import com.jd.bluedragon.distribution.alpha.PrintDeviceRequest;
import com.jd.bluedragon.distribution.alpha.PrintDeviceResponse;
import com.jd.bluedragon.distribution.alpha.jsf.JsfPrintDeviceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/9/5.
 */
public class JsfPrintDeviceServiceImpl implements JsfPrintDeviceService{

    private static final String printDeviceIdKing = "00000";//用于全部更新的

    @Autowired
    PrintDeviceService printDeviceService;

    @Autowired
    VersionInfoInJssService versionInfoInJssService;

    @Autowired
    VersionInfoInUccService versionInfoInUccService;

    /**
     * 用于灰度测试ISV信息
     * @return
     */
    private List<String> getPrintDeviceIdMinisterList() {
        List<String> printDeviceIdMinisterList = new ArrayList<String>();//用于灰度测试的五个ISV设备
        List<PrintDevice> printDevices = printDeviceService.allPrintDeviceInfo();//获取UCC信息
        for(PrintDevice printDevice : printDevices){
            printDeviceIdMinisterList.add(printDevice.getPrintDeviceId());//添加到灰度测试列表
        }
        return printDeviceIdMinisterList;
    }

    @Override
    public PrintDeviceResponse PrintDeviceUpdata(PrintDeviceRequest request) {
        List<String> printDeviceIdMinisterList = new ArrayList<String>();//用于灰度测试的五个ISV设备
        printDeviceIdMinisterList = getPrintDeviceIdMinisterList();//初始化灰度测试ISV列表
        PrintDeviceResponse response = new PrintDeviceResponse();//返回结果

        String versionIdInUcc = "";
        if(printDeviceIdMinisterList.contains(request.getPrintDeviceId())){//判断是否在灰度测试列表中
            versionIdInUcc = printDeviceService.searchVersionIdByPrintDeviceId(request.getPrintDeviceId());//获取UCC中的灰度ISV对应的最新版本号

        }else{
            versionIdInUcc = printDeviceService.searchVersionIdByPrintDeviceId(printDeviceIdKing);//获取UCC中全国上线的最新ISV版本号
        }

        String id = printDeviceIdMinisterList.contains(request.getPrintDeviceId())? request.getPrintDeviceId() : printDeviceIdKing;
        boolean printDeviceState = printDeviceService.printDeviceState(id); //获取该设备的状态,用于灰度测试的直接查找，全国的则通过查找对应全国的ISV的状态
        if(printDeviceState == true){
            //该ISV是启用状态则往下执行获取版本号
            if(request.getVersionId() != versionIdInUcc){//判断是否是最新版本
                boolean versionState = versionInfoInUccService.versionState(versionIdInUcc);
                if(versionState == true){//并且最新版本可用
                    //通过最新版本的版本编号versionIdInUcc获取JSS中的下载地址
                    try{
                        URL url = versionInfoInJssService.downloadVersion(versionIdInUcc);
                        response.setPrintDeviceId(request.getPrintDeviceId());
                        response.setVersionId(versionIdInUcc);
                        response.setYn(0);//请求成功
                        response.setUrl(url);
                    }catch (Exception e){
                        response.setPrintDeviceId(request.getPrintDeviceId());
                        response.setVersionId(versionIdInUcc);
                        response.setYn(0);//请求失败
                    }
                }
            }
        }else{
            response.setPrintDeviceId(request.getPrintDeviceId());
            response.setVersionId(request.getVersionId());
            response.setYn(1);
        }
        return response;
    }
}
