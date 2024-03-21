package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

/**
 * 暴力分拣任务个性化信息
 */
public class ViolenceSortInfoData implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 任务详情页标题 **/
    private String title;
    /** 触发时间 格式化后MM/dd HH:mm:ss **/
    private String createTime;
    /** 设备名称 **/
    private String deviceName;
    
    /** 视频链接 **/
    private String url;

    /**
     * 判责流程号
     * @return
     */
    private String processInstanceId;

    /**
     * 对应网格业务主键，多个用英文逗号分割
     * @return
     */
    private String gridKeys;
    //暴力分拣id
    private Long id;
    /** 暴力触发时间 时间戳格式**/
    private Long operateTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getGridKeys() {
        return gridKeys;
    }

    public void setGridKeys(String gridKeys) {
        this.gridKeys = gridKeys;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
