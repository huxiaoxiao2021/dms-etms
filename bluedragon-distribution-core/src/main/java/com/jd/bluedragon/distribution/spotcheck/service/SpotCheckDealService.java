package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckContext;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckResult;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.InputStream;
import java.util.List;

/**
 * 抽检处理接口
 *
 * @author hujiping
 * @date 2021/8/10 11:12 上午
 */
public interface SpotCheckDealService {

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
     * 运单是否操作过抽检
     *
     * @param waybillCode
     * @return
     */
    boolean checkIsHasSpotCheck(String waybillCode);

    /**
     * 运单是否超标：只从redis查询
     *
     * @param waybillCode
     * @return
     */
    boolean checkIsExcessFromRedis(String waybillCode);

    /**
     * 获取已抽检包裹号
     *
     * @param waybillCode
     * @param siteCode
     * @return
     */
    String spotCheckPackSetStr(String waybillCode, Integer siteCode);

    /**
     * 处理上传图片
     *
     * @param packageCode
     * @param siteCode
     * @param url
     */
    void dealPictureUrl(String packageCode, Integer siteCode, String url);

    /**
     * 是否执行抽检改造模式
     *
     * @param siteCode
     * @return
     */
    boolean isExecuteSpotCheckReform(Integer siteCode);

    /**
     * 是否开启 设备抽检AI图片识别
     *
     * @param siteCode
     * @return
     */
    boolean isExecuteDwsAIDistinguish(Integer siteCode);

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

    void brushSpotCheck(List<WeightVolumeSpotCheckDto> list);
}
