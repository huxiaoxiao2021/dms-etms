package com.jd.bluedragon.distribution.jy.dto.violentSorting;

import lombok.Data;

import java.io.Serializable;

/**
 * {
 * "createTime":1698226363000,
 * "id":47216,
 * "deviceNo": "xxxxx",
 * "nationalChannelCode":"xxxxx",
 * "deviceName":"xxxx",
 * "url":"http://storage.jd.local/xxxx",
 * "gridBusinessKey":"xxxx"
 * }
 */
@Data
public class ViolentSortingDto implements Serializable {
    private Long createTime;// 创建时间
    private Integer id; //暴力分拣id ，与 fdm_jdl_exp_wl_ai_cv_pro_gai_fj_violent_chain 对应
    private String deviceNo;// 摄像头编号
    private String nationalChannelCode;//通道编号
    private String deviceName;// 摄像头名称
    private String url;// 视频地址
    private String gridBusinessKey; //网格业务主键

    // 下面的字段根据 gridBusinessKey 获取
    String areaHubCode;
    String areaHubName;
    String provinceAgencyCode;
    String provinceAgencyName;
    Integer siteCode;
    String siteName;
    String gridCode;
    String gridName;
    String ownerUserErp;
    String andonMachineCode;
    Boolean isUpgradeNotify;// 是否升级提醒
}
