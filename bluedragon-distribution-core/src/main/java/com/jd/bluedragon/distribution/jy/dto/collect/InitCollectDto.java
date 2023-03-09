package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class InitCollectDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 操作时间
     */
    private Long operateTime;
    /**
     * 初始化时机： InitCollectNodeEnum
     */
    private Integer operateNode;


    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

}

