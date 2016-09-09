package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.alpha.jss.JssVersion;
import com.jd.bluedragon.alpha.service.VersionInfoInUccService;
import com.jd.bluedragon.alpha.ucc.UccVersion;
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
    UccVersion uccVersion;
    @Autowired
    JssVersion jssVersion;

    @Override
    public List<Version> versionList() {
        List<Version> list = uccVersion.versionList();
        return list;
    }

    @Override
    public List<Version> queryList(String versionId,String state) throws Exception {
        List<Version> list = uccVersion.queryList(versionId,state);
        return list;
    }

    @Override
    public Integer uploadVersion(Version version) {
        try{
            uccVersion.uploadVersion(version);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;//上传失败
        }
    }

    @Override
    public Integer deleteVersion(List<String> versionIdList) {
        try{
            uccVersion.deleteVersion(versionIdList);//删除UCC的配置信息
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Integer modifyVersion(Version version) {
        version.setUpdateTime(DateHelper.formatDateTime(new Date()));//设置更新数据的时间
        try{
            uccVersion.modifyVersion(version);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean versionState(String versionId) {
        boolean bool = uccVersion.versionState(versionId);
        return bool;
    }

    @Override
    public Integer changeVersionState(String versionId, String state) {
        Integer result;
        try{
            uccVersion.changeVersionState(versionId,state);
            result = 1;//更新状态成功
        }catch(Exception e){
            result = -1;//异常处理，状态更新未成功
        }
        return result;
    }
}
