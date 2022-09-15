package com.jd.bluedragon.distribution.jy.dto.unload;


import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class JyUnloadCarTaskCompleteDto {
    private static final long serialVersionUID = 1L;

    /**
     * 主任务BizId
     */
    private String masterTaskBizId;
    /**
     * 主任taskId
     */
    private String masterTaskTaskId;
    /**
     * 子任务BizId
     */
    private String childTaskBizId;
    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 实操节点： 1-交班；2-任务完成
     */
    private Integer operateNode;

    /**
     * 操作时间
     */
    private Long operatorTime;

    /**
     * 操作人
     */
    private User user;
    /**
     * 操作场地
     */
    private CurrentOperate currentOperate;

    public String getMasterTaskBizId() {
        return masterTaskBizId;
    }

    public void setMasterTaskBizId(String masterTaskBizId) {
        this.masterTaskBizId = masterTaskBizId;
    }

    public String getChildTaskBizId() {
        return childTaskBizId;
    }

    public void setChildTaskBizId(String childTaskBizId) {
        this.childTaskBizId = childTaskBizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public Long getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Long operatorTime) {
        this.operatorTime = operatorTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getMasterTaskTaskId() {
        return masterTaskTaskId;
    }

    public void setMasterTaskTaskId(String masterTaskTaskId) {
        this.masterTaskTaskId = masterTaskTaskId;
    }
}
