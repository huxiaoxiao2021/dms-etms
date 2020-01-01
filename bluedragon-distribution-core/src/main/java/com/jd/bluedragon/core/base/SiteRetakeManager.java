package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.common.orm.page.Page;
import com.jd.etms.erp.service.domain.VendorOrder;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ql.erp.dto.vendor.ReTakeRequestDTO;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 驻厂再取
 * @date 2018年08月02日 14时:23分
 */
public interface SiteRetakeManager {
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
     * @param vendorOrder
     * @param page
     * @return
     */
    Page<VendorOrder> selectVendorOrderList(VendorOrder vendorOrder, Page page);



    /**
     * 批量更新揽收状态接口
     *
     * 返回失败数据
     * @return
     */
    List<String> batchUpdateReTakeTime(List<ReTakeRequestDTO> reTakeRequestDTOS);

}
