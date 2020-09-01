package com.jd.bluedragon.common.dto.search.request;

import java.io.Serializable;

/**
 * 获取包裹、箱信息入参对象
 */
public class PackBoxRequest  implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 操作站点ID
     */
    private Integer createSiteCode;

    /**
     * 包裹/运单号
     */
    private String waybillNoOrPackNo;

    /**
     *分拣类型 10：正向 20：逆向 30：三方
     */
    private Integer type;

    /**
     * 查询状态码 见枚举 ExpressStatusTypeEnum
     * HAS_INSPECTION("0", "已验货"),
     HAS_SORTING("1", "已分拣"),
     HAS_INSPECTION_OR_HAS_SORTING("1,0", "已验货已分拣"),
     HAS_SORTING_OR_HAS_INSPECTION("0,1", "已分拣已验货"),
     HAS_SEND("2", "已发货");
     */
    private String statusQueryCode;

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getWaybillNoOrPackNo() {
        return waybillNoOrPackNo;
    }

    public void setWaybillNoOrPackNo(String waybillNoOrPackNo) {
        this.waybillNoOrPackNo = waybillNoOrPackNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getStatusQueryCode() {
        return statusQueryCode;
    }

    public void setStatusQueryCode(String statusQueryCode) {
        this.statusQueryCode = statusQueryCode;
    }
}
