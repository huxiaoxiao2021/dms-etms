package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.receive.domain.AbnormalPictureMq;

import java.io.InputStream;

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
     * @return
     */
    InvokeResult<String> searchExcessPicture(String packageCode);

    /**
     * 上传异常照片的同时给判责发消息
     * @param abnormalPictureMq
     */
    void sendMqToPanZe(AbnormalPictureMq abnormalPictureMq);
}
