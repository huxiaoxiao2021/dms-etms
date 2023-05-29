package com.jd.bluedragon.openplateform.send;

import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 发货后其他处理动作
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-26 17:41:03 周五
 */
public interface JyOpenSendExtraHandleService {

    /**
     * 发货后处理城配相关
     * @return 处理结果
     * @author fanggang7
     * @time 2023-05-26 17:42:19 周五
     */
    Result<Boolean> afterOpenPlatformSend(JYCargoOperateEntity jyCargoOperateDto);
}
