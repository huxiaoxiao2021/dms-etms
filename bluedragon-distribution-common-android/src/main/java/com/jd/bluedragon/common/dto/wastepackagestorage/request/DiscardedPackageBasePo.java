package com.jd.bluedragon.common.dto.wastepackagestorage.request;

import com.jd.bluedragon.common.dto.base.request.BaseRequest;

import java.io.Serializable;

/**
 * 弃件暂存基础类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 20:46:04 周四
 */
public class DiscardedPackageBasePo extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 4519290326774661464L;

    /**
     * 场地部门类型
     */
    private Integer siteDepartType;

    public DiscardedPackageBasePo() {
    }

    public Integer getSiteDepartType() {
        return siteDepartType;
    }

    public DiscardedPackageBasePo setSiteDepartType(Integer siteDepartType) {
        this.siteDepartType = siteDepartType;
        return this;
    }
}
