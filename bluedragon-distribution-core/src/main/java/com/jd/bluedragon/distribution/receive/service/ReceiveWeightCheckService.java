package com.jd.bluedragon.distribution.receive.service;

import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 17:54
 */
public interface ReceiveWeightCheckService {

    /**
     * 新增一条记录
     * */
    public int insert(ReceiveWeightCheckResult receiveWeightCheckResult);

    /**
     * 按条件查询
     * */
    public PagerResult<ReceiveWeightCheckResult> queryByCondition(ReceiveWeightCheckCondition condition);

    /**
     * 根据条件查询数据条数
     * */
    public Integer queryNumByCondition(ReceiveWeightCheckCondition condition);

    /**
     * 整理导出数据
     * */
    public List<List<Object>> getExportData(ReceiveWeightCheckCondition condition);

    /**
     * 判断换单新单运单是否存
     * @param oldWaybillCode 老运单号
     * @param newWaybillCode 新运单号
     * @return
     */
    public InvokeResult<Integer> waybillExchangeCheckWeightAndVolume( String oldWaybillCode, String newWaybillCode);

    /**
     * 逆向换单打印业务校验称重和量方
     * @param oldWaybillCode 换单之前的旧单号
     * @param newWaybillCode 换单之后的新单号
     * @param eightVolumeOperEnable 是否启用包裹称重和量方 {@see WaybillPrintRequest#weightVolumeOperEnable}
     * @param weightOperFlow 包裹称重和量方数据 {@see WaybillPrintRequest#weightOperFlow}
     * @param isHalfPackage 是否包裹半收，（符合原单号的waybillStatus == 600）
     * @return
     */
    InterceptResult<String> reverseChangePrintCheckWeightAndVolume(String oldWaybillCode, String newWaybillCode,
                                       int eightVolumeOperEnable, WeightOperFlow weightOperFlow, boolean isHalfPackage);
}
