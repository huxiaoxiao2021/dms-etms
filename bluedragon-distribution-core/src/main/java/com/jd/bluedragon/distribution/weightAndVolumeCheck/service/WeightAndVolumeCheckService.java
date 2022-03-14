package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightAndVolumeCheckHandleMessage;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightVolumePictureDto;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;

import java.io.BufferedWriter;
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
     * 查看超标图片（C网）
     * @param packageCode
     * @param siteCode
     * @return
     */
    InvokeResult<String> searchExcessPicture(String packageCode,Integer siteCode);

    /**
     * 查看超标图片（一单多件）
     * @return 图片列表
     */
    InvokeResult<Pager<WeightVolumePictureDto>> searchPicture4MultiplePackage(Pager<WeightVolumeQueryCondition> weightVolumeQueryConditionPager);

    /**
     * 查看超标图片（B网）
     * @param packageCode
     * @param siteCode
     * @return
     */
    InvokeResult<List<String>> searchExcessPictureOfB2b(String packageCode, Integer siteCode);

    /**
     * 根据前缀获取最近上传的图片
     * @param prefixName
     * @return
     */
    String searchPictureUrlRecent(String prefixName);

    /**
     * 根据前缀获取最近上传的图片
     * @param prefixName
     * @return
     */
    String searchPictureUrlRecent(String prefixName, Integer maxKeys);

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<WeightVolumeCollectDto> queryByCondition(WeightAndVolumeCheckCondition condition);

    void setProductType(WeightVolumeCollectDto weightVolumeCollectDto, Waybill  waybill);

    /**
     * 处理消费称重抽检处理消息
     * @param weightAndVolumeCheckHandleMessage 消息体
     * @return 处理结果
     * @author fanggang7
     * @time 2020-08-25 10:08:37 周二
     */
    InvokeResult<Boolean> handleAfterUploadImgMessageOrAfterSend(WeightAndVolumeCheckHandleMessage weightAndVolumeCheckHandleMessage);

    /**
     * 导出
     * @param condition
     * @param innerBfw
     */
    void export(WeightAndVolumeCheckCondition condition, BufferedWriter innerBfw);

}
