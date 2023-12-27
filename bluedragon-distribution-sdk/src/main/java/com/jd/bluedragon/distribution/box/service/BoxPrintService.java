package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.request.box.BoxPrintReq;
import com.jd.bluedragon.distribution.api.request.box.CreateBoxReq;
import com.jd.bluedragon.distribution.api.response.box.BoxPrintInfo;
import com.jd.bluedragon.distribution.api.response.box.CreateBoxInfo;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 箱号打印相关服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-11-01 15:21:21 周三
 */
public interface BoxPrintService {

    /**
     * 创建箱号
     *
     * @param createBoxReq 创建箱号入参
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    Result<CreateBoxInfo> createBoxPrintInfo(CreateBoxReq createBoxReq);

    /**
     * 获取箱号打印数据
     *
     * @param boxPrintReq 箱号打印入参
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    Result<BoxPrintInfo> getBoxPrintInfo(BoxPrintReq boxPrintReq);
}
