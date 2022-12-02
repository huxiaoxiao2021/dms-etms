package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.photo.GoodsPhotoInfoDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/2 15:56
 * @Description: 货物图片信息保存
 */
public interface GoodsPhotoInfoGatewayService {

    JdCResponse<Boolean> saveGoodsPhotoInfo(GoodsPhotoInfoDto dto);
}
