package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

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
     * 查看超标图片
     * @param packageCode
     * @param siteCode
     * @return
     */
    InvokeResult<String> searchExcessPicture(String packageCode,Integer siteCode);

    /**
     * 上传异常照片的同时给判责发消息
     * @param abnormalPictureMq
     * @param siteCode
     */
    void sendMqToPanZe(AbnormalPictureMq abnormalPictureMq,Integer siteCode);

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<WeightAndVolumeCheck> queryByCondition(WeightAndVolumeCheckCondition condition);

    /**
     * 导出
     * @param condition
     * @return
     */
    List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition);
}
