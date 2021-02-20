package com.jd.bluedragon.core.base;

import com.zhongyouex.order.api.dto.BoxDetailInfoDto;
import com.zhongyouex.order.api.dto.BoxDetailResultDto;

import java.util.List;

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
    public List<BoxDetailInfoDto> findBoxDetailInfoList(String boxCode);
}
