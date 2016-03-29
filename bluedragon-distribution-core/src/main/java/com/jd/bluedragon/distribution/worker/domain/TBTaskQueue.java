package com.jd.bluedragon.distribution.worker.domain;
import java.io.Serializable;
import java.util.Date;
/**
 * Created by wangtingwei on 2015/10/13.
 */
public class TBTaskQueue implements Serializable {

    private static final long serialVersionUID=1L;

    private int id;
    private String taskType;
    private int queueId;
    private String ownSign;
    private String baseTaskType;
    private String curServer;
    private String reqServer;
    private Date gmtCreate;
    private Date gmtModified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TBTaskQueue that = (TBTaskQueue) o;

        if (id != that.id) return false;
        if (queueId != that.queueId) return false;
        if (baseTaskType != null ? !baseTaskType.equals(that.baseTaskType) : that.baseTaskType != null) return false;
        if (curServer != null ? !curServer.equals(that.curServer) : that.curServer != null) return false;
        if (gmtCreate != null ? !gmtCreate.equals(that.gmtCreate) : that.gmtCreate != null) return false;
        if (gmtModified != null ? !gmtModified.equals(that.gmtModified) : that.gmtModified != null) return false;
        if (ownSign != null ? !ownSign.equals(that.ownSign) : that.ownSign != null) return false;
        if (reqServer != null ? !reqServer.equals(that.reqServer) : that.reqServer != null) return false;
        if (taskType != null ? !taskType.equals(that.taskType) : that.taskType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (taskType != null ? taskType.hashCode() : 0);
        result = 31 * result + queueId;
        result = 31 * result + (ownSign != null ? ownSign.hashCode() : 0);
        result = 31 * result + (baseTaskType != null ? baseTaskType.hashCode() : 0);
        result = 31 * result + (curServer != null ? curServer.hashCode() : 0);
        result = 31 * result + (reqServer != null ? reqServer.hashCode() : 0);
        result = 31 * result + (gmtCreate != null ? gmtCreate.hashCode() : 0);
        result = 31 * result + (gmtModified != null ? gmtModified.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public String getOwnSign() {
        return ownSign;
    }

    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }

    public String getBaseTaskType() {
        return baseTaskType;
    }

    public void setBaseTaskType(String baseTaskType) {
        this.baseTaskType = baseTaskType;
    }

    public String getCurServer() {
        return curServer;
    }

    public void setCurServer(String curServer) {
        this.curServer = curServer;
    }

    public String getReqServer() {
        return reqServer;
    }

    public void setReqServer(String reqServer) {
        this.reqServer = reqServer;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
