package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.stock.iwms.export.param.ChuguanParam;

import java.util.List;
import java.util.Map;

/**
 * @author : xumigen
 * @date : 2019/9/27
 */
public interface ChuguanExportManager {


    long insertStockVirtualIntOut(List<ChuguanParam> chuguanParamList);

    /*******************方法扩展区*********************/
    KuGuanDomain queryByParams(Map<String, Object> paramMap);

    /**
     * 已经做了异常处理,只以运单号做库查询条件
     * @param waybillCode
     * @return 返回一个KuGuanDomain对象,如果查不到对应运单的库管对象,则返回null
     */
    public KuGuanDomain queryByWaybillCode(String waybillCode);
}
