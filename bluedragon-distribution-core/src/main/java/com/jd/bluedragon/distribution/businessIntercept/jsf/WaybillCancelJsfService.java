package com.jd.bluedragon.distribution.businessIntercept.jsf;

import com.jd.bluedragon.distribution.intercept.WaybillCancelService;
import com.jd.dms.java.utils.sdk.base.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运单拦截接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-02-10 16:38:54 周五
 */
@Service("dmsWaybillCancelJsfService")
public class WaybillCancelJsfService implements WaybillCancelService {

    @Autowired
    private com.jd.bluedragon.distribution.waybill.service.WaybillCancelService  waybillCancelService;

    /**
     * 删除异常运单拦截
     *
     * @param waybillCodeList 运单单号列表
     * @return 处理结果
     * @author fanggang7
     * @time 2023-02-10 17:38:52 周五
     */
    @Override
    public Result<Integer> delByWaybillCodeListInterceptType99(List<String> waybillCodeList) {
        return waybillCancelService.delByWaybillCodeListInterceptType99(waybillCodeList);
    }
}
