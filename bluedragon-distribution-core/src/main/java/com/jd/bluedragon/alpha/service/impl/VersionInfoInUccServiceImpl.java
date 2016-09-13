package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.alpha.jss.JssVersionService;
import com.jd.bluedragon.alpha.service.VersionInfoInUccService;
import com.jd.bluedragon.alpha.ucc.UccVersionService;
import com.jd.bluedragon.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
@Service("versionInfoInUccService")
public class VersionInfoInUccServiceImpl implements VersionInfoInUccService{
    @Autowired
    UccVersionService uccVersionService;

    @Autowired
    JssVersionService jssVersionService;
    @Override
    public List<Version> versionList() {
        List<Version> list = uccVersionService.versionList();
        return list;
    }

    @Override
    public List<Version> queryList(String versionId,boolean state) throws Exception {
        List<Version> list = uccVersionService.queryList(versionId,state);
        return list;
    }

    @Override
    public Version queryById(String versionId) throws Exception{
        Version version = uccVersionService.queryById(versionId);
        return version;
    }

    @Override
    public Integer uploadVersion(Version version) {
        try{
            uccVersionService.uploadVersion(version);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;//上传失败
        }
    }

    @Override
    public Integer deleteVersion(List<String> versionIdList) {
        try{
            /** 删除UCC配置信息 **/
            uccVersionService.deleteVersion(versionIdList);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Integer modifyVersion(Version version) {
        /** 设置更新数据的时间 **/
        version.setUpdateTime(DateHelper.formatDateTime(new Date()));
        try{
            uccVersionService.modifyVersion(version);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean versionState(String versionId) {
        boolean bool = uccVersionService.versionState(versionId);
        return bool;
    }

    @Override
    public Integer changeVersionState(String versionId, boolean state) {
        Integer result;
        try{
            uccVersionService.changeVersionState(versionId,state);
            result = 1;
        }catch(Exception e){
            result = -1;
        }
        return result;
    }
}
