package com.jd.bluedragon.distribution.siteRetake.service;

import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeCondition;
import com.jd.bluedragon.distribution.siteRetake.domain.SiteRetakeOperation;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年08月02日 14时:46分
 */
public interface SiteRetakeService {
    /**
     * 查询商家
     *
     * @param key
     * @return
     */
    List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key);

    /**
     * 查询揽收未分配列表
     *
     * @param siteRetakeCondition
     * @return
     */
    Page<VendorOrder> queryVendorOrderList(SiteRetakeCondition siteRetakeCondition, Page page);

    /**
     * 更新状态
     *
     * @param siteRetakeOperation
     * @return
     */
    JdResponse<String> updateCommonOrderStatus(SiteRetakeOperation siteRetakeOperation);
}
