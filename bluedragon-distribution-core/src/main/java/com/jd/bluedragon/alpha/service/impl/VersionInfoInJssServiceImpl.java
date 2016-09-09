package com.jd.bluedragon.alpha.service.impl;

import com.jd.bluedragon.alpha.jss.JssVersion;
import com.jd.bluedragon.alpha.service.VersionInfoInJssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/30.
 */
@Service("versionInfoInJssService")
public class VersionInfoInJssServiceImpl implements VersionInfoInJssService{

    @Autowired
    JssVersion jssClient;

    @Override
    public List<String> allVersionIdInJss() {
        List<String> versionList = jssClient.getVersionId();
        return versionList;
    }

    @Override
    public Integer uploadNewVersion(String versionId, long length , InputStream inputStream) {
        String keyName = versionId + ".rar";
        try{
            jssClient.addVersion(keyName,length,inputStream);
            return 1;//上传成功
        }catch(Exception e){
            return -1;//上传失败
        }
    }

    @Override
    public Integer modifyVersion(String versionId, long length , InputStream inputStream) {
        String keyName = versionId + ".rar";

        List<String> versionList = new ArrayList<String>();
        versionList.add(versionId);
        try{
            jssClient.deleteVersion(versionList);//先删除旧版本
            jssClient.addVersion(keyName,length,inputStream);//添加新版本
            return 1;//更新完成
        }catch(Exception e){
            return -1;//更新失败
        }
    }

    @Override
    public Integer deleteVersionByVersionId(List<String> versionIdList) {
        Integer result = null;

            try{
                jssClient.deleteVersion(versionIdList);
                result = 1;
            }catch(Exception e){
                result = -1;
            }

        return result;
    }

    @Override
    public URL downloadVersion(String versionId) throws MalformedURLException {
        URI uri = null ;
        try{
            uri = jssClient.downloadVersion(versionId);
        }catch(Exception e){
            e.printStackTrace();
        }
        return uri.toURL();
    }
}
