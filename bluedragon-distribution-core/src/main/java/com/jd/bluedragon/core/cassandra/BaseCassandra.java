package com.jd.bluedragon.core.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LoggingRetryPolicy;
import com.datastax.driver.core.policies.Policies;
import com.datastax.driver.core.policies.TokenAwarePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseCassandra {

    private static final Logger log = LoggerFactory.getLogger(BaseCassandra.class);

    private Cluster cluster;

    private int connectTimeoutMillis;
    private int readTimeoutMillis;
    private String userName;
    private String password;
    private String node[];
    private int maxConnectPerHost;
    private int coreConnectPerHost;
    private int heartbeatIntervalSeconds;
    private int newNodeDelaySeconds;
    private int nonBlockingExecutorSize;
    private int notIfLockTimeoutSeconds;
    private String dataCenter;

    public BaseCassandra(String nodes[],String userName,String password,int timeout) {

        cluster = Cluster.builder().addContactPoints(nodes)
                 .withCredentials(userName, password)
                .withRetryPolicy(new LoggingRetryPolicy(Policies.defaultRetryPolicy()))
                .withSocketOptions(new SocketOptions().setKeepAlive(true).setReadTimeoutMillis(timeout))
                .build();

        log.info("cluster name:{}",cluster.getClusterName());
    }
    public BaseCassandra() {
    }
    public void init() {
//        System.setProperty("com.datastax.driver.NEW_NODE_DELAY_SECONDS",newNodeDelaySeconds+"");
//        System.setProperty("com.datastax.driver.NON_BLOCKING_EXECUTOR_SIZE",nonBlockingExecutorSize+"");
//        System.setProperty("com.datastax.driver.NOTIF_LOCK_TIMEOUT_SECONDS",notIfLockTimeoutSeconds+"");

        cluster = Cluster.builder().addContactPoints(node)
                .withCredentials(userName, password)
                .withRetryPolicy(new LoggingRetryPolicy(Policies.defaultRetryPolicy()))
                .withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
                .withSocketOptions(new SocketOptions().setKeepAlive(true).setReadTimeoutMillis(readTimeoutMillis).setConnectTimeoutMillis(connectTimeoutMillis))
                .withPoolingOptions(new PoolingOptions().setMaxConnectionsPerHost(HostDistance.LOCAL, maxConnectPerHost) .setCoreConnectionsPerHost(HostDistance.LOCAL, coreConnectPerHost)
                .setHeartbeatIntervalSeconds(heartbeatIntervalSeconds))
                //.withReconnectionPolicy(new ConstantReconnectionPolicy(1000L))
                .withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy(dataCenter)))
                .build();

        log.info("cluster name:{}",cluster.getClusterName());
    }

    public Session getSession(String keyspace){
        return cluster.connect(keyspace);
    }

    public Cluster getCluster() {
        return cluster;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getNode() {
        return node;
    }

    public void setNode(String[] node) {
        this.node = node;
    }

    public int getMaxConnectPerHost() {
        return maxConnectPerHost;
    }

    public void setMaxConnectPerHost(int maxConnectPerHost) {
        this.maxConnectPerHost = maxConnectPerHost;
    }

    public int getHeartbeatIntervalSeconds() {
        return heartbeatIntervalSeconds;
    }

    public void setHeartbeatIntervalSeconds(int heartbeatIntervalSeconds) {
        this.heartbeatIntervalSeconds = heartbeatIntervalSeconds;
    }

    public int getNewNodeDelaySeconds() {
        return newNodeDelaySeconds;
    }

    public void setNewNodeDelaySeconds(int newNodeDelaySeconds) {
        this.newNodeDelaySeconds = newNodeDelaySeconds;
    }

    public int getNonBlockingExecutorSize() {
        return nonBlockingExecutorSize;
    }

    public void setNonBlockingExecutorSize(int nonBlockingExecutorSize) {
        this.nonBlockingExecutorSize = nonBlockingExecutorSize;
    }

    public int getNotIfLockTimeoutSeconds() {
        return notIfLockTimeoutSeconds;
    }

    public void setNotIfLockTimeoutSeconds(int notIfLockTimeoutSeconds) {
        this.notIfLockTimeoutSeconds = notIfLockTimeoutSeconds;
    }

    public int getCoreConnectPerHost() {
        return coreConnectPerHost;
    }

    public void setCoreConnectPerHost(int coreConnectPerHost) {
        this.coreConnectPerHost = coreConnectPerHost;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    public void close() {

        cluster.close();
    }

}
