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
     * @param detail 明细
     * @return 结果
     */
    boolean cancel(ThirdBoxDetail detail);

    /**
     * 查询箱子明细
     * 兼容上线关系表中存在重复数据问题 修改SQL去重逻辑 取最新数据
     * @param tenantCode 租户编码
     * @param startSiteId 始发站点
     * @param boxCode 箱号
     * @return 结果集
     */
    List<ThirdBoxDetail> queryByBoxCode(String tenantCode, Integer startSiteId, String boxCode);

    /**
     * 获取运单或包裹装箱数据
     * 为了走索引查询包裹时也需要传入运单号
     * @param tenantCode
     * @param waybillCode
     * @param packageCode
     * @return
     */
    List<ThirdBoxDetail> queryByWaybillOrPackage(String tenantCode, String waybillCode, String packageCode);


    /**
     * 检查是否存在数据
     * @param tenantCode
     * @param startSiteId
     * @param boxCode
     * @return
     */
    boolean isExist(String tenantCode, Integer startSiteId, String boxCode);
}
