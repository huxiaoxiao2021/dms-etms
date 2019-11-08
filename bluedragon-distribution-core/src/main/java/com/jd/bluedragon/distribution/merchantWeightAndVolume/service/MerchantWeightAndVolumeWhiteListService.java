package com.jd.bluedragon.distribution.merchantWeightAndVolume.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/11/5 14:09
 */
public interface MerchantWeightAndVolumeWhiteListService {

    /**
     * 查询
     * @param condition
     * @return*/
    PagerResult<MerchantWeightAndVolumeDetail> queryByCondition(MerchantWeightAndVolumeCondition condition);

    /**
     * 删除记录
     * @param detail
     * @return*/
    InvokeResult<Integer> delete(MerchantWeightAndVolumeDetail detail);

    /**
     * 校验导入数据
     * @param dataList
     * @param importErpCode
     * @return*/
    String checkExportData(List<MerchantWeightAndVolumeDetail> dataList, String importErpCode);

    /**
     * 获取所有数据
     * @param condition
     * @return*/
    List<List<Object>> getExportData(MerchantWeightAndVolumeCondition condition);


    /**
     * 判断是否存在
     * @param detail
     * @return
     */
    Boolean isExist(MerchantWeightAndVolumeDetail detail);
}
