package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;

public interface LoadCarTaskServiceWSManager {
    /**
     * 校验是否可上传图片
     * @param goodsLoadingReq
     * @return
     */
    JdCResponse<Boolean> uploadPhotoCheck(GoodsLoadingReq goodsLoadingReq);
}
