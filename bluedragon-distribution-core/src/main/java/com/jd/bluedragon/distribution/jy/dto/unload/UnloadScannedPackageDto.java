package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 已扫描的包裹
 */
@Data
public class UnloadScannedPackageDto  {
    // 包裹号
    private String packageCode;

    // 运单号
    private String waybillCode;

    // 箱号
    private String boxCode;

    // 封车编码
    private String sealCarCode;

    //路由信息
    private String routing;

    //扫描数据来源：APP、OTHER
    private String source;

    // 条码类型：PACKAGE、WAYBILL、BOX
    private String barCodeType;

    // 是否无任务卸车；1：是：0：否
    private Integer manualCreatedFlag;

    // 产品类型
    private String productType;

    // 是否拦截 0不拦截; 1: 拦截
    private Integer interceptFlag;

    // 是否多扫： 0非多扫; 1多扫
    private Integer moreScanFlag;

    // 是否已扫： 0未扫; 1已扫
    private Integer scannedFlag;

    // 是否本场地 0非本场地; 1本场地
    private Integer localSiteFlag;

    // 对冲 NONE 类型的产品 统计数据
    private Integer removeAggFlag;

    // 操作人 用户编码
    private String operatorCode;

    private String operatorName;

    // 操作人erp
    private String operatorErp;

    // 操作时间
    private Long operatorTime;

    // 操作场地
    private Integer operateSiteId;

    // 业务主键
    private String bizId;

    // 任务ID
    private String taskId;

    // 车牌号
    private String vehicleNumber;

    // 目的站点ID
    private Integer endSiteId;

    // 是否应扫 0不; 1: 应扫
    private Integer shouldScanFlag;


    // 应卸包裹总数--用于计算总进度
    private Integer totalSealPackageCount;

    @JSONField(serialize=false)
    public String getDocId() {
        return (bizId == null ? this.sealCarCode : bizId) + "|" + this.operateSiteId + "|" + this.packageCode;
    }

    @JSONField(serialize=false)
    public String getKey() {
        return this.operateSiteId + "|" + this.packageCode;
    }
}
