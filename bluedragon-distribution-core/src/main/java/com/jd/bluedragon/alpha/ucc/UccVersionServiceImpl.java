package com.jd.bluedragon.alpha.ucc;

import com.google.gson.Gson;
import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.std.ucc.client.ConfClient;
import com.jd.std.ucc.client.client.ConfClientFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单独对ISV版本信息的维护
 * Created by wuzuxiang on 2016/8/25.
 */
public class UccVersionServiceImpl implements UccVersionService {

    private static final Log logger = LogFactory.getLog(UccVersionServiceImpl.class);
    ConfClient confClient = ConfClientFactory.getConfClient();

    Gson gson = new Gson();

    private String path ;//版本信息的UCC库
    private String readToken ;//只读token
    private String writeToken ;//写token

    /**
     * 获取所有的version信息
     * @return
     */
    @Override
    public List<Version> versionList() throws Exception{
        Map<String,String> keyMap ;
        List<Version> list = new ArrayList<Version>();
        Version version;
        keyMap = confClient.getPathValues(path,readToken);
        for (String value: keyMap.values()) {
            version = gson.fromJson(value,Version.class);
            list.add(version);
        }
        return list;
    }

    /**
     * 根据版本编号、版本状态查询版本信息
     * @param versionId 版本编号
     * @param state 版本状态
     * @return
     */
    @Override
    public List<Version> queryList(String versionId,boolean state)throws Exception{
        List<Version> result = new ArrayList<Version>();
        Version version;
        if(!"".equals(versionId) && null != versionId){
            String value= confClient.getConfValue(path,readToken,versionId);
            version = gson.fromJson(value,Version.class);
            if(state == version.isState()){
                result.add(version);
            }
        }else{
            List<Version> list = versionList();
            for(Version a:list){
                if(state == a.isState()){
                    result.add(a);
                }
            }
        }
        return result;
    }

    /**
     * 上传版本文件的的同时，需要处理ucc信息备份
     * @param version 版本编号信息
     */
    @Override
    public void uploadVersion(Version version)  throws Exception{

        version.setCreateTime(DateHelper.formatDateTime(new Date()));
        version.setUpdateTime(DateHelper.formatDateTime(new Date()));

        confClient.addConfValue(path,writeToken,version.getVersionId(),gson.toJson(version));
    }

    /**
     * 根据版本编号批量删除
     * @param versionIdList 版本编号
     */
    @Override
    public void deleteVersion(List<String> versionIdList)  throws Exception{
        for (String versionId:versionIdList) {
            confClient.deleteConfKey(path,writeToken,versionId);
        }

    }

    /**
     * 修改UCC上的版本信息
     * @param version
     */
    @Override
    public void modifyVersion(Version version) throws Exception{
        confClient.updateConfValue(path,writeToken,version.getVersionId(),gson.toJson(version));
    }

    /**
     * 获取版本信息的状态
     * @param versionId 版本号
     * @return 状态为0返回false 状态为1返回true
     */
    @Override
    public boolean versionState(String versionId) throws Exception{
        boolean bool = false;
        String value = confClient.getConfValue(path,readToken,versionId);
        Version versionInfo = gson.fromJson(value,Version.class);
        bool = versionInfo.isState();
        return bool;
    }

    @Override
    public Version queryById(String versionId) throws Exception{
        String str = confClient.getConfValue(path,readToken,versionId);
        Version version = gson.fromJson(str,Version.class);
        return version;
    }

    /**
     * 更改版本状态
     * @param versionId
     * @param state
     */
    @Override
    public void changeVersionState(String versionId,boolean state)throws Exception{

        /** 先取出版本的初始信息**/
        String value1 = confClient.getConfValue(path,readToken,versionId);
        Version version = gson.fromJson(value1,Version.class);
        if(version.isState() != state){
            version.setState(state);
            confClient.updateConfValue(path,writeToken,versionId,gson.toJson(version));
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getReadToken() {
        return readToken;
    }

    public void setReadToken(String readToken) {
        this.readToken = readToken;
    }

    public String getWriteToken() {
        return writeToken;
    }

    public void setWriteToken(String writeToken) {
        this.writeToken = writeToken;
    }
}
