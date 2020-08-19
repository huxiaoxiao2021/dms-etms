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
}
