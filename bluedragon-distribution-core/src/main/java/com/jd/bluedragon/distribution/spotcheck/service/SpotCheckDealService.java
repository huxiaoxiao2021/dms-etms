package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckContext;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;

/**
 * 抽检处理接口
 *
 * @author hujiping
 * @date 2021/8/10 11:12 上午
 */
public interface SpotCheckDealService {

    /**
     * 组装抽检核对数据
     *  通过计费获取
     * @param spotCheckContext
     * @return
     */
    void assembleContrastDataFromFinance(SpotCheckContext spotCheckContext);

    /**
     * 组装抽检核对数据
     *  通过运单称重流水获取
     * @param spotCheckContext
     * @return
     */
    void assembleContrastDataFromWaybillFlow(SpotCheckContext spotCheckContext);

    /**
     * 获取产品类型
     * @param waybillSign
     * @return
     */
    DmsBaseDict getProductType(String waybillSign);

    /**
     * 是否执行新的抽检模式
     *
     * @param siteCode
     * @return
     */
    boolean isExecuteNewSpotCheck(Integer siteCode);

    /**
     * 是否集齐
     *
     * @param spotCheckContext
     * @return
     */
    boolean gatherTogether(SpotCheckContext spotCheckContext);

    /**
     * 发送抽检全程跟踪
     *
     * @param spotCheckContext
     */
    void sendWaybillTrace(SpotCheckContext spotCheckContext);

    /**
     * 记录抽检日志
     *
     * @param spotCheckContext
     */
    void recordSpotCheckLog(SpotCheckContext spotCheckContext);

    /**
     * 包裹是否已发货
     *
     * @param packageCode
     * @param siteCode
     * @return
     */
    boolean checkIsHasSend(String packageCode, Integer siteCode);

    /**
     * 运单是否操作过抽检
     *
     * @param waybillCode
     * @return
     */
    boolean checkIsHasSpotCheck(String waybillCode);

    /**
     * 获取已抽检包裹号
     *
     * @param waybillCode
     * @param siteCode
     * @return
     */
    String spotCheckPackSetStr(String waybillCode, Integer siteCode);

    /**
     * 校验包裹是否操作过抽检
     *
     * @param packageCode
     * @param siteCode
     * @return
     */
    boolean checkPackHasSpotCheck(String packageCode, Integer siteCode);

    /**
     * 下发超标数据
     *
     * @param weightVolumeCollectDto
     */
    void issueSpotCheckDetail(WeightVolumeCollectDto weightVolumeCollectDto);

    /**
     * 是否抽检数据给计费
     *
     * @param collectDto
     * @return
     */
    boolean isSueToFinance(WeightVolumeCollectDto collectDto);

    /**
     * 处理上传图片
     *
     * @param packageCode
     * @param siteCode
     * @param url
     */
    void dealPictureUrl(String packageCode, Integer siteCode, String url);

    /**
     * 执行新下发逻辑
     *
     * @param message
     * @return
     */
    InvokeResult<Boolean> executeNewHandleProcess(WeightAndVolumeCheckHandleMessage message);
}
