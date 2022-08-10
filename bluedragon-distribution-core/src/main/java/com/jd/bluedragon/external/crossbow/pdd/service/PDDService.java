package com.jd.bluedragon.external.crossbow.pdd.service;

import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;

/**
 * <p>
 *     拼多多网络对接接口的service层处理类，包含处理拼多多接口的多种方法，目前分拣只对接了一个，以后有拼多多的对接需求，请加到类中
 *
 *     目前对接接口
 *     1. 拼多多面单信息获取接口地址：
 *          测试环境：http://express-channel-api.test.yiran.com/waybill/detail/query
 *          正式环境：http://express.pinduoduo.com/waybill/detail/query
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
public interface PDDService {

    /**
     * 查询pdd信息，先从运单中查，再从cache中查，最后从pdd接口中差。同时记录查询来源
     * @param waybillCode pdd单号
     * @param source
     * @return
     */
    PDDResponse<PDDWaybillDetailDto> queryPDDWaybillInfoByWaybillCodeWithCacheAndSource(String waybillCode, String source, Boolean cacheSwitch, Boolean waybillSwitch);
}
