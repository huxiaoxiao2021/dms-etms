package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.SpotCheckSourceEnum;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;

import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: WeightAndVolumeCheckService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/22 17:56
 */
public interface WeightAndVolumeCheckService {


    /**
     * 上传超标图片
     * @param imageName
     * @param imageSize
     * @param inputStream
     * @return
     */
    void uploadExcessPicture(String imageName, long imageSize, InputStream inputStream) throws Exception;

    /**
     * 查看
     * @param waybillCode
     * @param siteCode
     * @param isWaybillSpotCheck
     * @return
     */
    InvokeResult<String> searchPicture(String waybillCode,Integer siteCode,Integer isWaybillSpotCheck);

    /**
     * 查看超标图片（C网）
     * @param packageCode
     * @param siteCode
     * @return
     */
    InvokeResult<String> searchExcessPicture(String packageCode,Integer siteCode);

    /**
     * 查看超标图片（B网）
     * @param packageCode
     * @param siteCode
     * @return
     */
    InvokeResult<List<String>> searchExcessPictureOfB2b(String packageCode, Integer siteCode);

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<WeightVolumeCollectDto> queryByCondition(WeightAndVolumeCheckCondition condition);

    /**
     * 导出
     * @param condition
     * @return
     */
    List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition);

    /**
     * 发消息并更新
     * @param packageCode
     * @param siteCode
     * @deprecated 业务逻辑变更
     * （1）A分拣可操作多次抽检，每次正常发送全程跟踪称重量方日志，
     * （2）A分拣如果多次抽检，则统计系统覆盖记录最新的一条抽检数据，只记录暂时先不下发
     * （3）当包裹号操作【发货】扫描节点时，则触发将最新的一条超标抽检数据进行下发，最新的一条如果未超标则不下发。
     */
    void sendMqAndUpdate(String packageCode, Integer siteCode);

    /**
     * 称重体积数据处理
     * @param packWeightVO
     * @param spotCheckSourceEnum
     * @param result
     */
    InvokeResult<Boolean> dealSportCheck(PackWeightVO packWeightVO, SpotCheckSourceEnum spotCheckSourceEnum,InvokeResult<Boolean> result);

    void setProductType(WeightVolumeCollectDto weightVolumeCollectDto);

    /**
     * 查询最新一条抽检记录数据
     * @param query 查询条件
     * @return 抽检记录
     * @author fanggang7
     * @time 2020-08-24 17:12:55 周一
     */
    InvokeResult<WeightVolumeCollectDto> queryLatestCheckRecord(WeightVolumeQueryCondition query);

    /**
     * 处理消费称重抽检处理消息
     * @param weightAndVolumeCheckHandleMessage 消息体
     * @return 处理结果
     * @author fanggang7
     * @time 2020-08-25 10:08:37 周二
     */
    InvokeResult<Boolean> handleAfterUploadImgMessageOrAfterSend(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage);
}
