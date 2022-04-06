package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckContext;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.InputStream;
import java.util.Map;

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
     * 运单是否超标
     *
     * @param waybillCode
     * @param siteCode
     * @return
     */
    boolean checkIsExcess(String waybillCode, Integer siteCode);

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

    /**
     * 是否执行抽检改造模式
     *
     * @param siteCode
     * @return
     */
    boolean isExecuteSpotCheckReform(Integer siteCode);

    /**
     * 单个图片识别
     *
     * @param waybillCode
     * @param weight
     * @param picUrl
     * @param uploadPicType SpotCheckPicTypeEnum
     * @param excessType 1：重量图片 2：面单图片
     * @return
     */
    ImmutablePair<Integer, String> singlePicAutoDistinguish(String waybillCode, Double weight, String picUrl, Integer uploadPicType, Integer excessType);

    /**
     * 校验超标
     *
     * @param spotCheckContext
     */
    InvokeResult<SpotCheckResult> checkIsExcessReform(SpotCheckContext spotCheckContext);

    /**
     * 组装核对数据
     *
     * @param spotCheckContext
     */
    void assembleContrastData(SpotCheckContext spotCheckContext);

    /**
     * 下发超标数据
     *
     * @param weightVolumeSpotCheckDto
     */
    void spotCheckIssue(WeightVolumeSpotCheckDto weightVolumeSpotCheckDto);

    /**
     * 执行下发
     *
     * @param weightVolumeSpotCheckDto
     */
    void executeIssue(WeightVolumeSpotCheckDto weightVolumeSpotCheckDto);

    /**
     * 上传图片
     *
     * @param originalFileName
     * @param inStream
     * @return
     */
    String uploadExcessPicture(String originalFileName, InputStream inStream);

    /**
     * 获取包裹图片缓存
     *
     * @param packageCode
     * @param siteCode
     * @return
     */
    String getSpotCheckPackUrlFromCache(String packageCode, Integer siteCode);
}
