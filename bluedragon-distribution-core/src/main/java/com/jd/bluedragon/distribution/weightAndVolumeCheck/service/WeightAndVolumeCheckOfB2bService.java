package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckConditionB2b;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightVolumeCheckOfB2bPackage;
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
     * 获取运单的包裹信息
     * @param waybillOrPackageCode
     * @return
     */
    InvokeResult<List<WeightVolumeCheckOfB2bPackage>> getPackageNum(String waybillOrPackageCode);

    /**
     * 检验是否超标（运单维度）
     * @param condition
     * @return
     */
    InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> checkIsExcessOfWaybill(WeightVolumeCheckConditionB2b condition);

    /**
     * 判断是否超标（包裹维度）
     * @param params
     * @return
     */
    InvokeResult<Integer> checkIsExcessOfPackage(List<WeightVolumeCheckOfB2bPackage> params);

    /**
     * 处理超标数据（运单维度）
     * @param params
     * @return
     */
    InvokeResult<String> dealExcessDataOfWaybill(List<WeightVolumeCheckConditionB2b> params);

    /**
     * 处理超标数据（包裹维度）
     * @param params
     * @return
     */
    InvokeResult<String> dealExcessDataOfPackage(List<WeightVolumeCheckOfB2bPackage> params);

    /**
     * 查看超标图片
     * @param packageCode
     * @param siteCode
     * @return
     */
    com.jd.bluedragon.distribution.base.domain.InvokeResult<List<String>> searchExcessPicture(String packageCode, Integer siteCode);

    /**
     * 上传超标图片
     * @param image
     * @param request
     * @return
     */
    com.jd.bluedragon.distribution.base.domain.InvokeResult uploadExcessPicture(MultipartFile image, HttpServletRequest request);

}
