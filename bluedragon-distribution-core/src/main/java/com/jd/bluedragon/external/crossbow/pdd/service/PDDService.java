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
     * 拼多多面单打印接口的逻辑处理类
     * @param waybillCode 拼多多电子面单号
     * @return 返回拼多多电子面单处理对象
     */
    PDDWaybillDetailDto queryWaybillDetailByWaybillCode(String waybillCode);

    /**
     * 拼多多面单打印接口的逻辑处理类,包含pdd返回值
     * @param waybillCode 拼多多电子面单号
     * @return 返回拼多多电子面单处理对象
     */
    PDDResponse<PDDWaybillDetailDto> queryPDDWaybillByWaybillCode(String waybillCode);
}
