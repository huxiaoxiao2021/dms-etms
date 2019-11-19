package com.jd.bluedragon.distribution.jss.utils;

import com.jd.jss.Credential;
import com.jd.jss.JingdongStorageService;
import com.jd.jss.client.ClientConfig;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName JssStorageClient
 * @date 2019/4/17
 */
public class JssStorageClient {

    private final static Logger logger = Logger.getLogger(JssStorageClient.class);

    /**
     * 内网连接端点
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 安全密钥
     */
    private String secretKey;

    /**
     * 服务器响应超时
     */
    private int socketTimeout = 50000;

    /**
     * 服务器请求超时
     */
    private int connectionTimeout = 50000;

    private long partSize = 128 * 1024 * 1024;

    private int maxConnections = 128;

    /**
     * 锁
     */
    private static final Lock lock = new ReentrantLock();

    private JingdongStorageService storageService = null;

    public void init() {
        try {
            lock.lock();
            ClientConfig config = new ClientConfig();
            //默认是50s
            config.setSocketTimeout(socketTimeout);
            //默认是50s
            config.setConnectionTimeout(connectionTimeout);
            //默认128M,大文件分割存储的块大小
            config.setPartSize(partSize);
            //默认连接数128
            config.setMaxConnections(maxConnections);
            config.setEndpoint(endpoint);
            storageService = new JingdongStorageService(new Credential(accessKey, secretKey), config);
        } catch (Exception e) {
            logger.error("JSS存储创建服务客户端链接失败", e);
            throw new RuntimeException("JSS存储创建服务客户端链接失败", e);
        } finally {
            lock.unlock();
        }
    }

    public JingdongStorageService getStorageService() {
        if (storageService == null) {
            this.init();
        }
        return storageService;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
