package com.jd.bluedragon.distribution.jy.dto.work;

import lombok.Data;

import java.io.Serializable;

/**
 * 判责系统下发暴力分拣消息dto
 */
@Data
public class ViolentSortingMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //暴力分拣id ，与 fdm_jdl_exp_wl_ai_cv_pro_gai_fj_violent_chain 对应
    private Long id;
    // 摄像头编号
    private String deviceNo;
    //通道编号
    private String nationalChannelCode;
    // 摄像头名称
    private String deviceName;
    // 视频地址 
    private String url;
    //场地
    private Integer siteCode;
    
    //判责流程编号,现场确认责任后回传给判责系统
    private String processInstanceId;

    private Long createTime;// 创建时间
    
    
}
