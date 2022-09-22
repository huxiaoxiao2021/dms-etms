package com.jd.bluedragon.distribution.labelPrint.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.label.domain.farmar.FarmarEntity;
import com.jd.bluedragon.distribution.labelPrint.domain.farmar.FarmarPrintEntity;
import com.jd.bluedragon.distribution.labelPrint.domain.farmar.FarmarPrintRequest;

/**
 * 标签打印服务
 *
 * @author hujiping
 * @date 2022/8/22 4:40 PM
 */
public interface LabelPrintService {

    /**
     * 砝码打印处理
     *
     * @param farmarPrintRequest
     * @return
     */
    InvokeResult<FarmarPrintEntity> farmarPrintDeal(FarmarPrintRequest farmarPrintRequest);

    /**
     * 根据砝码编码获取砝码标签信息
     *
     * @param farmarCode
     * @return
     */
    InvokeResult<FarmarEntity> getFarmarInfo(String farmarCode);
}
