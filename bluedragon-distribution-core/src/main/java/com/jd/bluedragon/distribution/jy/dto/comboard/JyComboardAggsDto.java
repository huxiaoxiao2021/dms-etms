package com.jd.bluedragon.distribution.jy.dto.comboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class JyComboardAggsDto extends JyAggsDto{

    /**
     * 始发
     */
    private Integer operateSiteId;
    /**
     * 目的
     */
    private Integer receiveSiteId;
    /**
     *  流向
     */
    private String sendFlow;
    /**
     * 业务id
     */
    private String bizId;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 扫描类型
     */
    private Integer scanType;
    /**
     * 已扫
     */
    private Integer scannedCount;
    /**
     * 组板数量
     */
    private Integer boardCount;
    /**
     * 多扫
     */
    private Integer moreScannedCount;
    /**
     * 拦截量
     */
    private Integer interceptCount;
    /**
     * 待扫
     */
    private Integer waitScanCount;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 体积
     */
    private BigDecimal volume;
    /**
     * 包含箱内包裹统计
     */
    private Integer packageTotalScannedCount;

    public String getSendFlow() {
        if (operateSiteId == null || receiveSiteId == null) {
            return null;
        }
        return operateSiteId + "-" + receiveSiteId;
    }

    public String getKey() {
        return  defaultStr(operateSiteId) + "-" +
                defaultStr(receiveSiteId) + "-" +
                defaultStr(bizId) + "-" +
                defaultStr(boardCode) + "-" +
                defaultStr(productType) + "-" +
                defaultStr(scanType);
    }

    private String defaultStr(String value){
        if (value == null) {
            return "-1";
        }
        return value;
    }

    private String defaultStr(Integer value){
        if (value == null) {
            return "-1";
        }
        return value+"";
    }

}
