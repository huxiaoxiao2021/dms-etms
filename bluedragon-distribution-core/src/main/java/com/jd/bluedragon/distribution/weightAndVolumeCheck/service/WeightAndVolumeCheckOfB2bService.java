package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bWaybill;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 15:06
 */
public interface WeightAndVolumeCheckOfB2bService {

    /**
     * 检验是否超标（运单维度）
     * @param condition
     * @return
     */
    InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> checkIsExcessOfWaybill(WeightVolumeCheckConditionB2b condition);

    /**
     * 处理超标数据（运单维度）
     * @param param
     * @return
     */
    InvokeResult<String> dealExcessDataOfWaybill(WeightVolumeCheckConditionB2b param);

    /**
     * 上传超标图片
     * @param image
     * @param request
     * @return
     */
    InvokeResult<String> uploadExcessPicture(MultipartFile image, HttpServletRequest request);

    /**
     * PDA判断记录是否存在
     *
     * @param waybillCode
     * @return
     */
    InvokeResult<String> checkRecordExist(String waybillCode, Integer siteCode);
}
