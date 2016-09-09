package com.jd.bluedragon.alpha.ucc;

import com.jd.bluedragon.alpha.domain.Version;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
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
public class UccVersionImpl implements UccVersion {

    private static final Log logger = LogFactory.getLog(UccVersionImpl.class);
    ConfClient confClient = ConfClientFactory.getConfClient();

    private String path ;//版本信息的UCC库
    private String readToken ;//只读token
    private String writeToken ;//写token

    /**
     * 获取所有的version信息
     * @return
     */
    @Override
    public List<Version> versionList(){
        Map<String,String> keyMap ;
        List<Version> list = new ArrayList<Version>();
        Version version;
        try{
            keyMap = confClient.getPathValues(path,readToken);
            for (String value: keyMap.values()) {
                version = JsonHelper.fromJsonUseGson(value,Version.class);
                list.add(version);
            }
        }catch(Exception e){

            logger.error("UCC连接失败：",e);
            e.printStackTrace();
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
    public List<Version> queryList(String versionId,String state)throws Exception{
        List<Version> result = new ArrayList<Version>();
        Version version;
        if("".equals(versionId)==false && null != versionId){
            String value= confClient.getConfValue(path,readToken,versionId);
            version = JsonHelper.fromJsonUseGson(value,Version.class);
            if(null != state && state.equals(version.getState())){
                result.add(version);
            }
            if(null == state || "".equals(state)){
                result.add(version);
            }
        }else{
            if(null != state && !"".equals(state)){
                List<Version> list = versionList();
                for(Version a:list){
                    if(state.equals(a.getState())){
                        result.add(a);
                    }
                }
            }else{
                result = versionList();
            }
        }
        return result;
    }

    /**
     * 上传版本文件的的同时，需要处理ucc信息备份
     * @param version 版本编号信息
     */
    @Override
    public void uploadVersion(Version version){

        version.setCreateTime(DateHelper.formatDateTime(new Date()));
        version.setUpdateTime(DateHelper.formatDateTime(new Date()));

        try{
            confClient.addConfValue(path,writeToken,version.getVersionId(),JsonHelper.toJson(version));
        }catch (Exception e){
            logger.error("UCC连接失败：",e);
            e.printStackTrace();
        }
    }

    /**
     * 根据版本编号批量删除
     * @param versionIdList 版本编号
     */
    @Override
    public void deleteVersion(List<String> versionIdList){
        for (String versionId:versionIdList) {
            try{
                confClient.deleteConfKey(path,writeToken,versionId);
            }catch (Exception e){
                logger.error("UCC连接失败：",e);
                e.printStackTrace();
            }
        }

    }

    /**
     * 修改UCC上的版本信息
     * @param version
     */
    @Override
    public void modifyVersion(Version version){
        try{
            confClient.updateConfValue(path,writeToken,version.getVersionId(),JsonHelper.toJson(version));
        }catch(Exception e){
            logger.error("UCC连接失败：",e);
            e.printStackTrace();
        }
    }

    /**
     * 获取版本信息的状态
     * @param versionId 版本号
     * @return 状态为0返回false 状态为1返回true
     */
    @Override
    public boolean versionState(String versionId){
        boolean bool = false;
        try{
            String value = confClient.getConfValue(path,readToken,versionId);
            Version versionInfo = JsonHelper.fromJsonUseGson(value,Version.class);
            if("" == versionInfo.getState() || null == versionInfo.getState() || versionInfo.getState() == "0"){
                bool = false;//已停用
            }
            if(versionInfo.getState() == "1"){
                bool = true;//已启用
            }
        }catch(Exception e){
            bool = false;//停用
            logger.error("UCC连接失败：",e);
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * 更改版本状态
     * @param versionId
     * @param state
     */
    @Override
    public void changeVersionState(String versionId,String state)throws Exception{

        String value1 = confClient.getConfValue(path,readToken,versionId);//先取出版本的初始信息
        Version version = JsonHelper.fromJsonUseGson(value1,Version.class);
        if(version.getState() != state){
            version.setState(state);//执行修改
            confClient.updateConfValue(path,writeToken,versionId,JsonHelper.toJson(version));
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
