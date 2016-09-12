package com.jd.bluedragon.alpha.jss;


import com.jcloud.jss.Credential;
import com.jcloud.jss.JingdongStorageService;
import com.jcloud.jss.client.ClientConfig;
import com.jcloud.jss.domain.ObjectListing;
import com.jcloud.jss.domain.ObjectSummary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
public class JssVersionServiceImpl implements JssVersionService {

    private static final Log logger = LogFactory.getLog(JssVersionServiceImpl.class);

    /**存储数据的最基本的单元*/
    private String bucket;
    /**访问密钥*/
    private String accesskey;
    /**安全密钥*/
    private String secretkey;
    /**内网连接端点*/
    private String endpoint;
    /**服务器请求超时*/
    private long connectionTimeout=10000;
    /**服务器响应超时*/
    private long socketTimeout=10000;
    /**存放时间*/
    private Integer storeTime = 2592000;


    /**
     * 获取所有的版本编号
     */
    public List<String> getVersionId(){
        JingdongStorageService jss = getJss();

        List<String> result = new ArrayList<String>();
        ObjectListing vertionList = jss.bucket(bucket).listObject();
        for (ObjectSummary key: vertionList.getObjectSummaries()){
            result.add(key.getKey().trim().substring(0,key.getKey().trim().indexOf(".")));
        }
        return result;
    }

    /**
     * 上传文件夹
     * @param keyName
     * @param length 流的大小
     * @param inputStream 上传流
     */
    public void addVersion(String keyName, long length, InputStream inputStream){
        JingdongStorageService jss = getJss();
        try{
            jss.bucket(bucket).object(keyName).entity(length,inputStream).put();
            inputStream.close();
        }catch(Throwable e){
            logger.error("UCC连接失败：",e);
            e.printStackTrace();
        }
    }

    /**
     * 批量删除版本信息
     * @param
     */
    public void deleteVersion(List<String> versionIdList)throws Exception{
        JingdongStorageService jss = getJss();
        for (String versionId : versionIdList) {
            String key = versionId + ".rar";

            jss.bucket(bucket).object(key).delete();

        }

    }

    /**
     * 获取对应版本的下载地址
     * 抛出异常
     */
    public URI downloadVersion(String versionId)throws MalformedURLException {
        JingdongStorageService jss = getJss();

        URI uri = jss.bucket(bucket).object(versionId).generatePresignedUrl(10000);//获得带有预签名的下载地址timeout == 10000
        return uri;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(long socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(Integer storeTime) {
        this.storeTime = storeTime;
    }

    public JingdongStorageService getJss() {
        Credential credential = new Credential(accesskey, secretkey);
        ClientConfig config = new ClientConfig();
        config.setEndpoint(endpoint);
        config.setConnectionTimeout(5000);
        config.setSocketTimeout(50000);
        return  new JingdongStorageService(credential,config);
    }
}
