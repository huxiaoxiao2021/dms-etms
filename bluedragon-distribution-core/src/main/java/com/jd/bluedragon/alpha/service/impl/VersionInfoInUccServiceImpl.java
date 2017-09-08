package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.alpha.jss.JssVersionService;
import com.jd.bluedragon.alpha.service.VersionInfoInUccService;
import com.jd.bluedragon.alpha.ucc.UccVersionService;
import com.jd.bluedragon.utils.DateHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
@Service("versionInfoInUccService")
public class VersionInfoInUccServiceImpl implements VersionInfoInUccService{

    private static final Log LOGGER = LogFactory.getLog(VersionInfoInUccServiceImpl.class);
    @Autowired
    UccVersionService uccVersionService;

    @Autowired
    JssVersionService jssVersionService;
    @Override
    public List<Version> versionList(){
        try{
            List<Version> list = uccVersionService.versionList();
            return list;
        }catch(Exception e){
            LOGGER.error("获取所有版本号失败",e);
            return Collections.emptyList();
        }
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
    public Integer uploadVersion(Version version) throws Exception{
        uccVersionService.uploadVersion(version);
        return 1;
    }

    @Override
    public Integer deleteVersion(List<String> versionIdList)throws Exception {
        /** 删除UCC配置信息 **/
        uccVersionService.deleteVersion(versionIdList);
        return 1;
    }

    @Override
    public Integer modifyVersion(Version version)throws Exception {
        /** 设置更新数据的时间 **/
        version.setUpdateTime(DateHelper.formatDateTime(new Date()));
        uccVersionService.modifyVersion(version);
        return 1;
    }

    @Override
    public boolean versionState(String versionId) {
        boolean bool = Boolean.FALSE;
        try{
            bool = uccVersionService.versionState(versionId);
        } catch (Exception e){
            LOGGER.error("ucc连接失败" , e);
        }
        return bool;
    }

    @Override
    public Integer changeVersionState(String versionId, boolean state) throws Exception{
        uccVersionService.changeVersionState(versionId,state);
        return 1;
    }
}
