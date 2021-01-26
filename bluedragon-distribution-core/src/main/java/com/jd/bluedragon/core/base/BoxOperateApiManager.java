package com.jd.bluedragon.core.base;

import com.zhongyouex.order.api.dto.BoxDetailResultDto;

/**
 * 众邮相关接口
 */
public interface BoxOperateApiManager {

    /**
     * 判断箱是否空箱
     * @param boxCode
     * @return
     */
    public boolean findBoxIsEmpty(String boxCode);

    /**
     * 获取箱包明细
     * @param boxCode
     * @return
     */
    public BoxDetailResultDto findBoxDetailInfoList(String boxCode);
}
