package com.jd.bluedragon.distribution.third.service;

import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: ThirdBoxDetailService
 * @Description: 三方装箱明细表--Service接口
 * @author wuyoude
 * @date 2020年01月07日 16:34:04
 *
 */
public interface ThirdBoxDetailService extends Service<ThirdBoxDetail> {

    /**
     * 建立箱-包关系
     * @param detail 箱-包关系
     * @return 结果
     */
    boolean sorting(ThirdBoxDetail detail);

    /**
     * 取消某一包裹的绑定关系
     * @param tenantCode 租户编码
     * @param startSiteId 操作场地
     * @param boxCode 箱号
     * @param packageCode 包裹号
     * @return 结果
     */
    boolean cancel(String tenantCode, Integer startSiteId, String boxCode, String packageCode);

    /**
     * 查询箱子明细
     * @param tenantCode 租户编码
     * @param startSiteId 始发站点
     * @param boxCode 箱号
     * @return 结果集
     */
    List<ThirdBoxDetail> queryByBoxCode(String tenantCode, Integer startSiteId, String boxCode);
}
