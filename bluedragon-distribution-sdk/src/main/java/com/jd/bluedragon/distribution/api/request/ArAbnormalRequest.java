package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:14分
 */
public class ArAbnormalRequest extends JdRequest {
    /**
     * 转发类型  10 航空转陆运 、20 航空转铁路 、30 铁路转航空
     */
    private  Integer transpondType;
    /**
     * 异常原因
     */
    private String transpondReason;

    /**
     * 包裹号、运单号、箱号、批次号
     */
    private String packageCode;

    public Integer getTranspondType() {
        return transpondType;
    }

    public void setTranspondType(Integer transpondType) {
        this.transpondType = transpondType;
    }

    public String getTranspondReason() {
        return transpondReason;
    }

    public void setTranspondReason(String transpondReason) {
        this.transpondReason = transpondReason;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
